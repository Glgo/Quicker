package net.getquicker.push

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.getquicker.bean.DevicesDataBean
import net.getquicker.bean.PushRequestBean
import net.getquicker.net.HttpApi
import javax.inject.Inject

typealias ResultListener = (List<DevicesDataBean>?) -> Unit

@HiltViewModel
class PushViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var httpApi: HttpApi

    /**
     * 粘贴到窗口
     */
    fun pasteToWindow(
        text: String,
        listener: ResultListener? = null
    ) {
        request("paste", text, listener = listener)
    }

    /**
     * 粘贴到窗口
     */
    fun copy(text: String, listener: ResultListener? = null) {
        request("copy", text, listener = listener)
    }

    /**
     * 粘贴到窗口
     */
    fun open(url: String, listener: ResultListener? = null) {
        request("open", url, listener = listener)
    }

    /**
     * 发送按键
     */
    fun sendKey(key: String, listener: ResultListener? = null) {
        request("sendkeys", key, listener = listener)
    }

    /**
     * 运行动作
     */
    fun action(
        action: String,
        data: String? = null,
        listener: ResultListener? = null
    ) {
        request("action", data, action, true, listener)
    }

    private fun request(
        operation: String, data: String? = null, action: String? = null, wait: Boolean = false,
        listener: ResultListener? = null
    ) {
        viewModelScope.launch {
            try {
                val result = httpApi.request(PushRequestBean(operation, data, action, wait))
                if (result.isSuccess == true) {
                    if (!result.devices.isNullOrEmpty()) {
                        val list = mutableListOf<DevicesDataBean>()
                        for (key in result.devices.keys) {
                            val value = result.devices[key]
                            list.add(DevicesDataBean(key, value))
                        }
                        listener?.invoke(list)
                    }
                } else {
                    result.errorMessage?.let { ToastUtils.showShort(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}