package net.getquicker.client

import android.os.Build
import android.util.Base64
import com.blankj.utilcode.util.LogUtils
import net.getquicker.BuildConfig
import net.getquicker.events.ConnectionStatusChangedEvent
import net.getquicker.events.ServerMessageEvent
import net.getquicker.events.SessionClosedEvent
import net.getquicker.messages.MessageBase
import net.getquicker.messages.recv.LoginStateMessage
import net.getquicker.messages.send.*
import net.getquicker.net.MyCodecFactory
import net.getquicker.utils.SpUtil
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.transport.socket.nio.NioSocketConnector
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.net.InetSocketAddress

/**
 * 客户端调度类
 */
object ClientManager {
    private var session: IoSession? = null
    private var connector: NioSocketConnector? = null
    private var connectThread: Thread? = null

    init {
        EventBus.getDefault().register(this)
    }

    // 连接状态
    var _status = ConnectionStatus.Disconnected
        private set

    fun isConnected(): Boolean {
        return session?.isActive == true
    }

    fun sendMessage(msg: MessageBase) {
        if (isConnected()) {
            session!!.write(msg)
        } else {
            changeStatus(ConnectionStatus.Disconnected, "连接中断")
        }
    }

    fun connect(retry: Int, callback: ConnectServiceCallback? = null) {
        shutdown()
        connectThread = Thread { doConnect(retry, callback) }
        connectThread?.start()
    }

    /**
     * 执行建立链接
     *
     * @param retryCount 重试次数
     * @param callback   连接回调
     */
    private fun doConnect(retryCount: Int, callback: ConnectServiceCallback?) {
        if (_status === ConnectionStatus.Connecting) {
            LogUtils.e("正在连接服务器，不能重复连接。")
            return
        }
        connector = NioSocketConnector()
        connector!!.connectTimeoutMillis = 3000

        //设置协议封装解析处理
        connector!!.filterChain.addLast("protocol", ProtocolCodecFilter(MyCodecFactory()))

//        //设置心跳包
//        KeepAliveFilter heartFilter = new KeepAliveFilter(new HeartbeatMessageFactory());
//        heartFilter.setRequestInterval(5 * 60);
//        heartFilter.setRequestTimeout(10);
//        mSocketConnector.getFilterChain().addLast("heartbeat", heartFilter);

        //设置 handler 处理业务逻辑
        connector!!.handler = MinaClientHandler()
        val mSocketAddress = InetSocketAddress(SpUtil.ip, SpUtil.panelPort)
        //配置服务器地址
        var count = 0
        do {
            try {
                if (count > 0) {
                    Thread.sleep(2000)
                }
                changeStatus(ConnectionStatus.Connecting, "")
                val mFuture = connector!!.connect(mSocketAddress)
                mFuture.awaitUninterruptibly()
                if (!mFuture.isConnected) {
                    val e = mFuture.exception
                    LogUtils.e("连接失败" + e.message)
                    callback?.connectCallback(false, null)
                    changeStatus(ConnectionStatus.Disconnected, e.localizedMessage)
                } else {
                    session = mFuture.session
                    callback?.connectCallback(true, null)
                    changeStatus(ConnectionStatus.Connected, "")
                    sendLoginMsg()
                    break
                }
            } catch (e: Exception) {
                LogUtils.e("连接服务器错误！$e")
                callback?.connectCallback(false, null)
                releaseConnector()
                changeStatus(ConnectionStatus.Disconnected, "")
                e.printStackTrace()
            }
            count++
        } while (count <= retryCount)
    }

    private fun sendLoginMsg() {
        val msg = DeviceLoginMessage()
        msg.ConnectionCode = SpUtil.panelCode
        msg.Version = BuildConfig.VERSION_NAME
        msg.DeviceName = Build.MODEL + "(" + Build.MANUFACTURER + " " + Build.PRODUCT + ")"
        sendMessage(msg)
    }

    /**
     * 更改状态并通知ui
     * @param status
     */
    private fun changeStatus(status: ConnectionStatus, message: String?) {
        LogUtils.e("status changed:$status")
        _status = status
        EventBus.getDefault().post(ConnectionStatusChangedEvent(_status, message))
    }

    /**
     * 释放connector资源
     */
    private fun releaseConnector() {
        if (connector != null) {
            connector?.filterChain?.clear()
            connector?.dispose()
            connector = null
        }
    }

    // 是否正在关闭
    fun shutdown() {
        if (true == connectThread?.isAlive) {
            connectThread?.interrupt()
            connectThread = null
        }
        releaseConnector()
        if (session?.isConnected == true) {
            session?.closeNow()
            session = null
        }
    }

    /**
     * 请求PC重发当前状态，用于切换屏幕后刷新界面
     */
    fun requestReSendState() {
        sendCommandMsg(CommandMessage.RESEND_STATE, "")
    }

    /**
     * 请求pc更新面板数据，进行翻页操作
     *
     * @param dataPageType 请求面板的类型
     * @see CommandMessage.DATA_PAGE_GLOBAL_LEFT
     */
    fun requestUpdateDataPage(dataPageType: String) {
        sendCommandMsg(CommandMessage.CHANGE_PAGE, dataPageType)
    }

    /**
     * 向pc发送切换静音消息
     */
    fun sendToggleMuteMsg() {
        val msg = ToggleMuteMessage()
        try {
            sendMessage(msg)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun openWindow() {
        sendCommandMsg(CommandMessage.OPEN_MAINWIN, null)
    }

    /**
     * 发送调整音量消息
     */
    fun sendUpdateVolumeMsg(volume: Int) {
        val msg = UpdateVolumeMessage()
        msg.MasterVolume = volume
        try {
            sendMessage(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 发送按钮按下消息
     */
    fun sendButtonClickMsg(buttonIndex: Int) {
        val msg = ButtonClickedMessage()
        msg.ButtonIndex = buttonIndex
        try {
            sendMessage(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 发送普通文本消息
     * @param type
     * @param text
     */
    fun sendTextMsg(type: Int, text: String) {
        val msg = TextDataMessage()
        msg.DataType = type
        msg.Data = text
        try {
            sendMessage(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 发送通用的命令消息
     * @param command
     * @param data
     */
    private fun sendCommandMsg(command: String?, data: String?) {
        val msg = CommandMessage()
        msg.Command = command
        msg.Data = data
        try {
            sendMessage(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 发送图片消息
     * 注意：因为使用base64编码传送，所以电脑接收到图片后，图片大小比手机上要大很多
     * @param fileName
     * @param content
     */
    fun sendPhotoMsg(fileName: String?, content: ByteArray) {
        val msg = PhotoMessage()
        msg.FileName = fileName
        msg.Data = Base64.encodeToString(content, Base64.DEFAULT)
        sendMessage(msg)
    }

    fun lockContext() {
        sendCommandMsg(CommandMessage.LOCK_PANEL, null)
    }

    /**
     * 连接断开
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: SessionClosedEvent) {
        changeStatus(ConnectionStatus.Disconnected, "已断开")
    }

    /**
     * 处理收到的pc消息--登录状态
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ServerMessageEvent) {
        val originMessage: MessageBase = event.serverMessage
        if (originMessage is LoginStateMessage) {
            val msg: LoginStateMessage = originMessage
            if (msg.IsLoggedIn) {
                //updateConnectionStatus(ConnectionStatus.LoggedIn, msg.ErrorMessage);
                changeStatus(ConnectionStatus.LoggedIn, msg.ErrorMessage)
            } else {
                changeStatus(ConnectionStatus.LoginFailed, msg.ErrorMessage)
            }
        }
    }
}