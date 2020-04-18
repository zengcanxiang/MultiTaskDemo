package cn.zengcanxiang.learning.monitor

import android.annotation.SuppressLint
import android.webkit.*
import org.json.JSONObject
import java.net.URL

object WebViewMonitorUtils {

    private var injectJs = "https://www.android_asset.com/monitor.js"

    @SuppressLint("SetJavaScriptEnabled")
    fun addMonitor(webView: WebView, listener: MonitorListener) {
        if (webView.url == "about:blank") {
            return
        }

        injectJs = "https://${URL(webView.url).host}/monitor.js"

        val jsMsg = """
            javascript:(
                function() {
                    console.log("xxx1");
                    var script=document.createElement("script");
                    script.setAttribute("type","text/javascript");
                    script.setAttribute('src', '$injectJs');
                    document.head.appendChild(script);
                    script.onload = function() {
                        startWebViewMonitor();
                    };
                }
            )();
        """.trimIndent()

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView.addJavascriptInterface(MonitorJSObject(listener), "monitorNative")
        webView.loadUrl(jsMsg)
    }

    fun buildMonitorWebResponse(
        webView: WebView,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return if (request?.url?.toString()?.contains(injectJs) == true) {
            val open = webView.context.assets.open("monitor.js")
            WebResourceResponse(
                "text/javascript", "utf-8", open
            )
        } else {
            null
        }
    }
}


private typealias MonitorListener = (message: String) -> Unit

private class MonitorJSObject(private val listener: MonitorListener) {

    @JavascriptInterface
    fun trackPerformance(monitorMsg: String) {
        listener.invoke(monitorMsg)
    }
}


class DefaultMonitorData(monitorMsg: String) {

    /**
     * 网页加载情况
     */
    val payload = JSONObject(monitorMsg).optJSONObject("payload")

    /**
     * 资源加载信息列表
     */
    val resourceMsgArray = payload?.optJSONArray("resourceTiming")

    /**
     * 页面加载起始时间
     */
    val startTime = optTime("navigationStart")

    /**
     * 加载耗时时间
     */
    val loadTotalTime = optTime("loadEventEnd") - startTime

    /**
     * 白屏时间
     */
    val whiteScreenTime = optTime("responseStart") - startTime

    /**
     * dom的耗时
     */
    val domTotalTime = optTime("domComplete") - optTime("domLoading")

    /**
     * dns耗时
     */
    val dnsLoadTotalTime = optTime("domainLookupEnd") - optTime("domainLookupStart")

    /**
     * tcp连接耗时
     */
    val connectTotalTime = optTime("connectEnd") - optTime("connectStart")

    /**
     * 请求耗时
     */
    val requestTotalTime = optTime("responseEnd") - optTime("requestStart")

    /**
     * 获取时间
     */
    private fun optTime(key: String): Long {
        return payload?.optJSONObject("navigationTiming")?.optLong(key) ?: 0
    }
}

open class DefaultMonitorWebViewClient(
    private val listener: MonitorListener
) : WebViewClient() {
    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        WebViewMonitorUtils.addMonitor(view, listener)
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return WebViewMonitorUtils.buildMonitorWebResponse(
            view, request
        ) ?: super.shouldInterceptRequest(view, request)
    }
}
