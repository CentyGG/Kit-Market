package com.example.kit_market.presentation.screen.payment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
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
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.setSupportMultipleWindows(false)
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                var handled = false
                webViewClient = object : WebViewClient() {
                    private fun checkUrl(loadedUrl: String, source: String): Boolean {
                        Log.d(TAG, "[$source] URL: $loadedUrl")
                        if (handled) return true
                        val lower = loadedUrl.lowercase()
                        if (lower.contains("success") || lower.contains("state=completed")) {
                            Log.d(TAG, "Payment SUCCESS detected from $source")
                            handled = true
                            onSuccess()
                            return true
                        } else if (lower.contains("fail") || lower.contains("state=rejected")) {
                            Log.d(TAG, "Payment FAIL detected from $source")
                            handled = true
                            onFail()
                            return true
                        }
                        return false
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val loadedUrl = request?.url?.toString() ?: return false

                        if (checkUrl(loadedUrl, "shouldOverrideUrlLoading")) return true

                        // intent:// схемы (некоторые банки используют их)
                        if (loadedUrl.startsWith("intent://")) {
                            try {
                                val intent = Intent.parseUri(loadedUrl, Intent.URI_INTENT_SCHEME)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                try {
                                    val intent = Intent.parseUri(loadedUrl, Intent.URI_INTENT_SCHEME)
                                    val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                    if (fallbackUrl != null) {
                                        view?.loadUrl(fallbackUrl)
                                    }
                                } catch (_: Exception) { }
                            }
                            return true
                        }

                        // Deeplink банковского приложения (СБП) — не http/https
                        if (!loadedUrl.startsWith("http://") && !loadedUrl.startsWith("https://")) {
                            Log.d(TAG, "Opening deeplink: $loadedUrl")
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(loadedUrl))
                                context.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                Log.w(TAG, "No app found for deeplink: $loadedUrl")
                            }
                            return true
                        }

                        return false
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
