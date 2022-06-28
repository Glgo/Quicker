package net.getquicker.panel

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.RequestBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.getquicker.R
import net.getquicker.base.BaseEventBusFragment
import net.getquicker.bean.DataPageValues
import net.getquicker.client.ClientManager
import net.getquicker.client.ConnectionStatus
import net.getquicker.client.MessageCache
import net.getquicker.databinding.ActivityQuickerBinding
import net.getquicker.events.ConnectionStatusChangedEvent
import net.getquicker.events.ServerMessageEvent
import net.getquicker.messages.MessageBase
import net.getquicker.messages.recv.UpdateButtonsMessage
import net.getquicker.messages.recv.VolumeStateMessage
import net.getquicker.messages.send.CommandMessage
import net.getquicker.messages.send.TextDataMessage
import net.getquicker.utils.ImagePicker
import net.getquicker.utils.PageNavUtil
import net.getquicker.utils.PageNavUtil.gotoFragment
import net.getquicker.utils.exts.singleClick
import net.getquicker.utils.requestCamera
import net.getquicker.utils.svg.GlideApp
import net.getquicker.utils.svg.SvgSoftwareLayerSetter
import net.getquicker.utils.toast
import net.getquicker.widget.DialogUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayOutputStream


/**
 *  author : Clay
 *  date : 2021/12/21 15:35:22
 *  description : Quicker面板界面
 */
class PanelFragment : BaseEventBusFragment<ActivityQuickerBinding>() {
    private val voiceRecognitionRequestCode = 1234
    private val photoRequestCode = 2234
    private lateinit var globalAdapter: PanelPageAdapter
    private lateinit var contextAdapter: PanelPageAdapter

    //网络断开后重连的最大次数
    private val retryCountMax = 1

    //当前重连次数
    private var retryCount = 0

    // 用于显示svg图标
    private lateinit var requestBuilder: RequestBuilder<PictureDrawable>
    private val currentTime: Long
        get() = System.currentTimeMillis()
    private var startSeekTime: Long = 0L
    private var isTouchSeekBar = false

