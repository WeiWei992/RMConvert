package com.rmconvert.app.ui.taxrefund

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rmconvert.app.data.BNM_QUOTED_COUNTRIES
import com.rmconvert.app.data.Country
import com.rmconvert.app.data.CountryTaxInfo
import com.rmconvert.app.data.MALAYSIA
import com.rmconvert.app.data.RatesRepository
import com.rmconvert.app.data.RatesUiState
import com.rmconvert.app.data.vatInfoFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaxRefundResult(
    val vatAmount: Double,
    val refundAmount: Double,
    val netCost: Double,
    val convertedRefund: Double?
)

private fun fmt(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

data class TaxRefundUiState(
    val purchaseCountry: Country = BNM_QUOTED_COUNTRIES.first { it.currencyCode == "THB" },
    val targetCountry: Country = MALAYSIA,
    val purchaseAmountText: String = "1000",
    val vatPercentText: String = fmt(vatInfoFor("THB").standardVatPercent),
    val refundPercentText: String = "100",
    val ratesState: RatesUiState = RatesUiState.Loading
) {
    val vatInfo: CountryTaxInfo get() = vatInfoFor(purchaseCountry.currencyCode)

    val result: TaxRefundResult?
        get() {
            val purchaseAmount = purchaseAmountText.toDoubleOrNull() ?: return null
            val vatPercent = vatPercentText.toDoubleOrNull() ?: return null
            val refundPercent = refundPercentText.toDoubleOrNull() ?: return null
            val vatAmount = purchaseAmount - purchaseAmount / (1 + vatPercent / 100)
            val refundAmount = vatAmount * (refundPercent / 100)
            val netCost = purchaseAmount - refundAmount
            val rates = (ratesState as? RatesUiState.Success)?.ratesByCurrency
            val converted = rates?.let {
                val fromRate = it[purchaseCountry.currencyCode]
                val toRate = it[targetCountry.currencyCode]
                if (fromRate != null && toRate != null) refundAmount * (fromRate / toRate) else null
            }
            return TaxRefundResult(vatAmount, refundAmount, netCost, converted)
        }
}

class TaxRefundViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RatesRepository.getInstance(application)

    private val _uiState = MutableStateFlow(TaxRefundUiState())
    val uiState: StateFlow<TaxRefundUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.state.collect { ratesState ->
                _uiState.value = _uiState.value.copy(ratesState = ratesState)
            }
        }
        viewModelScope.launch { repository.refresh() }
    }

    fun setPurchaseCountry(country: Country) {
        _uiState.value = _uiState.value.copy(
            purchaseCountry = country,
            vatPercentText = fmt(vatInfoFor(country.currencyCode).standardVatPercent)
        )
    }

    fun setTargetCountry(country: Country) {
        _uiState.value = _uiState.value.copy(targetCountry = country)
    }

    fun setPurchaseAmount(text: String) {
        _uiState.value = _uiState.value.copy(purchaseAmountText = text)
    }

    fun setVatPercent(text: String) {
        _uiState.value = _uiState.value.copy(vatPercentText = text)
    }

    fun setRefundPercent(text: String) {
        _uiState.value = _uiState.value.copy(refundPercentText = text)
    }
}
