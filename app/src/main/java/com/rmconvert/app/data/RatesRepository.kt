package com.rmconvert.app.data

import android.content.Context
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

sealed interface RatesUiState {
    data object Loading : RatesUiState
    data class Success(
        /** currency code -> MYR value of one unit of that currency. Always includes "MYR" -> 1.0 */
        val ratesByCurrency: Map<String, Double>,
        val asOf: String,
        val isFromCache: Boolean
    ) : RatesUiState
    data class Error(val message: String) : RatesUiState
}

/**
 * Fetches daily reference exchange rates published by Bank Negara Malaysia
 * (https://api.bnm.gov.my/public/exchange-rate). Every rate is MYR-per-foreign-unit,
 * so any two currencies are converted by triangulating through MYR. Falls back to the
 * last successfully fetched rates (cached on-device) when there is no network.
 */
class RatesRepository(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("rmconvert_rates_cache", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient()

    private val _state = MutableStateFlow<RatesUiState>(RatesUiState.Loading)
    val state: StateFlow<RatesUiState> = _state.asStateFlow()

    suspend fun refresh() {
        _state.value = RatesUiState.Loading
        withContext(Dispatchers.IO) {
            try {
                val rawJson = fetchRaw()
                val rates = parseRates(rawJson)
                val fetchedAt = System.currentTimeMillis()
                cache(rawJson, fetchedAt)
                _state.value = RatesUiState.Success(rates, formatTimestamp(fetchedAt), isFromCache = false)
            } catch (e: Exception) {
                val cached = loadCache()
                _state.value = if (cached != null) {
                    RatesUiState.Success(cached.first, formatTimestamp(cached.second), isFromCache = true)
                } else {
                    RatesUiState.Error(e.message ?: "Unable to fetch exchange rates")
                }
            }
        }
    }

    private fun fetchRaw(): String {
        val request = Request.Builder()
            .url("https://api.bnm.gov.my/public/exchange-rate")
            .header("Accept", "application/vnd.BNM.API.v1+json")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("BNM API returned HTTP ${response.code}")
            return response.body?.string() ?: throw IOException("Empty response body")
        }
    }

    private fun parseRates(rawJson: String): Map<String, Double> {
        val envelope = json.decodeFromString(BnmRateEnvelope.serializer(), rawJson)
        return envelope.data
            .mapNotNull { rate -> rate.myrPerForeignUnit()?.let { rate.currencyCode to it } }
            .toMap() + ("MYR" to 1.0)
    }

    private fun cache(rawJson: String, fetchedAt: Long) {
        prefs.edit()
            .putString(KEY_JSON, rawJson)
            .putLong(KEY_TIMESTAMP, fetchedAt)
            .apply()
    }

    private fun loadCache(): Pair<Map<String, Double>, Long>? {
        val rawJson = prefs.getString(KEY_JSON, null) ?: return null
        val fetchedAt = prefs.getLong(KEY_TIMESTAMP, 0L)
        if (fetchedAt == 0L) return null
        return try {
            parseRates(rawJson) to fetchedAt
        } catch (e: Exception) {
            null
        }
    }

    private fun formatTimestamp(epochMillis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a")
        return formatter.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()))
    }

    companion object {
        private const val KEY_JSON = "raw_json"
        private const val KEY_TIMESTAMP = "fetched_at"

        @Volatile private var instance: RatesRepository? = null

        fun getInstance(context: Context): RatesRepository =
            instance ?: synchronized(this) {
                instance ?: RatesRepository(context).also { instance = it }
            }
    }
}
