package com.preasx24.dtechtv.network.model

import com.google.gson.annotations.SerializedName
import com.preasx24.dtechtv.domain.model.Country

data class CountryDto(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String,
    @SerializedName("flag") val flag: String
) {
    fun toDomain() = Country(
        code = code,
        name = name,
        flag = flag
    )
}
