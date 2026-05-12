package com.example.kit_market.presentation.screen.payment

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

private const val TAG = "KitPayment"

@Composable
actual fun PlatformWebView(
    url: String,
    onSuccess: () -> Unit,
    onFail: () -> Unit
) {
    Log.d(TAG, "WebView loading URL: $url")
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                var handled = false
                webViewClient = object : WebViewClient() {
                    private fun checkUrl(loadedUrl: String, source: String) {
                        Log.d(TAG, "[$source] URL: $loadedUrl")
                        if (handled) return
                        val lower = loadedUrl.lowercase()
                        if (lower.contains("success") || lower.contains("state=completed")) {
                            Log.d(TAG, "Payment SUCCESS detected from $source")
                            handled = true
                            onSuccess()
                        } else if (lower.contains("fail") || lower.contains("state=rejected")) {
                            Log.d(TAG, "Payment FAIL detected from $source")
                            handled = true
                            onFail()
                        }
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val loadedUrl = request?.url?.toString() ?: return false
                        checkUrl(loadedUrl, "shouldOverrideUrlLoading")
                        return handled
                    }

                    override fun onPageStarted(view: WebView?, loadedUrl: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, loadedUrl, favicon)
                        if (loadedUrl != null) checkUrl(loadedUrl, "onPageStarted")
                    }

                    override fun onPageFinished(view: WebView?, loadedUrl: String?) {
                        super.onPageFinished(view, loadedUrl)
                        if (loadedUrl != null) checkUrl(loadedUrl, "onPageFinished")
                    }
                }
                loadUrl(url)
            }
        }
    )
}
