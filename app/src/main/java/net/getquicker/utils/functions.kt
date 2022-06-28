package net.getquicker.utils

import androidx.annotation.StringRes
import com.blankj.utilcode.util.ToastUtils

// toast
fun toast(msg: CharSequence) = ToastUtils.showShort(msg)
fun toast(@StringRes msg: Int) = ToastUtils.showShort(msg)
fun toastLong(msg: CharSequence) = ToastUtils.showLong(msg)
fun toastLong(@StringRes msg: Int) = ToastUtils.showLong(msg)

