package net.getquicker.utils


import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.FullCallback
import com.blankj.utilcode.util.PermissionUtils.OnRationaleListener.ShouldRequest
import net.getquicker.widget.showOpenAppSettingDialog
import net.getquicker.widget.showRationaleDialog

//拍照（包含请求读写存储卡权限）
fun requestCamera(listener: () -> Unit) =
    request(
        listener,
        permissions = arrayOf(PermissionConstants.CAMERA, PermissionConstants.STORAGE)
    )

fun requestLocation(listener: () -> Unit) =
    request(
        listener,
        permissions = arrayOf(PermissionConstants.LOCATION)
    )

//录音（包含请求读写存储卡权限）
fun requestRecord(listener: () -> Unit) =
    request(
        listener,
        permissions = arrayOf(PermissionConstants.MICROPHONE,PermissionConstants.STORAGE)
    )

/**
 * 请求读写存储卡权限
 * context必须为application
 */
fun requestStorage(listener: () -> Unit) =
    request(
        listener,
        permissions = arrayOf(PermissionConstants.STORAGE)
    )

private inline fun request(
    crossinline granted: () -> Unit,
    crossinline denied: (() -> Unit) = {},
    vararg permissions: String
) = PermissionUtils.permission(*permissions)
    .rationale { activity, shouldRequest: ShouldRequest ->
        showRationaleDialog(activity, shouldRequest)
    }
    .callback(object : FullCallback {
        override fun onGranted(permissionsGranted: List<String>) {
            granted()
        }

        override fun onDenied(
            permissionsDeniedForever: List<String>,
            permissionsDenied: List<String>
        ) {
            if (permissionsDeniedForever.isNotEmpty()) {
                showOpenAppSettingDialog(ActivityUtils.getTopActivity())
            }
            denied.invoke()
        }
    })
    .request()

