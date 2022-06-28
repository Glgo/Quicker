package net.getquicker.bean

/**
 *  author : Clay
 *  date : 2022/05/06 16:29:02
 *  description : WebSocket请求数据格式
 */
data class SendMsgBean(

    //操作类型
    val operation: String?,
    val data: String?,
    //操作类型为action时，指定动作的id或名称
    val action: String? = null,
    //消息类型标识，
    //2：命令请求消息，用于发送操作指令和内容。
    //4：命令响应消息，返回指令操作结果。
    //5：身份验证请求，客户端发送验证码。
    //6：身份验证响应，返回密码是否正确。
    val messageType: Int? = 2,
    //是否等待动作响应
    val wait: Boolean? = false,
    val serial: Int? = 0,//请求编号（不强制编号，可以直接写0）
    val extraData: String? = null//'可选的额外数据，文本或对象',
)