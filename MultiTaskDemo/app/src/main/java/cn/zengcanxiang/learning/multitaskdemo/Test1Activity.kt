package cn.zengcanxiang.learning.multitaskdemo

import android.content.Intent
import android.os.Bundle
import android.app.ActivityManager
import android.os.Message
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.test1.*


class Test1Activity : BaseTaskActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "test1"

        val button = Button(this)
        button.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        button.text = "点击发送消息给多进程服务"
        button.setOnClickListener {
            val msg = Message.obtain().apply {
                val b = Bundle()
                b.putString("clientMsg", "activity发送消息给service:“你好，我是activity发送的初始化信息”")
                what = 1
                data = b
            }
            clientMessenger.send(msg, handler)
        }
        parentLayout.addView(button)
    }

    override fun taskInfo(): ActivityManager.TaskDescription {
        return ActivityManager.TaskDescription(
            "test1", null, ContextCompat.getColor(this, R.color.colorPrimary)
        )
    }

    override fun url(intent: Intent): String {
        return intent.getStringExtra("url") ?: "https://www.xiaoheiban.cn/"
    }

//    companion object {
//        fun launcher(context: Context, url: String) {
//            Intent(context, Test1Activity::class.java).apply {
//                putExtra("url", url)
//                context.startActivity(this)
//            }
//        }
//    }
}
