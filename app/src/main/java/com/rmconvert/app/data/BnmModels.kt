package com.rmconvert.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BnmRateEnvelope(
    val data: List<BnmCurrencyRate> = emptyList()
)

@Serializable
data class BnmCurrencyRate(
    @SerialName("currency_code") val currencyCode: String,
    val unit: Int = 1,
    val rate: BnmRateDetail
)

@Serializable
data class BnmRateDetail(
    val date: String,
    @SerialName("buying_rate") val buyingRate: Double? = null,
    @SerialName("selling_rate") val sellingRate: Double? = null,
    @SerialName("middle_rate") val middleRate: Double? = null
)

/**
 * BNM quotes every currency as MYR per [unit] of foreign currency (unit is 1 or 100
 * depending on the currency). This returns the MYR value of a single foreign unit.
 */
fun BnmCurrencyRate.myrPerForeignUnit(): Double? {
    val quote = rate.middleRate
        ?: if (rate.buyingRate != null && rate.sellingRate != null) {
            (rate.buyingRate + rate.sellingRate) / 2.0
        } else {
            rate.buyingRate ?: rate.sellingRate
        }
    return quote?.let { it / unit }
}
