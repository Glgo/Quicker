package net.getquicker.net

import com.google.gson.Gson
import net.getquicker.messages.MessageBase
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolEncoderAdapter
import org.apache.mina.filter.codec.ProtocolEncoderOutput

/**
 * 编码器将数据直接发出去(不做处理)
 */
class MyDataEncoder : ProtocolEncoderAdapter() {

    override fun encode(
        session: IoSession, message: Any, out: ProtocolEncoderOutput
    ) {
        if (message is MessageBase) {
            val gson = Gson()
            val msgJson = gson.toJson(message)
            val msgBytes = msgJson.toByteArray(charset("utf-8"))
            val buffer = IoBuffer.allocate(msgBytes.size + 16)
            buffer.putInt(-0x1)
            buffer.putInt(message.messageType)
            buffer.putInt(msgBytes.size)
            buffer.put(msgBytes)
            buffer.putInt(0)
            buffer.flip()
            out.write(buffer)
        }
    }
}