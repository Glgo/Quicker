package net.getquicker.messages.send;


import net.getquicker.messages.MessageBase;

public class ToggleMuteMessage implements MessageBase {
    public static final int MessageType = 102;

    @Override
    public int getMessageType() {
        return MessageType;
    }
}
