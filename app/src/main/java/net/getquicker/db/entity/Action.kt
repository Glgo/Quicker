package net.getquicker.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 *  author : Clay
 *  date : 2022/06/13 17:30:01
 *  description : 动作
 *  @param name 动作名称
 *  @param param 动作参数
 *  @param count 运行的次数
 *  @param createTime 创建动作的时间 格式 yyyy-MM-dd HH:mm:ss
 *  @param lastRunTime 最后一次运行动作的时间
 *  @param deviceName 设备名称
 */
@Entity
data class Action(
    val name: String,
    val param: String,
    val count: Int,
    @ColumnInfo(name = "create_time") val createTime: String,
    @ColumnInfo(name = "last_run_time") val lastRunTime: String,
    @ColumnInfo(name = "device_name") val deviceName: String
)