package com.preasx24.dtechtv.data.repository

import com.preasx24.dtechtv.domain.model.Country
import com.preasx24.dtechtv.domain.repository.PlaylistRepository
import com.preasx24.dtechtv.network.api.IptvOrgApi

class PlaylistRepositoryImpl(
    private val api: IptvOrgApi
) : PlaylistRepository {

    override suspend fun getAvailableCountries(): List<Country> {
        return try {
            api.getCountries().map { it.toDomain() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
