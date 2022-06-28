package net.getquicker.mouse

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.getquicker.base.BaseFragment
import net.getquicker.bean.SendMsgBean
import net.getquicker.databinding.ActivityMouseControlBinding
import net.getquicker.net.WebSocketManager
import net.getquicker.utils.SpUtil
import net.getquicker.utils.exts.singleClick
import net.getquicker.widget.DialogUtil
import net.getquicker.widget.MouseControlView
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 *  author : Clay
 *  date : 2022/01/04 08:50:56
 *  description : 鼠标控制
 */
@AndroidEntryPoint
class MouseControlFragment : BaseFragment<ActivityMouseControlBinding>() {
    @Inject
    lateinit var webSocketManager: WebSocketManager
    private var webSocket: WebSocket? = null

    //滚轮最小滚动距离
    private val wheelMinSize = 10
    private var isAltKeyDown = false
    override fun ActivityMouseControlBinding.initBinding(savedInstanceState: Bundle?) {
        val ip = SpUtil.ip
        val socketPort = SpUtil.socketPort
        if (ip.isNotBlank() && socketPort != 0) {//自动连接
            webSocket =
                webSocketManager.connect(getWssUrl(ip, socketPort.toString()), SpUtil.socketCode)
        } else {
            showSettingDialog()
        }
        initClickListener()
        webSocketManager.addListener(object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                //接受二进制数据
//                val bytesArray = bytes.toByteArray()
//                val bitmap = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.size)
//                lifecycleScope.launch(Dispatchers.Main) { imageView.loadImage(bitmap) }
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                ToastUtils.showShort("连接失败")
                LogUtils.e(t.message)
                lifecycleScope.launch(Dispatchers.Main) { showSettingDialog() }
            }
        })
        mouseControlView.updateMouseListener { type, x, y ->
            when (type) {
                MouseControlView.LEFTCLICK -> {
                    webSocket?.send(GsonUtils.toJson(SendMsgBean("inputscript", "click:left")))
                }
                MouseControlView.LEFTDOWN -> {
                    webSocket?.send(GsonUtils.toJson(SendMsgBean("inputscript", "down:left")))
                }
                MouseControlView.LEFTUP -> {
                    webSocket?.send(GsonUtils.toJson(SendMsgBean("inputscript", "up:left")))
                }
                MouseControlView.MOUSEMOVE -> {
                    webSocket?.send(
                        GsonUtils.toJson(
                            SendMsgBean(
                                "inputscript", "move:${x!!.roundToInt()},${y!!.roundToInt()}"
                            )
                        )
                    )
                }
                MouseControlView.WHEEL -> {
                    val xInt = x!!.roundToInt()
                    val yInt = y!!.roundToInt()
                    if (abs(xInt) > wheelMinSize) {
                        webSocket?.send(
                            GsonUtils.toJson(
                                SendMsgBean("inputscript", "hwheeldelta:${xInt * 5}")
                            )
                        )
                    }
                    if (abs(yInt) > wheelMinSize) {
                        webSocket?.send(
                            GsonUtils.toJson(
                                SendMsgBean("inputscript", "wheeldelta:${-yInt}")
                            )
                        )
                    }
                }
            }
        }
    }

    private fun ActivityMouseControlBinding.initClickListener() {
        ivSetting.singleClick { showSettingDialog() }
        tvBackspace.setOnClickListener {
            webSocket?.send(GsonUtils.toJson(SendMsgBean("sendkeys", "{BS}")))
        }
        tvSpace.setOnClickListener {
            webSocket?.send(GsonUtils.toJson(SendMsgBean("sendkeys", " ")))

//            if (isAltKeyDown) {
//                webSocket?.send(GsonUtils.toJson(SendMsgBean("keyup", "LMenu")))
//            } else {
////                webSocket?.send(GsonUtils.toJson(SendMsgBean("keydown", "LMenu")))
////                webSocket?.send(GsonUtils.toJson(SendMsgBean("keypress", "{TAB}")))
//                webSocket?.send(
//                    GsonUtils.toJson(//ALT(长按)+TAB
//                        SendMsgBean(
//                            "inputscript", "keydown:LMenu\nkeypress:{TAB}"
//                        )
//                    )
//                )
//            }
//            isAltKeyDown = !isAltKeyDown
        }
        tvEnter.setOnClickListener {
            webSocket?.send(GsonUtils.toJson(SendMsgBean("sendkeys", "{ENTER}")))
        }
        tvLeft.setOnClickListener {
            if (isAltKeyDown) {
                webSocket?.send(GsonUtils.toJson(SendMsgBean("sendkeys", "+{TAB}")))
            } else {
                webSocket?.send(GsonUtils.toJson(SendMsgBean("sendkeys", "{LEFT}")))
            }
        }
        tvUp.setOnClickListener {
            webSocket?.send(GsonUtils.toJson(SendMsgBean("sendkeys", "{UP}")))
        }
        tvDown.setOnClickListener {
            webSocket?.send(GsonUtils.toJson(SendMsgBean("sendkeys", "{DOWN}")))
        }
        tvRight.setOnClickListener {
            if (isAltKeyDown) {
                webSocket?.send(GsonUtils.toJson(SendMsgBean("sendkeys", "{TAB}")))
            } else {
                webSocket?.send(GsonUtils.toJson(SendMsgBean("sendkeys", "{RIGHT}")))
            }
        }
    }

    private fun showSettingDialog() {
        DialogUtil.showSocketSettingDialog(requireActivity()) { ip, port, code ->
            webSocket = webSocketManager.connect(getWssUrl(ip, port), code)
        }
    }

    override fun onDestroy() {
        webSocketManager.removeLast()
        super.onDestroy()
    }

    private fun getWssUrl(ip: String, port: String): String {
        val wssIp = ip.replace(".", "-")
        return "wss://$wssIp.lan.quicker.cc:$port/ws"
    }
}