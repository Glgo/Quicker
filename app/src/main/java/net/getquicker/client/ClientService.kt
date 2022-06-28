package net.getquicker.client

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import net.getquicker.events.ServerMessageEvent
import net.getquicker.messages.MessageBase
import net.getquicker.messages.recv.UpdateButtonsMessage
import net.getquicker.messages.recv.VolumeStateMessage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ClientService : Service(), ConnectServiceCallback {
    private val binder: IBinder = LocalBinder()
    override fun onBind(intent: Intent): IBinder = binder
    override fun connectCallback(isSuccess: Boolean, obj: Any?) {

    }

    inner class LocalBinder : Binder() {
        fun getService() = this@ClientService
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
// TODO: 2021/12/21 wifi监控
//        NetworkUtils.addOnWifiChangedConsumer { result->result.allResults }
        ClientManager.connect(1, this@ClientService)
    }

    /**
     * 处理收到的pc消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ServerMessageEvent) {
        val originMessage: MessageBase = event.serverMessage
        if (originMessage is UpdateButtonsMessage) {
            MessageCache.lastUpdateButtonsMessage = originMessage
        } else if (originMessage is VolumeStateMessage) {
            MessageCache.lastVolumeStateMessage = originMessage
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        ClientManager.shutdown()
    }
}