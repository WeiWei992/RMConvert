package com.rmconvert.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rmconvert.app.ui.converter.ConverterScreen
import com.rmconvert.app.ui.taxrefund.TaxRefundScreen

private enum class RMConvertTab(val label: String) {
    Convert("Convert"),
    TaxRefund("Tax Refund")
}

@Composable
fun RMConvertApp() {
    var selectedTab by remember { mutableStateOf(RMConvertTab.Convert) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == RMConvertTab.Convert,
                    onClick = { selectedTab = RMConvertTab.Convert },
                    icon = { Icon(Icons.Default.CurrencyExchange, contentDescription = null) },
                    label = { Text(RMConvertTab.Convert.label) }
                )
                NavigationBarItem(
                    selected = selectedTab == RMConvertTab.TaxRefund,
                    onClick = { selectedTab = RMConvertTab.TaxRefund },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null) },
                    label = { Text(RMConvertTab.TaxRefund.label) }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            RMConvertTab.Convert -> ConverterScreen(modifier = Modifier.padding(innerPadding))
            RMConvertTab.TaxRefund -> TaxRefundScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}
