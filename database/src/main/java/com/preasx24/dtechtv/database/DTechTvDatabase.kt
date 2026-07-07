package com.preasx24.dtechtv.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.preasx24.dtechtv.database.entity.ChannelEntity
import com.preasx24.dtechtv.database.dao.ChannelDao

@Database(
    entities = [ChannelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DTechTvDatabase : RoomDatabase() {
    abstract val channelDao: ChannelDao
}
