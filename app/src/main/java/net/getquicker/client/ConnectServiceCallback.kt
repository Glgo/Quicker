package net.getquicker.client

interface ConnectServiceCallback {
    /**
     * 连接pc服务回调
     * @param isSuccess 连接状态
     * @param obj 状态相关信息，可为空！
     */
    fun connectCallback(isSuccess: Boolean, obj: Any?)
}