package net.getquicker.push

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SpanUtils
import dagger.hilt.android.AndroidEntryPoint
import net.getquicker.R
import net.getquicker.base.BaseFragment
import net.getquicker.databinding.ActivityPushBinding
import net.getquicker.utils.SpUtil
import net.getquicker.utils.exts.singleClick
import net.getquicker.utils.toast
import net.getquicker.widget.DialogUtil

/**
 *  author : Clay
 *  date : 2021/12/23 14:16:41
 *  description : 推送到电脑功能
 */
@AndroidEntryPoint
class PushFragment : BaseFragment<ActivityPushBinding>() {
    private val viewModel: PushViewModel by viewModels()
    override fun ActivityPushBinding.initBinding(savedInstanceState: Bundle?) {
        toolbar.tvTitle.text = "推送到电脑"
        toolbar.ivSetting.singleClick { DialogUtil.showPushSettingDialog(requireActivity()) }
        if (SpUtil.email.isEmpty() || SpUtil.pushCode.isEmpty()) {
            DialogUtil.showPushSettingDialog(requireActivity())
        }
        actionInputGone(true)
        tvPaste.singleClick {
            val text = ClipboardUtils.getText()
            etInput.append(text)
        }
        tvClear.singleClick {
            if (rbRunAction.isChecked) {
                if (etAction.id == requireActivity().currentFocus?.id) etAction.text = null
                else etInput.text = null
            } else {
                etInput.text = null
            }
        }
        radioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rb_paste_to_window -> {
                    actionInputGone(true)
                    tvDataLabel.text = "粘贴此内容到电脑"
                }
                R.id.rb_copy -> {
                    actionInputGone(true)
                    tvDataLabel.text = "复制此内容到电脑"
                }
                R.id.rb_open_url -> {
                    actionInputGone(true)
                    tvDataLabel.text = "电脑打开此网站"
                }
                R.id.rb_run_action -> {
                    actionInputGone(false)
                    tvDataLabel.text = "运行动作参数（可选）"
                }
            }
        }
        tvSend.singleClick {
            val data = etInput.text.toString()

            when {
                rbPasteToWindow.isChecked -> {
                    if (data.isEmpty()) etInput.error = "数据不能为空"
                    else viewModel.pasteToWindow(data)
                }
                rbCopy.isChecked -> {
                    if (data.isEmpty()) etInput.error = "数据不能为空"
                    else viewModel.copy(data)
                }
                rbOpenUrl.isChecked -> {
                    if (RegexUtils.isURL(data)) viewModel.open(data)
                    else etInput.error = "请输入正确的网址"
                }
                rbRunAction.isChecked -> {
                    val action = etAction.text.toString()
                    if (action.isEmpty()) {
                        etAction.error = "动作不能为空"
                    } else {
                        viewModel.action(action, data) {
                            if (!it.isNullOrEmpty()) {
                                val spanUtils = SpanUtils.with(tvResult)
                                for (result in it) {
                                    val text = result.data ?: continue
                                    when (result.data) {
                                        "-NO RESPONSE-", "Ok." -> continue
                                        else -> {
                                            spanUtils.append(result.devices).append("：")
                                                .append(text)
                                                .setClickSpan(
                                                    ContextCompat.getColor(
                                                        requireActivity(),
                                                        R.color.blue
                                                    ),
                                                    true
                                                ) {
                                                    ClipboardUtils.copyText(text)
                                                    toast("已复制：${text}")
                                                }.appendLine()
                                        }
                                    }

                                }
                                if (spanUtils.get().isNullOrEmpty()) {
                                    tvResult.text = "无返回值"
                                } else {
                                    spanUtils.create()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun actionInputGone(gone: Boolean) {
        val isGone = if (gone) View.GONE else View.VISIBLE
        mBinding.tvActionLabel.visibility = isGone
        mBinding.etAction.visibility = isGone
    }
}