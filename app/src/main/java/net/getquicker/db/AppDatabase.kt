package net.getquicker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.getquicker.db.dao.ActionDao
import net.getquicker.db.dao.DeviceDao
import net.getquicker.db.entity.Action
import net.getquicker.db.entity.Device
import net.getquicker.utils.Constants

@Database(entities = [Action::class, Device::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            val builder =
                Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            return builder.build()
        }
    }
}