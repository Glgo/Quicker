package net.getquicker.net

import com.blankj.utilcode.util.*
import kotlinx.coroutines.delay
import net.getquicker.bean.ResponseBean
import net.getquicker.bean.SendMsgBean
import net.getquicker.utils.MessageType
import okhttp3.*
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton

/**
 *  author : Clay
 *  date : 2022/05/06 15:59:57
 *  description : WebSocket管理类
 */
@Singleton
class WebSocketManager @Inject constructor() {
    @Inject
    lateinit var okHttpClient: OkHttpClient
    private var mWebSocket: WebSocket? = null
    val isConnect
        get() = mWebSocket != null
    private val webSocketListeners: MutableList<WebSocketListener> = mutableListOf()
    private var authCode: String = ""
    private val socketListener = object : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            LogUtils.e("onMessage:text:$text")
            val responseBean = GsonUtils.fromJson(text, ResponseBean::class.java)
            if (true == responseBean.isSuccess && responseBean.messageType == MessageType.authResp) {
                mWebSocket = webSocket
            }
            for (webSocketListener in webSocketListeners) {
                webSocketListener.onMessage(webSocket, text)
            }
            val data = responseBean.data
            if (data.isNullOrBlank() || responseBean.messageType != MessageType.push) return
            if (data.startsWith("clipboard:")) {
                val result = data.substring("clipboard:".length)
                ClipboardUtils.copyText(result)
                ToastUtils.showShort("文本已复制：\n${result}")
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            LogUtils.e("onMessage:bytes:$bytes")
            for (webSocketListener in webSocketListeners) {
                webSocketListener.onMessage(webSocket, bytes)
            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            //验证消息
            if (authCode.isNotEmpty()) {
                webSocket.send(
                    GsonUtils.toJson(
                        SendMsgBean(
                            null, authCode, messageType = MessageType.authReq
                        )
                    )
                )
            }
            for (webSocketListener in webSocketListeners) {
                webSocketListener.onOpen(webSocket, response)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            LogUtils.e("onClosed:$reason")
            for (webSocketListener in webSocketListeners) {
                webSocketListener.onClosed(webSocket, code, reason)
            }
            mWebSocket = null
//                    webSocketListeners.clear()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            LogUtils.e("onClosing")
            for (webSocketListener in webSocketListeners) {
                webSocketListener.onClosing(webSocket, code, reason)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            LogUtils.e("onFailure:${t.message}")
            for (webSocketListener in webSocketListeners) {
                webSocketListener.onFailure(webSocket, t, response)
            }
            mWebSocket = null
//                    webSocketListeners.clear()
        }
    }

    fun connect(url: String, authCode: String?): WebSocket {
        if (mWebSocket != null) return mWebSocket!!
        cancel()
        this@WebSocketManager.authCode = authCode ?: ""
        return okHttpClient.newWebSocket(Request.Builder().url(url).build(), socketListener)
    }

    fun addListener(webSocketListener: WebSocketListener) {
        if (webSocketListeners.contains(webSocketListener)) return
        webSocketListeners.add(webSocketListener)
    }

    fun removeListener(webSocketListener: WebSocketListener) {
        webSocketListeners.remove(webSocketListener)
    }

    fun removeLast() {
        if (webSocketListeners.isNotEmpty()) webSocketListeners.removeLast()
    }

    fun removeAllListener() {
        webSocketListeners.clear()
    }

    private fun cancel() {
        mWebSocket?.cancel()
        mWebSocket = null
    }


    fun sendHeart() {
        mWebSocket?.send(
            GsonUtils.toJson(
                SendMsgBean(null, null, messageType = MessageType.push)
            )
        )
    }
}