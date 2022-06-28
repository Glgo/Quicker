package net.getquicker.widget

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.core.CenterPopupView
import net.getquicker.R
import net.getquicker.utils.SpUtil
import net.getquicker.utils.exts.singleClick

/**
 * websocket IP 端口号 验证码输入窗口
 */
typealias SocketResult = (ip: String, port: String, code: String) -> Unit

class SocketSettingView(context: Context, private val socketResult: SocketResult) :
    CenterPopupView(context) {
    override fun getImplLayoutId(): Int = R.layout.dialog_socket_setting
    override fun onCreate() {
        super.onCreate()
        val etIp = findViewById<EditText>(R.id.et_ip)
        val etPort = findViewById<EditText>(R.id.et_port)
        val etCode = findViewById<EditText>(R.id.et_code)
        val tvConfirm = findViewById<TextView>(R.id.tv_confirm)
        etIp.setText(SpUtil.ip)
        etPort.setText(SpUtil.socketPort.toString())
        etCode.setText(SpUtil.socketCode)
        tvConfirm.singleClick {
            val ip = etIp.text.toString()
            val port = etPort.text.toString()
            val code = etCode.text.toString()
            if (ip.isEmpty() || port.isEmpty()) {
                ToastUtils.showShort("请输入IP和端口号")
            } else {
                SpUtil.ip = ip
                SpUtil.socketPort = port.toInt()
                SpUtil.socketCode = code
                socketResult.invoke(ip, port, code)
                dismiss()
            }
        }
    }
}