package com.preasx24.dtechtv.player

data class PlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val error: String? = null
)
