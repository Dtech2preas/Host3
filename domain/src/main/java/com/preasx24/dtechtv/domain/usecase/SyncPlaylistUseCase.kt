package com.preasx24.dtechtv.domain.usecase

import com.preasx24.dtechtv.domain.repository.ChannelRepository

class SyncPlaylistUseCase(
    private val channelRepository: ChannelRepository
) {
    suspend operator fun invoke(countryCode: String) {
        channelRepository.syncChannels(countryCode)
    }
}
