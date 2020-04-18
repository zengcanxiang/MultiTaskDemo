package cn.zengcanxiang.learning.multitaskdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle

object MultiTaskLauncherUtils {

    private val sparePool = mutableListOf(
//        Test1Activity::class.java,
//        Test2Activity::class.java,
        Test3Activity::class.java
    )

    /**
     * linkedHashMap设置访问顺序
     */
    private val usePool = LinkedHashMap<String, Class<out Any>>(
        16, 0.75F, true
    )

    fun launcher(
        context: Activity,
        onlyTag: String,
        data: Bundle
    ) {
        val startClass = findStartClass(onlyTag)
        Intent(context, startClass).apply {
            putExtras(data)
            context.startActivityForResult(this, 20)
        }
    }

    private fun findStartClass(onlyTag: String): Class<out Any>? {
        return when {
            usePool[onlyTag] != null -> usePool[onlyTag]
            sparePool.size <= 0 -> update(onlyTag)
            else -> {
                val temp = sparePool.removeAt(0)
                usePool[onlyTag] = temp
                temp
            }
        }
    }

    private fun update(
        onlyTag: String
    ): Class<out Any>? {
        // 找到最少使用的,即第一个
        val endItem = usePool.firstEntry() ?: return null
        // 如果onlyTag不一致，则移除原来的key。吧新的和这个class组成新的，加入
        if (endItem.key != onlyTag) {
            usePool.remove(endItem.key)
            usePool[onlyTag] = endItem.value
        }
        return endItem.value
    }

    private fun <K, V> LinkedHashMap<K, V>.firstEntry(): Map.Entry<K, V>? {
        var index = 0
        forEach {
            if (index == 0) {
                return it
            } else {
                index++
            }
        }
        return null
    }
}