    @Suppress("deprecation")
    override fun ActivityQuickerBinding.initBinding(savedInstanceState: Bundle?) {
        requestBuilder = GlideApp.with(this@PanelFragment).`as`(PictureDrawable::class.java)
            .listener(SvgSoftwareLayerSetter())

        globalAdapter = PanelPageAdapter(requireContext(), true)
        contextAdapter = PanelPageAdapter(requireContext(), false)
        vpGlobal.adapter = globalAdapter
        vpContext.adapter = contextAdapter

        ivLockPage.singleClick { ClientManager.lockContext() }
        btPc.singleClick { ClientManager.openWindow() }
        btMute.singleClick { ClientManager.sendToggleMuteMsg() }
        seekVolume.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                isTouchSeekBar = fromUser
                if (currentTime - startSeekTime > 200 && isTouchSeekBar)
                    ClientManager.sendUpdateVolumeMsg(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (isTouchSeekBar) ClientManager.sendUpdateVolumeMsg(seekBar.progress)
                isTouchSeekBar = false
            }
        })
        btVoice.singleClick { startVoiceInput() }
        btConfig.singleClick {  requireActivity().supportFragmentManager.gotoFragment(PageNavUtil.panelLogin) }
        btPhoto.singleClick {
            requestCamera {
                startActivityForResult(
                    ImagePicker.getPickImageIntent(requireActivity()) ?: return@requestCamera,
                    photoRequestCode
                ) //跳转，传递打开相册请求码
            }
        }
        vpGlobal.registerOnPageChangeCallback(PagerCallBack(true))
        vpContext.registerOnPageChangeCallback(PagerCallBack(false))
    }

    override fun onStart() {
        super.onStart()
        ClientManager.requestReSendState()
    }

    /**
     * 处理收到的pc消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ServerMessageEvent) {
        val originMessage: MessageBase = event.serverMessage
        processPcMessage(originMessage)
    }

    /**
     * 处理pc消息
     * @param originMessage
     */
    private fun processPcMessage(originMessage: MessageBase?) {
        if (originMessage == null) return
        if (originMessage is UpdateButtonsMessage) {
            //更新page通用数据
            DataPageValues.contextPageName = originMessage.ProfileName
            DataPageValues.contextDataPageCount = originMessage.ContextPageCount
            DataPageValues.currentContextPageIndex = originMessage.ContextPageIndex
            DataPageValues.globalDataPageCount = originMessage.GlobalPageCount
            DataPageValues.currentGlobalPageIndex = originMessage.GlobalPageIndex
            DataPageValues.IsContextPanelLocked = originMessage.IsContextPanelLocked

            if (!originMessage.Buttons.isNullOrEmpty()) {
                val globalData = originMessage.Buttons!!.filter { it.Index < 1000000 }
                val contextData = originMessage.Buttons!!.filter { it.Index >= 1000000 }
                if (globalData.isNotEmpty()) {
                    globalAdapter.setNewData(
                        originMessage.GlobalPageCount,
                        originMessage.GlobalPageIndex,
                        globalData
                    )
                    mBinding.vpGlobal.currentItem = originMessage.GlobalPageIndex
                    //更新小圆点
                    mBinding.viewpagerPoint.updateGlobalCuePoint()

                }
                if (contextData.isNotEmpty()) {
                    contextAdapter.setNewData(
                        originMessage.ContextPageCount,
                        originMessage.ContextPageIndex,
                        contextData
                    )
                    mBinding.vpContext.currentItem = originMessage.ContextPageIndex
                    //更新小圆点
                    mBinding.viewpagerPoint.updateContextCuePoint()
                }
            }
            MessageCache.lastUpdateButtonsMessage = originMessage
            //组件名称
            mBinding.tvProfileName.text = originMessage.ProfileName
            if (originMessage.IsContextPanelLocked) {
                mBinding.ivLockPage.setImageResource(R.drawable.ic_lock_black_24dp)
            } else {
                mBinding.ivLockPage.setImageResource(R.drawable.ic_lock_open_black_24dp)
            }
//            if (!originMessage.Buttons.isNullOrEmpty()) {
//                for (buttonItem in originMessage.Buttons!!) {
//                    updateButton(
//                        buttonItem, originMessage.GlobalPageIndex, originMessage.ContextPageIndex
//                    )
//                }
//            }
        } else if (originMessage is VolumeStateMessage) {
            val volumeStateMessage: VolumeStateMessage = originMessage
            MessageCache.lastVolumeStateMessage = volumeStateMessage
            updateVolumeState(volumeStateMessage)
        }
    }

    // 更新声音状态显示
    private fun updateVolumeState(message: VolumeStateMessage) {
        if (message.Mute) {
            mBinding.btMute.setImageResource(R.drawable.ic_volume_off_black_24dp)
            mBinding.seekVolume.visibility = View.INVISIBLE
            if (!isTouchSeekBar) mBinding.seekVolume.progress = message.MasterVolume
        } else {
            when {
                message.MasterVolume > 50 -> {
                    mBinding.btMute.setImageResource(R.drawable.ic_volume_up_black_24dp)
                }
                message.MasterVolume > 5 -> {
                    mBinding.btMute.setImageResource(R.drawable.ic_volume_down_black_24dp)
                }
                else -> {
                    mBinding.btMute.setImageResource(R.drawable.ic_volume_mute_black_24dp)
                }
            }
            mBinding.seekVolume.visibility = View.VISIBLE
            if (!isTouchSeekBar) mBinding.seekVolume.progress = message.MasterVolume
        }
    }

    @Suppress("deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == voiceRecognitionRequestCode) {
            if (resultCode == RESULT_OK && data != null) {
                //返回结果是一个list，我们一般取的是第一个最匹配的结果
                val text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ClientManager.sendTextMsg(TextDataMessage.TYPE_VOICE_RECOGNITION, text!![0])
            }
        } else if (requestCode == photoRequestCode) {
            lifecycleScope.launch {
                val loadingDialog = DialogUtil.showLoading(requireActivity())
                withContext(Dispatchers.IO) {
                    val bitmap: Bitmap =
                        ImagePicker.getImageFromResult(requireActivity(), resultCode, data)
                            ?: return@withContext
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val byteArray = stream.toByteArray()
                    ClientManager.sendPhotoMsg("image.jpg", byteArray)
                }
                loadingDialog.dismiss()
            }
        }
    }

    /**
     * 开启语音输入
     */
    @Suppress("deprecation")
    private fun startVoiceInput() {
        //开启语音识别功能
        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        )
        //设置模式，目前设置的是自由识别模式
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        //提示语言开始文字，就是效果图上面的文字
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please start your voice")
        //开始识别，这里检测手机是否支持语音识别并且捕获异常
        try {
            startActivityForResult(intent, voiceRecognitionRequestCode)
        } catch (a: ActivityNotFoundException) {
            toast("抱歉，您的设备当前不支持此功能。请安装Google语音搜索。")
        }
    }

    /**
     * 网络连接状态改变了。
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: ConnectionStatusChangedEvent) {
        // 如果连接断开，进入配置页面
        if (event.status === ConnectionStatus.Disconnected || event.status === ConnectionStatus.LoginFailed) {
            LogUtils.e("断开连接了...")
            when {
                retryCount == 0 -> {
                    ClientManager.connect(retryCountMax)
                }
                retryCount >= retryCountMax -> {
                    requireActivity().supportFragmentManager.gotoFragment(PageNavUtil.panelLogin)
                    LogUtils.e("断开连接了...")
                }
            }
            retryCount++
        }
    }
}

class PagerCallBack(private val isGlobal: Boolean) : ViewPager2.OnPageChangeCallback() {
    private var oldPosition = 0

    //是否是手动拖动
    private var isGesture = false
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        if (isGesture) {//只有手动拖动才发送翻页请求
            val tmp: Int = position - oldPosition
            if (isGlobal) {
                //全局数据页
                if (tmp < 0) ClientManager.requestUpdateDataPage(CommandMessage.DATA_PAGE_GLOBAL_LEFT)
                else ClientManager.requestUpdateDataPage(CommandMessage.DATA_PAGE_GLOBAL_RIGHT)
            } else {
                //上下文页
                if (tmp < 0) ClientManager.requestUpdateDataPage(CommandMessage.DATA_PAGE_CONTEXT_LEFT)
                else ClientManager.requestUpdateDataPage(CommandMessage.DATA_PAGE_CONTEXT_RIGHT)
            }
        }
        oldPosition = position
    }

    override fun onPageScrollStateChanged(state: Int) {
        super.onPageScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE) {
            isGesture = false
        } else if (state == SCROLL_STATE_DRAGGING) {
            isGesture = true
        }
    }
}