package com.preasx24.dtechtv.domain.repository

import com.preasx24.dtechtv.domain.model.Channel
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    fun getChannels(): Flow<List<Channel>>
    fun getFavorites(): Flow<List<Channel>>
    suspend fun toggleFavorite(channelId: String, isFavorite: Boolean)
    suspend fun syncChannels(countryCode: String)
}
