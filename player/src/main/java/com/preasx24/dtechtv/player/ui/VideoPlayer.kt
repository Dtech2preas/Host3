package com.preasx24.dtechtv.player.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.preasx24.dtechtv.player.ExoPlayerManager

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(
    streamUrl: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val playerManager = remember { ExoPlayerManager(context) }

    DisposableEffect(streamUrl) {
        playerManager.initializePlayer()
        playerManager.playChannel(streamUrl)
        onDispose {
            playerManager.releasePlayer()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = playerManager.player
                useController = true
                setShowNextButton(false)
                setShowPreviousButton(false)
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            view.player = playerManager.player
        }
    )
}
