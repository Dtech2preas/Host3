package com.preasx24.dtechtv.domain.model

data class Channel(
    val id: String,
    val name: String,
    val logo: String?,
    val group: String?,
    val streamUrl: String,
    val country: String?,
    val language: String?,
    val isFavorite: Boolean = false
)
