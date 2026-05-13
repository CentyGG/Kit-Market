package com.example.kit_market

import androidx.compose.ui.window.ComposeUIViewController
import com.example.kit_market.data.remote.TokenStorage

fun MainViewController(): platform.UIKit.UIViewController {
    TokenStorage.init(NSUserDefaultsTokenProvider())
    return ComposeUIViewController { App() }
}
