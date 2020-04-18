package cn.zengcanxiang.learning.multitaskdemo

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout

object WebViewPool {
    private var available = ArrayList<WebView>()
    private var inUse = ArrayList<WebView>()
    private var maxSize = 1

    /**
     * Webview 初始化
     * 最好放在application oncreate里
     */
    fun init(context: Context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handler(Looper.getMainLooper()).post {
                _init(context)
            }
        } else {
            _init(context)
        }
    }


    /**
     * 获取WebView
     *
     */
    fun getWebView(context: Context): WebView {
        val webView: WebView
        if (available.size > 0) {
            webView = available[0]
            available.removeAt(0)
            inUse.add(webView)
        } else {
            webView = WebView(
                MutableContextWrapper(context.applicationContext)
            )
            inUse.add(webView)
        }
        (webView.context as? MutableContextWrapper)?.let {
            it.baseContext = context
        }
        webView.loadUrl("about:blank")
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        webView.layoutParams = params
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        return webView
    }

    /**
     * 回收WebView ,解绑
     *
     */
    fun removeWebView(view: ViewGroup, webView: WebView) {
        view.removeView(webView)
        webView.removeAllViews()
        webView.loadUrl("about:blank")
        webView.stopLoading()
        webView.clearCache(true)
        Handler(Looper.getMainLooper()).postDelayed({
            webView.clearHistory()
            webView.webViewClient = null
            webView.webChromeClient = null
            inUse.remove(webView)
            (webView.context as? MutableContextWrapper)?.let {
                it.baseContext = it.baseContext.applicationContext
            }
            if (available.size < maxSize) {
                available.add(webView)
            } else {
                webView.destroy()
            }
        }, 500)
    }

    /**
     * 设置WebView池个数
     */
    fun setMaxPoolSize(size: Int) {
        maxSize = size
    }

    private fun _init(context: Context) {
        Looper.myQueue().addIdleHandler {
            Log.e("TAG", "webViewPool init : $this")
            for (i in 0 until maxSize) {
                val webView = WebView(
                    MutableContextWrapper(context.applicationContext)
                )
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webView.layoutParams = params
                available.add(webView)
            }
            return@addIdleHandler false
        }
    }
}
