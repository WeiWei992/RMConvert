package com.rmconvert.app.ui.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.rmconvert.app.data.Country
import com.rmconvert.app.data.RatesUiState
import com.rmconvert.app.ui.components.CountryPickerDialog

private enum class PickerTarget { FROM, TO }

@Composable
fun ConverterScreen(modifier: Modifier = Modifier, viewModel: ConverterViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var pickerTarget by remember { mutableStateOf<PickerTarget?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Currency Converter", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        when (val ratesState = uiState.ratesState) {
            is RatesUiState.Error -> {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Couldn't load live rates", fontWeight = FontWeight.Bold)
                        Text(ratesState.message, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.refresh() }) { Text("Try again") }
                    }
                }
            }
            is RatesUiState.Success -> if (ratesState.isFromCache) {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        "Showing cached rates from ${ratesState.asOf} — no internet connection",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            RatesUiState.Loading -> {}
        }

        ElevatedCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CurrencyRow(label = "From", country = uiState.fromCountry, onClick = { pickerTarget = PickerTarget.FROM })

                OutlinedTextField(
                    value = uiState.amountText,
                    onValueChange = viewModel::setAmount,
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    FilledIconButton(onClick = { viewModel.swap() }) {
                        Icon(Icons.Default.SwapVert, contentDescription = "Swap currencies")
                    }
                }

                CurrencyRow(label = "To", country = uiState.toCountry, onClick = { pickerTarget = PickerTarget.TO })

                Text(
                    "${uiState.resultText} ${uiState.toCountry.currencyCode}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                if (uiState.rateLineText.isNotEmpty()) {
                    Text(
                        uiState.rateLineText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { viewModel.refresh() }) {
                if (uiState.ratesState is RatesUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh rates")
                }
            }
            val successState = uiState.ratesState as? RatesUiState.Success
            Text(
                if (successState != null) "Rates by Bank Negara Malaysia · ${successState.asOf}" else "Loading live rates…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    pickerTarget?.let { target ->
        CountryPickerDialog(
            countries = ALL_COUNTRIES,
            onSelect = { country ->
                when (target) {
                    PickerTarget.FROM -> viewModel.setFromCountry(country)
                    PickerTarget.TO -> viewModel.setToCountry(country)
                }
            },
            onDismiss = { pickerTarget = null }
        )
    }
}

@Composable
private fun CurrencyRow(label: String, country: Country, onClick: () -> Unit) {
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
