package net.getquicker.client

import net.getquicker.events.ServerMessageEvent
import net.getquicker.events.SessionClosedEvent
import net.getquicker.messages.MessageBase
import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.IoSession
import org.greenrobot.eventbus.EventBus

class MinaClientHandler : IoHandlerAdapter() {
    //接收到服务器端消息
    override fun messageReceived(session: IoSession?, message: Any) {
        EventBus.getDefault().post(ServerMessageEvent(message as MessageBase))
    }

    override fun sessionClosed(session: IoSession) {
        EventBus.getDefault().post(SessionClosedEvent())
    }

    override fun inputClosed(session: IoSession?) {
        super.inputClosed(session)
        EventBus.getDefault().post(SessionClosedEvent())
    }
}