package com.rmconvert.app.ui.taxrefund

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmconvert.app.data.ALL_COUNTRIES
import com.rmconvert.app.data.BNM_QUOTED_COUNTRIES
import com.rmconvert.app.data.Country
import com.rmconvert.app.ui.components.CountryPickerDialog

private enum class PickerTarget { PURCHASE, TARGET }

@Composable
fun TaxRefundScreen(modifier: Modifier = Modifier, viewModel: TaxRefundViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var pickerTarget by remember { mutableStateOf<PickerTarget?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Tourist Tax Refund", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Estimate the VAT/GST refund on a purchase made abroad, converted to your currency.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ElevatedCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabeledCountryButton(
                    label = "Purchase country",
                    country = uiState.purchaseCountry,
                    onClick = { pickerTarget = PickerTarget.PURCHASE }
                )
                OutlinedTextField(
                    value = uiState.purchaseAmountText,
                    onValueChange = viewModel::setPurchaseAmount,
                    label = { Text("Purchase amount (${uiState.purchaseCountry.currencyCode}, tax included)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.vatPercentText,
                        onValueChange = viewModel::setVatPercent,
                        label = { Text("VAT/GST %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.refundPercentText,
                        onValueChange = viewModel::setRefundPercent,
                        label = { Text("Refund %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                LabeledCountryButton(
                    label = "Convert refund to",
                    country = uiState.targetCountry,
                    onClick = { pickerTarget = PickerTarget.TARGET }
                )
            }
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Row(Modifier.padding(12.dp)) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        if (uiState.vatInfo.refundSchemeAvailable) "Refund scheme likely available" else "Refund scheme may not be available",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(uiState.vatInfo.note, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        val result = uiState.result
        if (result != null) {
            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResultRow(
                        "VAT/GST included in price",
                        "%,.2f %s".format(result.vatAmount, uiState.purchaseCountry.currencyCode)
                    )
                    ResultRow(
                        "Estimated refund",
                        "%,.2f %s".format(result.refundAmount, uiState.purchaseCountry.currencyCode)
                    )
                    ResultRow(
                        "Net cost after refund",
                        "%,.2f %s".format(result.netCost, uiState.purchaseCountry.currencyCode)
                    )
                    HorizontalDivider()
                    Text(
                        if (result.convertedRefund != null) {
                            "= %,.2f %s refunded".format(result.convertedRefund, uiState.targetCountry.currencyCode)
                        } else {
                            "Live rates unavailable to convert"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            "Rates are indicative only. Minimum spend, eligible goods, and processing fees vary by " +
                "country and retailer — confirm with the official refund operator before you shop.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    pickerTarget?.let { target ->
        CountryPickerDialog(
            countries = when (target) {
                PickerTarget.PURCHASE -> BNM_QUOTED_COUNTRIES
                PickerTarget.TARGET -> ALL_COUNTRIES
            },
            onSelect = { country ->
                when (target) {
                    PickerTarget.PURCHASE -> viewModel.setPurchaseCountry(country)
                    PickerTarget.TARGET -> viewModel.setTargetCountry(country)
                }
            },
            onDismiss = { pickerTarget = null }
        )
    }
}

@Composable
private fun LabeledCountryButton(label: String, country: Country, onClick: () -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${country.flagEmoji}  ${country.name}")
                Text(country.currencyCode, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
