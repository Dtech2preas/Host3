package com.preasx24.dtechtv.domain.repository

import com.preasx24.dtechtv.domain.model.Country

interface PlaylistRepository {
    suspend fun getAvailableCountries(): List<Country>
}
