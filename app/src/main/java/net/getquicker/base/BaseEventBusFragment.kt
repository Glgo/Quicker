package net.getquicker.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import org.greenrobot.eventbus.EventBus

abstract class BaseEventBusFragment<VB : ViewDataBinding> : BaseFragment<VB>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}