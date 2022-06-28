package net.getquicker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import net.getquicker.db.entity.Action

@Dao
interface ActionDao {
    @Insert
    fun insert(vararg action: Action)

    @Query("select * from `action` where device_name=:deviceName  order by name")
    fun getAllByName(deviceName: String): List<Action>

    @Query("select * from `action` order by name desc")
    fun getAllByNameDesc(): List<Action>

    @Query("select * from `action` order by count")
    fun getAllByCount(): List<Action>

    @Query("select * from `action` order by count desc")
    fun getAllByCountDesc(): List<Action>

    @Query("select * from `action` order by create_time")
    fun getAllByCreateTime(): List<Action>

    @Query("select * from `action` order by create_time desc")
    fun getAllByCreateTimeDesc(): List<Action>

    @Query("select * from `action` order by last_run_time")
    fun getAllByLastRunTime(): List<Action>

    @Query("select * from `action` order by last_run_time desc")
    fun getAllByLastRunTimeDesc(): List<Action>

    @Delete
    fun delete(action: Action)

    @Query("DELETE FROM `action` WHERE name=:name ")
    fun deleteByName(name: String)
}