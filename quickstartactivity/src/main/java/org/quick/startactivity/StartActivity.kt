package org.quick.startactivity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.SparseArray
import androidx.annotation.Size
import java.io.Serializable

/**
 * @describe 快速简洁的返回startActivityForResult值，以回调的方式使用
 * @author ChrisZou
 * @date 2018/6/14-14:33
 * @from https://github.com/SpringSmell/QuickStartActivity
 * @email chrisSpringSmell@gmail.com
 */
object StartActivity {

    private val requestParamsList = SparseArray<((resultCode: Int, data: Intent?) -> Unit)>()

    private fun startActivity(builder: Builder, onActivityResultListener: ((resultCode: Int, data: Intent?) -> Unit)? = null) {
        if (onActivityResultListener == null)
            builder.context?.startActivity(builder.build())
        else {
            builder.build().component?.run {
                if (builder.context is Activity)
                    (builder.context as Activity).startActivityForResult(builder.build(), insertListener(className, onActivityResultListener))
                else
                    builder.context?.startActivity(builder.build())
            }
        }
    }

    /**
     * 取Hashcode的偶数位，创建RequestCode
     */
    fun createRequestCode(binder: Any): Int {
        val hasCodeStr = binder.hashCode().toString()
        var tempCode = ""
        for (index in hasCodeStr.length - 1 downTo 0)
            if (index % 2 != 0)
                tempCode += hasCodeStr[index]
        val requestCode = tempCode.toInt()
        return if (requestCode > 65536) requestCode / 2 else requestCode
    }

    /**
     * 插入绑定者
     * @param binder 绑定者
     * @param onActivityResultListener 回调监听
     * @return requestCode
     */
    fun insertListener(binder: Any, onActivityResultListener: ((resultCode: Int, data: Intent?) -> Unit)): Int {
        val requestCode = createRequestCode(binder)
        requestParamsList.put(requestCode, onActivityResultListener)/*这里是以目的地存储的*/
        return requestCode
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        requestParamsList.get(requestCode)?.invoke(resultCode, data)
    }

    fun remove(binder: Any) {
        requestParamsList.remove(createRequestCode(binder))
    }

    fun resetInternal() {
        requestParamsList.clear()
    }

    class Builder(var context: Context? = null, cls: Class<*>? = null) {
        var intent: Intent =
            if (cls == null)
                Intent()
            else
                Intent(context, cls).apply {
                    if (context !is Activity) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }

        fun addParams(data: Intent): Builder {
            intent.putExtras(data)
            return this
        }

        fun addParams(bundle: Bundle): Builder {
            intent.putExtras(bundle)
            return this
        }

        fun addParams(key: String, @Size(min = 1) vararg value: String): Builder {
            if (value.size == 1) intent.putExtra(key, value[0]) else intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, @Size(min = 1) vararg value: Float): Builder {
            if (value.size == 1) intent.putExtra(key, value[0]) else intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, @Size(min = 1) vararg value: Int): Builder {
            if (value.size == 1) intent.putExtra(key, value[0]) else intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, @Size(min = 1) vararg value: Double): Builder {
            if (value.size == 1) intent.putExtra(key, value[0]) else intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, @Size(min = 1) vararg value: Byte): Builder {
            if (value.size == 1) intent.putExtra(key, value[0]) else intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, @Size(min = 1) vararg value: CharSequence): Builder {
            if (value.size == 1) intent.putExtra(key, value[0]) else intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, @Size(min = 1) vararg value: Boolean): Builder {
            if (value.size == 1) intent.putExtra(key, value[0]) else intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, @Size(min = 1) vararg value: Long): Builder {
            if (value.size == 1) intent.putExtra(key, value[0]) else intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, @Size(min = 1) vararg value: Short): Builder {
            if (value.size == 1) intent.putExtra(key, value[0]) else intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, value: ArrayList<String>): Builder {
            intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, value: Bundle): Builder {
            intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, value: Serializable): Builder {
            intent.putExtra(key, value)
            return this
        }

        fun addParams(key: String, value: Parcelable): Builder {
            intent.putExtra(key, value)
            return this
        }

        fun navigation(onActivityResultListener: ((resultCode: Int, data: Intent?) -> Unit)? = null) {
            startActivity(this, onActivityResultListener)
        }

        fun build() = intent
    }

    /**
     * 获取intent中的内容
     *
     * @param intent
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
    </T> */
    fun <T> getIntentValue(intent: Intent, key: String, defaultValue: T): T {
        var value: Any? = defaultValue
        try {
            when (defaultValue) {
                is String -> value = intent.getStringExtra(key)
                is Int -> value = intent.getIntExtra(key, defaultValue)
                is Boolean -> value = intent.getBooleanExtra(key, defaultValue)
                is Serializable -> value = intent.getSerializableExtra(key)
                is Parcelable -> value = intent.getParcelableExtra(key)
                is Long -> value = intent.getLongExtra(key, defaultValue)
                is Float -> value = intent.getFloatExtra(key, defaultValue)
                is Double -> value = intent.getDoubleExtra(key, defaultValue)
                is java.util.ArrayList<*> -> value = intent.getStringArrayListExtra(key)
                is Bundle -> value = intent.getBundleExtra(key)
                is Class<*> -> defaultValue.newInstance() as T
            }
        } catch (o_O: Exception) {
            value = defaultValue
            Log.e("转换错误", "获取intent内容失败：或许是因为Key不存在,若需要解决请手动添加类型转换")
            o_O.printStackTrace()
        }

        if (value == null) {
            value = defaultValue
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}
