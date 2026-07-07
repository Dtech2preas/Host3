package com.preasx24.dtechtv.domain.usecase

import com.preasx24.dtechtv.domain.model.Channel
import com.preasx24.dtechtv.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow

class GetChannelsUseCase(
    private val channelRepository: ChannelRepository
) {
    operator fun invoke(): Flow<List<Channel>> = channelRepository.getChannels()
}
