package net.getquicker.net

import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolCodecFactory
import org.apache.mina.filter.codec.ProtocolDecoder
import org.apache.mina.filter.codec.ProtocolEncoder

class MyCodecFactory : ProtocolCodecFactory {
    private val decoder: MyDataDecoder = MyDataDecoder()
    private val encoder: MyDataEncoder = MyDataEncoder()

    override fun getDecoder(session: IoSession): ProtocolDecoder {
        return decoder
    }


    override fun getEncoder(session: IoSession): ProtocolEncoder {
        return encoder
    }

}