package net.getquicker.utils

import com.blankj.utilcode.util.SPUtils

object SpUtil {
    private const val ipTag = "ip"
    private const val portTag = "port"
    private const val panelCodeTag = "panelCode"
    private const val emailTag = "email"
    private const val pushCodeTag = "pushCode"
    private const val toDevicesTag = "toDevices"
    private const val isClearTag = "isClear"
    private const val socketPortTag = "socketPort"
    private const val socketCodeTag = "socketCode"

    var ip: String
        get() {
            if (UserInfo.ip == null) {
                UserInfo.ip = SPUtils.getInstance().getString(ipTag, "")
            }
            return UserInfo.ip!!
        }
        set(value) {
            UserInfo.ip = value
            SPUtils.getInstance().put(ipTag, value)
        }
    var panelPort: Int
        get() {
            if (UserInfo.panelPort == null) {
                UserInfo.panelPort = SPUtils.getInstance().getInt(portTag, 0)
            }
            return UserInfo.panelPort!!
        }
        set(value) {
            UserInfo.panelPort = value
            SPUtils.getInstance().put(portTag, value)
        }
    var panelCode: String
        get() {
            if (UserInfo.panelCode == null) {
                UserInfo.panelCode = SPUtils.getInstance().getString(panelCodeTag, "")
            }
            return UserInfo.panelCode!!
        }
        set(value) {
            UserInfo.panelCode = value
            SPUtils.getInstance().put(panelCodeTag, value)
        }

    var email: String
        get() {
            if (UserInfo.email == null) {
                UserInfo.email = SPUtils.getInstance().getString(emailTag, "")
            }
            return UserInfo.email!!
        }
        set(value) {
            UserInfo.email = value
            SPUtils.getInstance().put(emailTag, value)
        }

    var pushCode: String
        get() {
            if (UserInfo.pushCode == null) {
                UserInfo.pushCode = SPUtils.getInstance().getString(pushCodeTag, "")
            }
            return UserInfo.pushCode!!
        }
        set(value) {
            UserInfo.pushCode = value
            SPUtils.getInstance().put(pushCodeTag, value)
        }

    var toDevices: String
        get() {
            if (UserInfo.toDevices == null) {
                UserInfo.toDevices = SPUtils.getInstance().getString(toDevicesTag, "")
            }
            return UserInfo.toDevices!!
        }
        set(value) {
            UserInfo.toDevices = value
            SPUtils.getInstance().put(toDevicesTag, value)
        }

    var isClear: Boolean
        get() {
            if (UserInfo.isClear == null) {
                UserInfo.isClear = SPUtils.getInstance().getBoolean(isClearTag, false)
            }
            return UserInfo.isClear!!

        }
        set(value) {
            UserInfo.isClear = value
            SPUtils.getInstance().put(isClearTag, value)
        }
    var socketPort: Int
        get() {
            if (UserInfo.socketPort == null) {
                UserInfo.socketPort = SPUtils.getInstance().getInt(socketPortTag, 0)
            }
            return UserInfo.socketPort!!
        }
        set(value) {
            UserInfo.socketPort = value
            SPUtils.getInstance().put(socketPortTag, value)
        }
    var socketCode: String
        get() {
            if (UserInfo.socketCode == null) {
                UserInfo.socketCode = SPUtils.getInstance().getString(socketCodeTag, "")
            }
            return UserInfo.socketCode!!
        }
        set(value) {
            UserInfo.socketCode = value
            SPUtils.getInstance().put(socketCodeTag, value)
        }
}

object UserInfo {
    var email: String? = null
    var pushCode: String? = null
    var toDevices: String? = null
    var isClear: Boolean? = null
    var ip: String? = null
    var panelPort: Int? = null
    var panelCode: String? = null
    var socketPort: Int? = null
    var socketCode: String? = null
}