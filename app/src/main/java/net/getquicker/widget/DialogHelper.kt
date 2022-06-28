package net.getquicker.widget

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.OnRationaleListener.ShouldRequest
import net.getquicker.R

fun showRationaleDialog(context: Context?, shouldRequest: ShouldRequest) {
    AlertDialog.Builder(context)
        .setTitle(android.R.string.dialog_alert_title)
        .setMessage(R.string.permission_rationale_message)
        .setPositiveButton(
            android.R.string.ok
        ) { _, _ -> shouldRequest.again(true) }
        .setNegativeButton(
            android.R.string.cancel
        ) { _, _ -> shouldRequest.again(false) }
        .setCancelable(false)
        .create()
        .show()
}

fun showOpenAppSettingDialog(context: Context?) {
    AlertDialog.Builder(context)
        .setTitle(android.R.string.dialog_alert_title)
        .setMessage(R.string.permission_denied_forever_message)
        .setPositiveButton(
            android.R.string.ok
        ) { _, _ -> PermissionUtils.launchAppDetailsSettings() }
        .setNegativeButton(
            android.R.string.cancel
        ) { _, _ -> }
        .setCancelable(false)
        .create()
        .show()
}

fun showAlertDialog(
    context: Activity, msg: String, forceUpdate: Boolean,
    negative: (() -> Unit)? = null, positive: () -> Unit
) {
    AlertDialog.Builder(context).apply {
        setTitle("有新版本")
        setMessage(msg)
        setPositiveButton("下载") { _, _ -> positive() }
        if (null != negative)
            setNegativeButton(
                if (forceUpdate) "退出" else "取消"
            ) { _, _ -> negative() }
        setCancelable(false)
        create()
        show()
    }


}