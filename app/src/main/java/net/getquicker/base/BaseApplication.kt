package net.getquicker.base

import android.app.Application
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import dagger.hilt.android.HiltAndroidApp
import net.getquicker.BuildConfig
import net.getquicker.R

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        ToastUtils.getDefaultMaker().setBgResource(R.color.blue)
            .setTextColor(ContextCompat.getColor(this, R.color.white))
        LogUtils.getConfig().isLogSwitch = BuildConfig.DEBUG
    }
}