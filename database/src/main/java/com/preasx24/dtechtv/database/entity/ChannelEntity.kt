package com.preasx24.dtechtv.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.preasx24.dtechtv.domain.model.Channel

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val logo: String?,
    val groupName: String?,
    val streamUrl: String,
    val country: String?,
    val language: String?,
    val isFavorite: Boolean = false
) {
    fun toDomain() = Channel(
        id = id,
        name = name,
        logo = logo,
        group = groupName,
        streamUrl = streamUrl,
        country = country,
        language = language,
        isFavorite = isFavorite
    )
}
