package com.rmconvert.app.data

/**
 * Indicative standard VAT/GST rate and tourist-refund-scheme availability per country.
 * These are approximate, change over time, and vary by product category, minimum spend,
 * and retailer participation — they are meant as an editable starting point, not as tax
 * advice. Always verify with the official refund operator (e.g. Global Blue, Planet) or
 * customs authority before relying on them.
 */
data class CountryTaxInfo(
    val standardVatPercent: Double,
    val refundSchemeAvailable: Boolean,
    val note: String
)

val VAT_INFO_BY_CURRENCY: Map<String, CountryTaxInfo> = mapOf(
    "MYR" to CountryTaxInfo(6.0, false, "Malaysia's SST does not currently offer a tourist refund scheme."),
    "AED" to CountryTaxInfo(5.0, true, "UAE Tourist Refund Scheme (Planet); min spend AED 250 per receipt."),
    "AUD" to CountryTaxInfo(10.0, true, "Australia Tourist Refund Scheme; min AUD 300 spend, claim at departure."),
    "BND" to CountryTaxInfo(0.0, false, "Brunei has no VAT/GST."),
    "CAD" to CountryTaxInfo(5.0, false, "Canada's federal visitor GST/HST rebate program was discontinued."),
    "CHF" to CountryTaxInfo(8.1, true, "Switzerland VAT refund for purchases over CHF 300 (same store, same day)."),
    "CNY" to CountryTaxInfo(13.0, true, "China departure tax refund available in select pilot cities only."),
    "EGP" to CountryTaxInfo(14.0, true, "Egypt tourist refund available at limited locations for min spend."),
    "EUR" to CountryTaxInfo(20.0, true, "EU VAT varies by member state (~17-27%); refund for non-EU residents."),
    "GBP" to CountryTaxInfo(20.0, false, "UK ended its tourist VAT retail export scheme in Jan 2021."),
    "HKD" to CountryTaxInfo(0.0, false, "Hong Kong has no VAT/GST."),
    "IDR" to CountryTaxInfo(11.0, true, "Indonesia VAT refund at select airports; min spend IDR 500,000."),
    "INR" to CountryTaxInfo(18.0, false, "India GST varies by category (5/12/18/28%); no unified tourist refund."),
    "JPY" to CountryTaxInfo(10.0, true, "Japan tax-free shopping scheme (10%, 8% on food) at participating stores."),
    "KHR" to CountryTaxInfo(10.0, false, "Cambodia has no widely available tourist refund scheme."),
    "KRW" to CountryTaxInfo(10.0, true, "South Korea tourist VAT refund at participating stores/airports."),
    "MMK" to CountryTaxInfo(5.0, false, "Myanmar commercial tax; no tourist refund scheme."),
    "NPR" to CountryTaxInfo(13.0, false, "Nepal VAT refund scheme is limited/inconsistently available."),
    "NZD" to CountryTaxInfo(15.0, false, "New Zealand does not offer a tourist GST refund on goods."),
    "PHP" to CountryTaxInfo(12.0, true, "Philippines VAT refund for tourists (recent law); rollout still limited."),
    "PKR" to CountryTaxInfo(18.0, true, "Pakistan tourist refund available at select international airports."),
    "SAR" to CountryTaxInfo(15.0, true, "Saudi Arabia tourist VAT refund scheme at major airports/border points."),
    "SGD" to CountryTaxInfo(9.0, true, "Singapore eTRS tourist GST refund; min spend SGD 100."),
    "THB" to CountryTaxInfo(7.0, true, "Thailand VAT refund; min THB 2,000 per store, THB 5,000 total."),
    "TWD" to CountryTaxInfo(5.0, true, "Taiwan tourist VAT refund; min NTD 2,000 same store, same day."),
    "USD" to CountryTaxInfo(0.0, false, "The US has no federal VAT/GST; a few states offer limited sales-tax refunds."),
    "VND" to CountryTaxInfo(8.0, true, "Vietnam VAT refund at international airports; min VND 2,000,000.")
)

fun vatInfoFor(currencyCode: String): CountryTaxInfo =
    VAT_INFO_BY_CURRENCY[currencyCode] ?: CountryTaxInfo(0.0, false, "No VAT/GST data available for this currency.")
