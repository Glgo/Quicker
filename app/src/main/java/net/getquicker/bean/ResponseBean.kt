package net.getquicker.bean

/**
 *  author : Clay
 *  date : 2022/05/06 16:27:02
 *  description : 响应消息格式
 */
data class ResponseBean(
    //2：命令请求消息，用于发送操作指令和内容。
    //4：命令响应消息，返回指令操作结果。
    //5：身份验证请求，客户端发送验证码。
    //6：身份验证响应，返回密码是否正确。
    /**
     * @link MessageType
     */
    val messageType: Int?,//固定为4
    val data: String?,//返回的数据
    val isSuccess: Boolean?,//是否成功响应
    val message: String?,//错误消息，正确为："ok"
    val replyTo: Int?,//响应的哪一条消息的Serial值。
    val extraData: String? = null//'可选的额外数据，文本或对象',
)