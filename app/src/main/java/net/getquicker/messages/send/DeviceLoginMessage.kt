package net.getquicker.messages.send

import net.getquicker.messages.MessageBase

/**
 * 通用命令消息
 */
class DeviceLoginMessage : MessageBase {
    /// <summary>
    /// 连接验证码，防止误连接
    /// </summary>
    var ConnectionCode: String? = null

    /// <summary>
    /// 客户端版本
    /// </summary>
    var Version: String? = null

    /// <summary>
    /// 设备名称
    /// </summary>
    var DeviceName: String? = null

    companion object {
       const val MessageType = 200
    }

    override val messageType: Int
        get() = MessageType
}