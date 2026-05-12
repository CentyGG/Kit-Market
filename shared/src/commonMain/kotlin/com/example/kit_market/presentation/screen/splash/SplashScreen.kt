package com.example.kit_market.presentation.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kit_market.presentation.navigation.MainScreen
import com.example.kit_market.presentation.screen.auth.AuthScreen
import com.example.kit_market.presentation.theme.KitBlue
import com.example.kit_market.presentation.theme.KitWhite

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinInject<SplashViewModel>()
        val isLoggedIn by screenModel.isLoggedIn.collectAsState()

        LaunchedEffect(isLoggedIn) {
            when (isLoggedIn) {
                true -> navigator.replaceAll(MainScreen())
                false -> navigator.replaceAll(AuthScreen())
                null -> { /* still loading */ }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KitWhite),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "\uD83D\uDC33",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Кит-Маркет",
                    color = KitBlue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(color = KitBlue)
            }
        }
    }
}
