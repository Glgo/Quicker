package net.getquicker.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import net.getquicker.utils.exts.getViewBinding

abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), BaseBinding<VB> {
    protected lateinit var mBinding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = getViewBinding(inflater, container)
        return mBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.initBinding(savedInstanceState)
        mBinding.lifecycleOwner = this
    }
    override fun onDestroy() {
        mBinding.unbind()
        super.onDestroy()
    }
}