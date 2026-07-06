package com.preasx24.dtechtv.mobileui.player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.preasx24.dtechtv.player.ui.VideoPlayer
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun PlayerScreen(
    encodedStreamUrl: String
) {
    val streamUrl = URLDecoder.decode(encodedStreamUrl, StandardCharsets.UTF_8.toString())
    VideoPlayer(
        streamUrl = streamUrl,
        modifier = Modifier.fillMaxSize()
    )
}
