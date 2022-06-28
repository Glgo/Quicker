package net.getquicker.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.BarUtils
import net.getquicker.utils.exts.getViewBinding

abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity(), BaseBinding<VB> {
    protected open val statusBarLightMode = true

    //透明状态栏
    protected open val isTransparentStatusBar = false
    protected val mBinding: VB by lazy(LazyThreadSafetyMode.NONE) {
        getViewBinding(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        BarUtils.setStatusBarLightMode(window, statusBarLightMode)
        if (isTransparentStatusBar)
            BarUtils.transparentStatusBar(window)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.initBinding(savedInstanceState)
        mBinding.lifecycleOwner = this
    }

    override fun onDestroy() {
        mBinding.unbind()
        super.onDestroy()
    }
}