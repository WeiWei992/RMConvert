package com.rmconvert.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rmconvert.app.ui.RMConvertApp
import com.rmconvert.app.ui.theme.RMConvertTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RMConvertTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RMConvertApp()
                }
            }
        }
    }
}
