package net.getquicker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.GsonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.getquicker.R
import net.getquicker.bean.ResponseBean
import net.getquicker.net.WebSocketManager
import net.getquicker.utils.MessageType
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Inject

/**
 *  author : GL
 *  date : 2024-05-10 16:12
 *  description : WebSocket服务发送心跳包
 */
@AndroidEntryPoint
class WebSocketService : LifecycleService() {
    @Inject
    lateinit var webSocketManager: WebSocketManager
    private var webSocket: WebSocket? = null
    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            this@WebSocketService.webSocket = webSocket
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val responseBean = GsonUtils.fromJson(text, ResponseBean::class.java)
            if (responseBean.messageType == MessageType.authResp) {
                if (true == responseBean.isSuccess) {
                    //定时发送心跳
                    lifecycleScope.launch {
                        while (webSocketManager.isConnect) {
                            delay(60 * 1000)
                            webSocketManager.sendHeart()
                        }
                    }
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //开启前台服务
            val channelId = "quicker";
            val notification: Notification =
                NotificationCompat.Builder(this, channelId).setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("websocket连接中").setWhen(System.currentTimeMillis())
                    .build()

            val channel = NotificationChannel(
                channelId, "quicker", NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "后台保持websocket连接"

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            startForeground(1, notification)
        }

        webSocketManager.addListener(webSocketListener)
    }

    override fun onDestroy() {
        webSocketManager.removeListener(webSocketListener)
        super.onDestroy()
    }
}