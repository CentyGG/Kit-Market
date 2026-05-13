package com.example.kit_market.presentation.screen.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformWebView(
    url: String,
    onSuccess: () -> Unit,
    onFail: () -> Unit
) {
    val delegate = remember {
        WebViewNavigationDelegate(
            onSuccess = onSuccess,
            onFail = onFail
        )
    }

    UIKitView(
        factory = {
            val config = WKWebViewConfiguration().apply {
                defaultWebpagePreferences.allowsContentJavaScript = true
            }
            WKWebView(frame = kotlinx.cinterop.cValue { }, configuration = config).apply {
                navigationDelegate = delegate
                allowsBackForwardNavigationGestures = true
                val nsUrl = NSURL(string = url)
                val request = NSMutableURLRequest(uRL = nsUrl)
                loadRequest(request)
            }
        },
        modifier = Modifier
    )
}

private class WebViewNavigationDelegate(
    private val onSuccess: () -> Unit,
    private val onFail: () -> Unit
) : NSObject(), WKNavigationDelegateProtocol {

    private var handled = false

    private fun checkUrl(loadedUrl: String): Boolean {
        if (handled) return true
        val lower = loadedUrl.lowercase()
        if (lower.contains("success") || lower.contains("state=completed")) {
            handled = true
            onSuccess()
            return true
        } else if (lower.contains("fail") || lower.contains("state=rejected")) {
            handled = true
            onFail()
            return true
        }
        return false
    }

    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit
    ) {
        val loadedUrl = decidePolicyForNavigationAction.request.URL?.absoluteString
        if (loadedUrl != null) {
            if (checkUrl(loadedUrl)) {
                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                return
            }

            // Deeplink банковского приложения (СБП) — не http/https
            if (!loadedUrl.startsWith("http://") && !loadedUrl.startsWith("https://")) {
                val nsUrl = NSURL(string = loadedUrl)
                UIApplication.sharedApplication.openURL(nsUrl, options = emptyMap<Any?, Any>(), completionHandler = null)
                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                return
            }
        }

        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
    }

    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
        val loadedUrl = webView.URL?.absoluteString
        if (loadedUrl != null) {
            checkUrl(loadedUrl)
        }
    }
}
