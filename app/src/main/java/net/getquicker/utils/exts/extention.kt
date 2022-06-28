package net.getquicker.utils.exts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ClickUtils
import java.lang.reflect.ParameterizedType

/**
 *  author : guolei
 *  date : 2021/9/21 8:28
 *  description : 扩展类
 */
@Keep
fun <VB : ViewBinding> Any.getViewBinding(inflater: LayoutInflater): VB {
    val vbClass =
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
    val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java)
    return inflate.invoke(null, inflater) as VB
}

@Keep
fun <VB : ViewBinding> Any.getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
    val vbClass =
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
    val inflate = vbClass[0].getDeclaredMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return inflate.invoke(null, inflater, container, false) as VB
}


inline fun <reified T : Activity> Activity.showActivity(
    requestCode: Int = 0,
    bundle: Bundle? = null
) {
    val intent = Intent(this, T::class.java).apply {
        if (null != bundle)
            putExtras(bundle)
    }
    if (requestCode == 0) {
        startActivity(intent)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

inline fun <reified T : Activity> Fragment.showActivity(
    requestCode: Int = 0,
    bundle: Bundle? = null
) {
    val intent = Intent(activity, T::class.java).apply {
        if (null != bundle)
            putExtras(bundle)
    }
    if (requestCode == 0) {
        startActivity(intent)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

/**
 * 点击防抖
 */
fun <T : View> T.singleClick(action: ((T) -> Unit)) {
    ClickUtils.applyPressedBgDark(this)
    ClickUtils.applySingleDebouncing(this) { action(this) }
}

