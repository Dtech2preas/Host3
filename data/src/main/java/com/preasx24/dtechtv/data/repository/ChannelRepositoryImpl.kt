package com.preasx24.dtechtv.data.repository

import com.preasx24.dtechtv.database.dao.ChannelDao
import com.preasx24.dtechtv.database.entity.ChannelEntity
import com.preasx24.dtechtv.domain.model.Channel
import com.preasx24.dtechtv.domain.repository.ChannelRepository
import com.preasx24.dtechtv.network.api.IptvOrgApi
import com.preasx24.dtechtv.network.parser.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ChannelRepositoryImpl(
    private val channelDao: ChannelDao,
    private val api: IptvOrgApi
) : ChannelRepository {

    override fun getChannels(): Flow<List<Channel>> {
        return channelDao.getAllChannels().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavorites(): Flow<List<Channel>> {
        return channelDao.getFavoriteChannels().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavorite(channelId: String, isFavorite: Boolean) {
        channelDao.updateFavoriteStatus(channelId, isFavorite)
    }

    override suspend fun syncChannels(countryCode: String) {
        withContext(Dispatchers.IO) {
            val url = "https://iptv-org.github.io/iptv/countries/$countryCode.m3u"
            try {
                val responseBody = api.downloadPlaylist(url)
                val channels = M3uParser.parse(responseBody.byteStream(), countryCode)

                val entities = channels.map {
                    ChannelEntity(
                        id = it.id,
                        name = it.name,
                        logo = it.logo,
                        groupName = it.group,
                        streamUrl = it.streamUrl,
                        country = it.country,
                        language = it.language,
                        isFavorite = it.isFavorite
                    )
                }

                // For a robust sync, we might want to keep favorites.
                // Room REPLACE strategy handles conflicts, but overwrites fields.
                // A better approach is an UPSERT, but for MVP, REPLACE is fine.
                channelDao.deleteChannelsByCountry(countryCode)
                channelDao.insertChannels(entities)

            } catch (e: Exception) {
                // Log error or rethrow
                e.printStackTrace()
            }
        }
    }
}
