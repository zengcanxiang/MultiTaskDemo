package cn.zengcanxiang.learning.multitaskdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class Test3Activity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test1)

    }

//    override fun finish() {
//        moveTaskToBack(true)
//    }
//
//    override fun moveTaskToBack(nonRoot: Boolean): Boolean {
//        return try {
//            super.moveTaskToBack(nonRoot)
//        } catch (e: Exception) {
//            finishAndRemoveTask()
//            false
//        }
//    }
}
