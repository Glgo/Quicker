package net.getquicker.net

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import net.getquicker.messages.MessageBase
import net.getquicker.messages.recv.LoginStateMessage
import net.getquicker.messages.recv.UpdateButtonsMessage
import net.getquicker.messages.recv.VolumeStateMessage
import net.getquicker.messages.send.ButtonClickedMessage
import net.getquicker.messages.send.CommandMessage
import net.getquicker.net.MyDataDecoder
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.CumulativeProtocolDecoder
import org.apache.mina.filter.codec.ProtocolDecoderOutput
import java.nio.charset.StandardCharsets

class MyDataDecoder : CumulativeProtocolDecoder() {
    /**
     * 返回值含义:
     * 1、当内容刚好时，返回false，告知父类接收下一批内容
     * 2、内容不够时需要下一批发过来的内容，此时返回false，这样父类 CumulativeProtocolDecoder
     * 会将内容放进IoSession中，等下次来数据后就自动拼装再交给本类的doDecode
     * 3、当内容多时，返回true，因为需要再将本批数据进行读取，父类会将剩余的数据再次推送本类的doDecode方法
     */
    @Throws(Exception::class)
    public override fun doDecode(
        session: IoSession,
        `in`: IoBuffer,
        out: ProtocolDecoderOutput
    ): Boolean {

        /* 
         * 假定消息格式为：消息头（int类型：表示消息体的长度、short类型：表示事件号）+消息体
         */
        if (`in`.remaining() < 12) {
            return false
        }

        //以便后继的reset操作能恢复position位置
        `in`.mark()
        val headFlag = `in`.int
        val msgType = `in`.int
        val msgLength = `in`.int
        return if (`in`.remaining() >= msgLength + 4) {
            val msgJson =
                `in`.getString(msgLength, StandardCharsets.UTF_8.newDecoder())
            val end = `in`.int
            val msg = deserializeMsg(msgType, msgJson)
            out.write(msg)
            `in`.hasRemaining()
        } else {
            `in`.reset()

            // 消息不完整
            false
        }
    }

    private fun deserializeMsg(msgType: Int, content: String): MessageBase? {
        LogUtils.json(LogUtils.I, content)
        val gson = Gson()
        when (msgType) {
            UpdateButtonsMessage.MessageType ->                 // update buttons
                return gson.fromJson(content, UpdateButtonsMessage::class.java)
            ButtonClickedMessage.MessageType -> return gson.fromJson(
                content,
                ButtonClickedMessage::class.java
            )
            VolumeStateMessage.MessageType -> return gson.fromJson(
                content,
                VolumeStateMessage::class.java
            )
            LoginStateMessage.MessageType -> return gson.fromJson(
                content, LoginStateMessage::class.java
            )
            CommandMessage.MessageType -> return gson.fromJson(content, CommandMessage::class.java)
            else -> Log.e(TAG, "不支持的消息类型：$msgType")
        }
        return null
    }

    companion object {
        private val TAG = MyDataDecoder::class.java.simpleName
    }
}