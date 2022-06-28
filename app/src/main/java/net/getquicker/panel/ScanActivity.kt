package net.getquicker.panel

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.BarUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.getquicker.base.BaseActivity
import net.getquicker.databinding.ActivityScanBinding
import net.getquicker.utils.CameraManager
import net.getquicker.utils.ZxingManager
import net.getquicker.utils.exts.singleClick

/**
 *  author : Clay
 *  date : 2021/12/27 17:16:48
 *  description : 二维码扫描识别IP、端口号、验证码
 */
class ScanActivity : BaseActivity<ActivityScanBinding>() {
    private var isGetResult = false
    override fun ActivityScanBinding.initBinding(savedInstanceState: Bundle?) {
        BarUtils.transparentStatusBar(window)
        ivClose.singleClick { finish() }
        CameraManager(
            this@ScanActivity, this@ScanActivity,
            preview, findViewById<View>(android.R.id.content).rootView
        ) { data, width, height ->
            if (isGetResult) return@CameraManager
            isGetResult = true
            lifecycleScope.launch {
                ZxingManager.decode(data, width, height) {
                    if (it.startsWith("PB:")) {
                        val split = it.split("\n")
                        if (split.size >= 4) {
                            setResult(
                                RESULT_OK, Intent()
                                    .putExtra("ip", split[1])
                                    .putExtra("port", split[2])
                                    .putExtra("code", split[3])
                            )
                            finish()
                            return@decode
                        }
                    }
                }
                isGetResult = false
            }
        }

        val layoutParams = ivScan.layoutParams as ConstraintLayout.LayoutParams
        lifecycleScope.launch {
            var bias = 0f
            while (true) {
                delay(10)
                if (bias >= 1f) bias = 0f
                layoutParams.verticalBias = bias
                bias += 0.007f
                ivScan.layoutParams = layoutParams
                ivScan.alpha = 1f - bias
            }
        }
    }
}