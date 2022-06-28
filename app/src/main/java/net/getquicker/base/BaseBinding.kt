package net.getquicker.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding

interface BaseBinding<VB : ViewDataBinding> {
    fun VB.initBinding(savedInstanceState: Bundle?)
}
