package net.getquicker.messages.recv

import net.getquicker.messages.MessageBase

class UpdateButtonsMessage : MessageBase {
    var ProfileName: String? = null

    /// 全局面板数量
    var GlobalPageCount = 0

    /// <summary>
    /// 当前全局面板编号
    /// </summary>
    var GlobalPageIndex = 0

    /// <summary>
    /// 上下文面板数量
    /// </summary>
    var ContextPageCount = 0

    /// <summary>
    /// 当前上下文面板编号
    /// </summary>
    var ContextPageIndex = 0

    /// <summary>
    /// 上下文面板切换是否锁定
    /// </summary>
    var IsContextPanelLocked = false

    //
    var Buttons: List<ButtonItem>? = null

    /// <summary>
    /// 要更新的每个按钮的信息
    /// </summary>
    class ButtonItem {
        /// <summary>
        /// 编号
        /// </summary>
        var Index = 0

        /// <summary>
        /// 是否启用
        /// </summary>
        var IsEnabled = false

        /// <summary>
        /// 按钮文字标签
        /// </summary>
        var Label: String? = null

        /// <summary>
        /// 图标文件名
        /// </summary>
        var IconFileName: String? = null

        /// <summary>
        /// base64编码的图标文件内容
        /// </summary>
        var IconFileContent: String? = null
    }

    companion object {
        const val MessageType = 1
    }

    override val messageType: Int
        get() = MessageType
}