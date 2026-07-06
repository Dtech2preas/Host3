package com.preasx24.dtechtv.network.api

import com.preasx24.dtechtv.network.model.CountryDto
import retrofit2.http.GET
import retrofit2.http.Url
import okhttp3.ResponseBody

interface IptvOrgApi {
    @GET("https://iptv-org.github.io/api/countries.json")
    suspend fun getCountries(): List<CountryDto>

    @GET
    suspend fun downloadPlaylist(@Url url: String): ResponseBody
}
