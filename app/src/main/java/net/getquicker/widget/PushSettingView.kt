package net.getquicker.widget

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.lxj.xpopup.core.CenterPopupView
import net.getquicker.R
import net.getquicker.utils.SpUtil
import net.getquicker.utils.exts.singleClick

class PushSettingView(context: Context) : CenterPopupView(context) {
    override fun getImplLayoutId(): Int = R.layout.dialog_push_setting
    override fun onCreate() {
        super.onCreate()
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPushCode = findViewById<EditText>(R.id.et_push_code)
        val etToDevice = findViewById<EditText>(R.id.et_to_device)
        val tvConfirm = findViewById<TextView>(R.id.tv_confirm)

        etEmail.setText(SpUtil.email)
        etPushCode.setText(SpUtil.pushCode)
        etToDevice.setText(SpUtil.toDevices)
        tvConfirm.singleClick {
            val email = etEmail.text.toString()
            if (email.isEmpty()) {
                etEmail.error = "请输入email"
                return@singleClick
            }
            val pushCode = etPushCode.text.toString()
            if (pushCode.isEmpty()) {
                etPushCode.error = "请输入验证码"
                return@singleClick
            }
            val toDevice = etToDevice.text.toString()
            SpUtil.email = email
            SpUtil.pushCode = pushCode
            SpUtil.toDevices = toDevice
            dismiss()
        }
    }
}