package net.getquicker.utils

/**
 *  author : Clay
 *  date : 2022/06/20 09:57:46
 *  2：命令请求消息，用于发送操作指令和内容。
 *  4：命令响应消息，返回指令操作结果。
 *  5：身份验证请求，客户端发送验证码。
 *  6：身份验证响应，返回密码是否正确。
 */
object MessageType {
    const val push = 2
    const  val authReq = 5
    const val authResp = 6
}