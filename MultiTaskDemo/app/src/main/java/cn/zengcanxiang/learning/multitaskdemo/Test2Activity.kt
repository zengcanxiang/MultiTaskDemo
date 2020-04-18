package cn.zengcanxiang.learning.multitaskdemo

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat

class Test2Activity : BaseTaskActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "test2"
    }

    override fun taskInfo(): ActivityManager.TaskDescription {
        return ActivityManager.TaskDescription(
            "test2", null, ContextCompat.getColor(this, R.color.colorPrimary)
        )
    }

    override fun url(intent: Intent): String {
        return intent.getStringExtra("url") ?: "https://www.baidu.com"
    }

//    companion object {
//        fun launcher(context: Context, url: String) {
//            Intent(context, Test2Activity::class.java).apply {
//                putExtra("url", url)
//                context.startActivity(this)
//            }
//        }
//    }
}
