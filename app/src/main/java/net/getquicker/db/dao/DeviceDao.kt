package net.getquicker.db.dao

import androidx.room.*
import net.getquicker.db.entity.Device

@Dao
interface DeviceDao {
    @Insert
    fun insert(vararg device: Device)

    @Query("select * from `device` where name=:name")
    fun getDeviceByName(name: String): List<Device>

    /**
     * 获取选中的设备
     */
    @Query("select * from `device` where isSelect='true'")
    fun getAllSelectDevices(): List<Device>

    /**
     * 根据名称更新选中状态
     */
    @Query("update device set isSelect =:isSelect where name=:name")
    fun selectDevice(name: String, isSelect: Boolean)

    @Update
    fun update(vararg device: Device)

    @Delete
    fun delete(device: Device)
}