package com.preasx24.dtechtv.data.repository

import com.preasx24.dtechtv.core.logger.AppLogger
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
    private val api: IptvOrgApi,
    private val appLogger: AppLogger
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
            appLogger.log("SyncChannels: Starting sync for country $countryCode using URL $url")
            try {
                appLogger.log("SyncChannels: Downloading playlist...")
                val responseBody = api.downloadPlaylist(url)

                appLogger.log("SyncChannels: Parsing M3U content...")
                val channels = M3uParser.parse(responseBody.byteStream(), countryCode)
                appLogger.log("SyncChannels: Parsed ${channels.size} channels successfully.")

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

                appLogger.log("SyncChannels: Deleting old channels for $countryCode...")
                channelDao.deleteChannelsByCountry(countryCode)

                appLogger.log("SyncChannels: Inserting ${entities.size} new channels into database...")
                channelDao.insertChannels(entities)
                appLogger.log("SyncChannels: Sync completed successfully.")

            } catch (e: Exception) {
                appLogger.log("SyncChannels: Exception during sync - ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }
}
