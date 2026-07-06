package com.preasx24.dtechtv.mobileui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.preasx24.dtechtv.core.ui.components.ChannelItem
import com.preasx24.dtechtv.domain.model.Channel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChannelSelected: (Channel) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val channels by viewModel.channels.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("D-TECH TV") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(channels) { channel ->
                ChannelItem(
                    channel = channel,
                    onClick = { onChannelSelected(channel) }
                )
            }
        }
    }
}
