package net.getquicker.panel

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.RegexUtils
import net.getquicker.base.BaseEventBusFragment
import net.getquicker.client.ClientManager
import net.getquicker.client.ConnectionStatus
import net.getquicker.databinding.ActivityLoginBinding
import net.getquicker.events.ConnectionStatusChangedEvent
import net.getquicker.utils.PageNavUtil.gotoFragment
import net.getquicker.utils.PageNavUtil.panel
import net.getquicker.utils.SpUtil
import net.getquicker.utils.exts.showActivity
import net.getquicker.utils.exts.singleClick
import net.getquicker.utils.requestCamera
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *  author : Clay
 *  date : 2021/12/21 14:50:22
 *  description : 登录
 */
class LoginFragment : BaseEventBusFragment<ActivityLoginBinding>() {
    private val scanRequestCode = 10001
    override fun ActivityLoginBinding.initBinding(savedInstanceState: Bundle?) {
        etIp.setText(SpUtil.ip)
        etPort.setText(SpUtil.panelPort.toString())
        etVerifiableCode.setText(SpUtil.panelCode)
        ivScan.singleClick {
            requestCamera { showActivity<ScanActivity>(scanRequestCode) }
        }
        btConnect.singleClick {
            val ip = etIp.text.toString()
            if (!RegexUtils.isIP(ip)) {
                etIp.error = "请输入正确IP"
                return@singleClick
            }
            val port = etPort.text.toString()
            if (port.isBlank()) {
                etPort.error = "端口号不能为空"
                return@singleClick
            }
            val connectCode = etVerifiableCode.text.toString()
            if (connectCode.isBlank()) {
                etVerifiableCode.error = "验证码不能为空"
                return@singleClick
            }
            if (ip == SpUtil.ip && port.toInt() == SpUtil.panelPort && ClientManager.isConnected()) {
                requireActivity().supportFragmentManager.gotoFragment(panel)
                return@singleClick
            }
            SpUtil.ip = ip
            SpUtil.panelPort = port.toInt()
            SpUtil.panelCode = connectCode
            ClientManager.connect(1)
        }
    }

    /**
     * 更新连接状态显示
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: ConnectionStatusChangedEvent) {
        when (event.status) {
            ConnectionStatus.LoggedIn -> {
                requireActivity().supportFragmentManager.gotoFragment(panel)
            }
            ConnectionStatus.Connecting -> {
                mBinding.tvState.text = "连接中,请稍等..."
                mBinding.btConnect.isEnabled = false
            }
            else -> {
                mBinding.tvState.text = event.message
                mBinding.btConnect.isEnabled = true
            }
        }
    }

    @Suppress("deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == scanRequestCode && resultCode == RESULT_OK) {
            if (data != null) {
                val ip = data.getStringExtra("ip")
                val port = data.getStringExtra("port")
                val code = data.getStringExtra("code")
                if (ip != null && port != null && code != null) {
                    mBinding.etIp.setText(ip)
                    mBinding.etPort.setText(port)
                    mBinding.etVerifiableCode.setText(code)
                    mBinding.btConnect.performClick()
                }
            }
        }
    }
}