package com.example.kit_market

import androidx.compose.runtime.Composable
import com.example.kit_market.di.appModule
import com.example.kit_market.presentation.navigation.AppNavigator
import com.example.kit_market.presentation.theme.KitMarketTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {
        KitMarketTheme {
            AppNavigator()
        }
    }
}
