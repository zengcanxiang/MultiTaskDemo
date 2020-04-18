package cn.zengcanxiang.learning.multitaskdemo

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.util.Log
import android.os.*
import android.content.ComponentName
import android.os.Messenger
import android.os.IBinder
import android.content.ServiceConnection
import android.content.Context.BIND_AUTO_CREATE
import android.content.pm.ResolveInfo
import android.content.pm.PackageManager
import android.widget.Toast
import cn.zengcanxiang.learning.multitaskdemo.ServiceMessenger.incomingMessageHandler
import cn.zengcanxiang.learning.multitaskdemo.WebViewPool.init


const val ACTION = "com.zengcanxiang.messenger.service"

interface IClientMessenger {

    fun send(msg: Message, handler: Handler)

    fun register(activity: Activity)
}

interface IServiceMessenger {

    fun accept(msg: Message)

    fun send(msg: Message)
}

class ClientMessenger : IClientMessenger {
    private var isRegister: Boolean = false
    private var serviceMessenger: Messenger? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            service.linkToDeath(
                {
                    serviceMessenger = null
                    Log.e("TAG", "死亡通知")
                },
                0
            )
            serviceMessenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.e("TAG", "onServiceDisconnected")
        }
    }

    override fun send(msg: Message, handler: Handler) {
        msg.replyTo = Messenger(handler)
        serviceMessenger?.send(msg)
    }

    override fun register(activity: Activity) {
        if (!isRegister) {
            val intent = Intent()
            intent.action = ACTION
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            val pm = activity.packageManager
            //我们先通过一个隐式的Intent获取可能会被启动的Service的信息
            val info = pm.resolveService(intent, 0)
            if (info != null) {
                //如果ResolveInfo不为空，说明我们能通过上面隐式的Intent找到对应的Service
                //我们可以获取将要启动的Service的package信息以及类型
                val packageName = info.serviceInfo.packageName
                val serviceName = info.serviceInfo.name
                //然后我们需要将根据得到的Service的包名和类名，构建一个ComponentName
                //从而设置intent要启动的具体的组件信息，这样intent就从隐式变成了一个显式的intent
                //之所以大费周折将其从隐式转换为显式intent，是因为从Android 5.0 Lollipop开始，
                //Android不再支持通过通过隐式的intent启动Service，只能通过显式intent的方式启动Service
                //在Android 5.0 Lollipop之前的版本倒是可以通过隐式intent启动Service
                val componentName = ComponentName(packageName, serviceName)
                intent.component = componentName
                try {
                    activity.bindService(intent, connection, BIND_AUTO_CREATE)
                    isRegister = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}

object ServiceMessenger : IServiceMessenger {

    private var clientMessenger: Messenger? = null

    val incomingMessageHandler: Handler = Handler { msg ->
        accept(msg)
        return@Handler true
    }

    override fun accept(msg: Message) {
        clientMessenger = msg.replyTo
        Log.e("MessengerService", "收到信息")
        when (msg.what) {
            1 -> {
                Log.e("MessengerService", msg.data.toString())
                try {
                    val bundle = Bundle()
                    bundle.putString("serviceMsg", "service返回activity消息:“你好，我是service返回来的信息”")
                    val fh = Message.obtain()
                    fh.what = 0
                    fh.data = bundle
                    clientMessenger?.send(fh)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }

            }
        }
    }


    override fun send(msg: Message) {
        clientMessenger?.send(msg)
    }
}

class MessengerService : Service() {

    private var messenger: Messenger? = null

    init {
        messenger = Messenger(incomingMessageHandler)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return messenger?.binder
    }

}
