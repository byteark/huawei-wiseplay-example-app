package com.byteark.wiseplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.byteark.wiseplay.navigation.WisePlayNavigation
import com.byteark.wiseplay.ui.theme.WisePlayDRMTheme
import com.byteark.wiseplay.ui.viewmodels.ConfigurationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WisePlayDRMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WisePlayDRMApp()
                }
            }
        }
    }
}

@Composable
fun WisePlayDRMApp() {
    val navController = rememberNavController()
    val configurationViewModel: ConfigurationViewModel = viewModel()
    
    WisePlayNavigation(
        navController = navController,
        configurationViewModel = configurationViewModel
    )
}

@Preview(showBackground = true)
@Composable
fun WisePlayDRMAppPreview() {
    WisePlayDRMTheme {
        WisePlayDRMApp()
    }
}