package net.getquicker.messages.recv;

import net.getquicker.messages.MessageBase;

// 登录状态消息
public class LoginStateMessage implements MessageBase {
    public static final int MessageType = 201;

    public Boolean IsLoggedIn;

    public int ErrorCode;

    public String ErrorMessage;


    @Override
    public int getMessageType() {
        return MessageType;
    }

}
