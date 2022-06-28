package net.getquicker.messages.send

import net.getquicker.messages.MessageBase

class ButtonClickedMessage : MessageBase {
    companion object {
        const val MessageType = 101
    }

    var ButtonIndex = 0

    override val messageType: Int = MessageType
}