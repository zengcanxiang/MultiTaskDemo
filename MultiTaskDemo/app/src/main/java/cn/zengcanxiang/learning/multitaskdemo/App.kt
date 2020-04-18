package cn.zengcanxiang.learning.multitaskdemo

import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.e("TAG","App onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName()
            WebView.setDataDirectorySuffix("${processName}_suffix")
        }
        WebViewPool.init(this)
    }
}
