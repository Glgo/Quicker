package net.getquicker.client

import net.getquicker.messages.recv.UpdateButtonsMessage
import net.getquicker.messages.recv.VolumeStateMessage

/**
 * 应用状态
 */
object MessageCache {
    var lastUpdateButtonsMessage: UpdateButtonsMessage? = null
    var lastVolumeStateMessage: VolumeStateMessage? =        null
}