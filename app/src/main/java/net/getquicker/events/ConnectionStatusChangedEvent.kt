package net.getquicker.events

import net.getquicker.client.ConnectionStatus

/**
 * 到pc的连接状态改变了
 */
class ConnectionStatusChangedEvent(var status: ConnectionStatus, var message: String?)