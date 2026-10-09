package com.rmconvert.app.data

/**
 * A selectable country/currency. [currencyCode] is "MYR" for the Malaysia base entry
 * (always rate 1.0) or one of the ~26 currency codes Bank Negara Malaysia publishes
 * a daily reference rate for.
 */
data class Country(
    val name: String,
    val currencyCode: String,
    val flagEmoji: String
)

/** MYR is the pivot currency: BNM quotes every other currency's rate against it. */
val MALAYSIA = Country("Malaysia", "MYR", "🇲🇾")

val BNM_QUOTED_COUNTRIES: List<Country> = listOf(
    Country("United Arab Emirates", "AED", "🇦🇪"),
    Country("Australia", "AUD", "🇦🇺"),
    Country("Brunei", "BND", "🇧🇳"),
    Country("Canada", "CAD", "🇨🇦"),
    Country("Switzerland", "CHF", "🇨🇭"),
    Country("China", "CNY", "🇨🇳"),
    Country("Egypt", "EGP", "🇪🇬"),
    Country("Eurozone", "EUR", "🇪🇺"),
    Country("United Kingdom", "GBP", "🇬🇧"),
    Country("Hong Kong", "HKD", "🇭🇰"),
    Country("Indonesia", "IDR", "🇮🇩"),
    Country("India", "INR", "🇮🇳"),
    Country("Japan", "JPY", "🇯🇵"),
    Country("Cambodia", "KHR", "🇰🇭"),
    Country("South Korea", "KRW", "🇰🇷"),
    Country("Myanmar", "MMK", "🇲🇲"),
    Country("Nepal", "NPR", "🇳🇵"),
    Country("New Zealand", "NZD", "🇳🇿"),
    Country("Philippines", "PHP", "🇵🇭"),
    Country("Pakistan", "PKR", "🇵🇰"),
    Country("Saudi Arabia", "SAR", "🇸🇦"),
    Country("Singapore", "SGD", "🇸🇬"),
    Country("Thailand", "THB", "🇹🇭"),
    Country("Taiwan", "TWD", "🇹🇼"),
    Country("United States", "USD", "🇺🇸"),
    Country("Vietnam", "VND", "🇻🇳")
)

/** All selectable countries, Malaysia first. */
val ALL_COUNTRIES: List<Country> = listOf(MALAYSIA) + BNM_QUOTED_COUNTRIES
