package com.rmconvert.app.ui.converter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rmconvert.app.data.BNM_QUOTED_COUNTRIES
import com.rmconvert.app.data.Country
import com.rmconvert.app.data.MALAYSIA
import com.rmconvert.app.data.RatesRepository
import com.rmconvert.app.data.RatesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConverterUiState(
    val fromCountry: Country = MALAYSIA,
    val toCountry: Country = BNM_QUOTED_COUNTRIES.first { it.currencyCode == "SGD" },
    val amountText: String = "100",
    val ratesState: RatesUiState = RatesUiState.Loading
) {
    private val rates: Map<String, Double>?
        get() = (ratesState as? RatesUiState.Success)?.ratesByCurrency

    val resultText: String
        get() {
            val rates = rates ?: return "--"
            val amount = amountText.toDoubleOrNull() ?: return "--"
            val fromRate = rates[fromCountry.currencyCode] ?: return "--"
            val toRate = rates[toCountry.currencyCode] ?: return "--"
            return "%,.2f".format(amount * (fromRate / toRate))
        }

    val rateLineText: String
        get() {
            val rates = rates ?: return ""
            val fromRate = rates[fromCountry.currencyCode] ?: return ""
            val toRate = rates[toCountry.currencyCode] ?: return ""
            return "1 ${fromCountry.currencyCode} = %,.4f ${toCountry.currencyCode}".format(fromRate / toRate)
        }
}

class ConverterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RatesRepository.getInstance(application)

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.state.collect { ratesState ->
                _uiState.value = _uiState.value.copy(ratesState = ratesState)
            }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { repository.refresh() }
    }

    fun setFromCountry(country: Country) {
        _uiState.value = _uiState.value.copy(fromCountry = country)
    }

    fun setToCountry(country: Country) {
        _uiState.value = _uiState.value.copy(toCountry = country)
    }

    fun setAmount(text: String) {
        _uiState.value = _uiState.value.copy(amountText = text)
    }

    fun swap() {
        val current = _uiState.value
        _uiState.value = current.copy(fromCountry = current.toCountry, toCountry = current.fromCountry)
    }
}
