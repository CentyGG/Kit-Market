package com.example.kit_market.presentation.screen.payment

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
actual fun PlatformWebView(
    url: String,
    onSuccess: () -> Unit,
    onFail: () -> Unit
) {
    // iOS WebView implementation placeholder
    Text("WebView не поддерживается на iOS")
}
