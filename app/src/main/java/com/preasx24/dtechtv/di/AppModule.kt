package com.preasx24.dtechtv.di

import android.content.Context
import androidx.room.Room
import com.preasx24.dtechtv.core.logger.AppLogger
import com.preasx24.dtechtv.data.repository.ChannelRepositoryImpl
import com.preasx24.dtechtv.data.repository.PlaylistRepositoryImpl
import com.preasx24.dtechtv.database.DTechTvDatabase
import com.preasx24.dtechtv.database.dao.ChannelDao
import com.preasx24.dtechtv.domain.repository.ChannelRepository
import com.preasx24.dtechtv.domain.repository.PlaylistRepository
import com.preasx24.dtechtv.domain.usecase.GetChannelsUseCase
import com.preasx24.dtechtv.domain.usecase.GetCountriesUseCase
import com.preasx24.dtechtv.domain.usecase.SyncPlaylistUseCase
import com.preasx24.dtechtv.network.api.IptvOrgApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DTechTvDatabase {
        return Room.databaseBuilder(
            context,
            DTechTvDatabase::class.java,
            "dtechtv.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChannelDao(db: DTechTvDatabase): ChannelDao = db.channelDao

    @Provides
    @Singleton
    fun provideIptvOrgApi(appLogger: AppLogger): IptvOrgApi {
        val logging = HttpLoggingInterceptor { message ->
            appLogger.log("OkHttp: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val customInterceptor = Interceptor { chain ->
            val request = chain.request()
            appLogger.log("Network -> Sending request: ${request.method} ${request.url}")
            try {
                val response = chain.proceed(request)
                appLogger.log("Network <- Received response for ${response.request.url} with status code ${response.code}")
                response
            } catch (e: Exception) {
                appLogger.log("Network <! Request failed: ${e.message}")
                throw e
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(customInterceptor)
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://iptv-org.github.io/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IptvOrgApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChannelRepository(
        dao: ChannelDao,
        api: IptvOrgApi,
        appLogger: AppLogger
    ): ChannelRepository {
        return ChannelRepositoryImpl(dao, api, appLogger)
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(api: IptvOrgApi, appLogger: AppLogger): PlaylistRepository {
        return PlaylistRepositoryImpl(api, appLogger)
    }

    @Provides
    @Singleton
    fun provideGetChannelsUseCase(repo: ChannelRepository): GetChannelsUseCase {
        return GetChannelsUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideSyncPlaylistUseCase(repo: ChannelRepository): SyncPlaylistUseCase {
        return SyncPlaylistUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideGetCountriesUseCase(repo: PlaylistRepository): GetCountriesUseCase {
        return GetCountriesUseCase(repo)
    }
}
