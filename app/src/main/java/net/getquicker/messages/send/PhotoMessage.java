package net.getquicker.messages.send;


import net.getquicker.messages.MessageBase;

public class PhotoMessage  implements MessageBase {
    public static final int MessageType = 105;

    public String FileName ;

    public String Data;

    @Override
    public int getMessageType() {
        return MessageType;
    }

}