package net.getquicker.sendkey

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.children
import dagger.hilt.android.AndroidEntryPoint
import net.getquicker.base.BaseActivity
import net.getquicker.databinding.ActivitySendKeyBinding
import net.getquicker.push.PushViewModel
import net.getquicker.utils.Constants.altKeyCode
import net.getquicker.utils.Constants.backspaceKeyCode
import net.getquicker.utils.Constants.ctrlKeyCode
import net.getquicker.utils.Constants.shiftKeyCode
import net.getquicker.widget.ArrowView

/**
 *  author : Clay
 *  date : 2021/12/20 14:02:10
 *  description : 发送按键
 */
@AndroidEntryPoint
class SendKeyActivity : BaseActivity<ActivitySendKeyBinding>() {
    //输入法状态是否为大写
    private var isCapitalize = false
    private val isCtrlChecked: Boolean
        get() = mBinding.cbCtrl.isChecked

    private val isAltChecked: Boolean
        get() = mBinding.cbAlt.isChecked

    private val isShiftChecked: Boolean
        get() = mBinding.cbShift.isChecked

    private val isCapsChecked: Boolean
        get() = mBinding.cbCaps.isChecked

    private val viewModel: PushViewModel by viewModels()
    override fun ActivitySendKeyBinding.initBinding(savedInstanceState: Bundle?) {
        for (child in consContainer.children) {
            if (child is TextView && child.text.toString().length == 1) {
                child.setOnClickListener {
                    val sb = StringBuilder()
                    val keys = child.text.toString()
                    if (isCtrlChecked) sb.append(ctrlKeyCode)
                    if (isShiftChecked) sb.append(shiftKeyCode)
                    if (isAltChecked) sb.append(altKeyCode)
                    sb.append(keys)
                    viewModel.sendKey(sb.toString())
                }
            }
        }
        tvSpace.setOnClickListener { viewModel.action("9094e3c0-3ec1-4237-9796-c334aa3e0db5") }
        ivBackspace.setOnClickListener { viewModel.sendKey(backspaceKeyCode) }
        cbShift.setOnCheckedChangeListener { _, _ ->
            if (!isCtrlChecked && !isAltChecked) {
                mBinding.cbShift.isChecked = false
                viewModel.sendKey(shiftKeyCode)
            }
        }
        ivCapitalize.setOnClickListener {
            isCapitalize = !isCapitalize
            for (child in consContainer.children) {
                if (child is TextView) {
                    if (isCapitalize) {
                        child.text = child.text.toString().uppercase()
                    } else {
                        child.text = child.text.toString().lowercase()
                    }
                }
            }
        }
        cbCtrl.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) updateShiftButton()
        }
        cbAlt.setOnCheckedChangeListener { _, isChecked -> if (!isChecked) updateShiftButton() }
        arrowView.setOnArrowClickListener { arrow ->
            when (arrow) {
                ArrowView.Arrow.LEFT -> viewModel.sendKey("{LEFT}")
                ArrowView.Arrow.UP -> viewModel.sendKey("{UP}")
                ArrowView.Arrow.RIGHT -> viewModel.sendKey("{RIGHT}")
                ArrowView.Arrow.DOWN -> viewModel.sendKey("{DOWN}")
                else -> {}
            }
        }
    }

    private fun updateShiftButton() {
        if (!isCtrlChecked && !isAltChecked) {
            mBinding.cbShift.isChecked = false
        }
    }
}