package com.preasx24.dtechtv.mobileui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preasx24.dtechtv.domain.model.Channel
import com.preasx24.dtechtv.domain.usecase.GetChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getChannelsUseCase: GetChannelsUseCase
) : ViewModel() {

    val channels: StateFlow<List<Channel>> = getChannelsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
