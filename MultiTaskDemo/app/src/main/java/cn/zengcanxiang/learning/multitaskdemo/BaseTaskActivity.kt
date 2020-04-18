package cn.zengcanxiang.learning.multitaskdemo

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.zengcanxiang.learning.monitor.DefaultMonitorData
import cn.zengcanxiang.learning.monitor.DefaultMonitorWebViewClient
import kotlinx.android.synthetic.main.test1.*

abstract class BaseTaskActivity : AppCompatActivity() {

    var url: String = ""

    protected val webview: WebView by lazy {
        WebViewPool.getWebView(this)
    }

    protected val clientMessenger = ClientMessenger()
    protected val handler = Handler {
        Toast.makeText(
            this,
            "收到信息 ${it.what} ${it.data.getString("serviceMsg")}",
            Toast.LENGTH_SHORT
        ).show()
        return@Handler true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test1)
        parentLayout.addView(webview)
        webview.webViewClient = DefaultMonitorWebViewClient {
            val monitorData = DefaultMonitorData(it)
            Log.e("TAG", "总耗时：${monitorData.loadTotalTime}")
        }
        url = url(intent)
        webview.loadUrl(url)
        setTaskDescription(
            taskInfo()
        )

        clientMessenger.register(this)
    }

    abstract fun url(intent: Intent): String

    abstract fun taskInfo(): ActivityManager.TaskDescription

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (url != url(intent)) {
            url = url(intent)
            webview.loadUrl(url)
        }
    }

    override fun moveTaskToBack(nonRoot: Boolean): Boolean {
        return try {
            super.moveTaskToBack(nonRoot)
        } catch (e: Exception) {
            finishAndRemoveTask()
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WebViewPool.removeWebView(
            parentLayout, webview
        )
    }

}
