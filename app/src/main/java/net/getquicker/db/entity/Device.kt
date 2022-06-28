package net.getquicker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  author : Clay
 *  date : 2022/06/14 10:26:47
 *  description : 设备
 */
@Entity
data class Device(
    val name: String, var isSelect: Boolean,
    var pushEmail: String? = null, var pushCode: String? = null, var toDevice: String? = "",
    var panelPort: String? = null, var panelCode: String? = null,
    var socketPort: String? = null, var socketCode: String? = null,
) {
    @PrimaryKey(autoGenerate = true)
    var key: Long = 0
}