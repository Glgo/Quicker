package net.getquicker.client

/**
 * PC 连接状态
 */
enum class ConnectionStatus {
    Disconnected,  //未连接
    Connecting,  //连接中
    Connected,  //已连接
    LoggedIn,  // 已登录
    LoginFailed    // 登录失败
}