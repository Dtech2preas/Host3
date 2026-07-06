package com.preasx24.dtechtv.domain.usecase

import com.preasx24.dtechtv.domain.model.Country
import com.preasx24.dtechtv.domain.repository.PlaylistRepository

class GetCountriesUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(): List<Country> = playlistRepository.getAvailableCountries()
}
