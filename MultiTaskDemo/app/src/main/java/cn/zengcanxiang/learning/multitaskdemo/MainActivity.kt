package cn.zengcanxiang.learning.multitaskdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main.*


class MainActivity : AppCompatActivity() {
    var c1 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        click1.setOnClickListener {
            val data = Bundle().apply {
                putString("url", "https://www.xiaoheiban.cn/")
            }
            MultiTaskLauncherUtils.launcher(
                this@MainActivity,
                "$c1",
                data
            )
            c1++
        }
        click2.setOnClickListener {
            val data = Bundle().apply {
                putString("url", "https://im.qq.com/")
            }
            MultiTaskLauncherUtils.launcher(
                this@MainActivity,
                "launcher2",
                data
            )
        }
        click3.setOnClickListener {
            val intent = Intent(this, Test3Activity::class.java)
            intent.removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("TAG", "onActivityResult被回调了")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Toast.makeText(this, "onnew Intent", Toast.LENGTH_LONG).show()
    }
}
