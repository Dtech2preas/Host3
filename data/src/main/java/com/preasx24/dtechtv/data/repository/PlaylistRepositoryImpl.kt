package com.preasx24.dtechtv.data.repository

import com.preasx24.dtechtv.core.logger.AppLogger
import com.preasx24.dtechtv.domain.model.Country
import com.preasx24.dtechtv.domain.repository.PlaylistRepository
import com.preasx24.dtechtv.network.api.IptvOrgApi

class PlaylistRepositoryImpl(
    private val api: IptvOrgApi,
    private val appLogger: AppLogger
) : PlaylistRepository {

    override suspend fun getAvailableCountries(): List<Country> {
        appLogger.log("PlaylistRepository: Fetching available countries...")
        return try {
            val countries = api.getCountries().map { it.toDomain() }
            appLogger.log("PlaylistRepository: Successfully fetched ${countries.size} countries.")
            countries
        } catch (e: Exception) {
            appLogger.log("PlaylistRepository: Error fetching countries: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
