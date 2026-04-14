package com.example.kit_market.presentation.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.example.kit_market.presentation.screen.splash.SplashScreen

@Composable
fun AppNavigator() {
    Navigator(SplashScreen()) { navigator ->
        SlideTransition(navigator)
    }
}
