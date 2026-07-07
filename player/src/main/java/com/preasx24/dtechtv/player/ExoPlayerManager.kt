package com.preasx24.dtechtv.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExoPlayerManager(private val context: Context) {

    private var _player: ExoPlayer? = null
    val player: ExoPlayer? get() = _player

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    fun initializePlayer() {
        if (_player == null) {
            _player = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        _playerState.value = _playerState.value.copy(
                            isBuffering = playbackState == Player.STATE_BUFFERING
                        )
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        _playerState.value = _playerState.value.copy(
                            error = error.localizedMessage
                        )
                    }
                })
            }
        }
    }

    fun playChannel(streamUrl: String) {
        val mediaItem = MediaItem.fromUri(streamUrl)
        _player?.setMediaItem(mediaItem)
        _player?.prepare()
        _player?.play()
        _playerState.value = PlayerState(isBuffering = true)
    }

    fun releasePlayer() {
        _player?.release()
        _player = null
        _playerState.value = PlayerState()
    }
}
