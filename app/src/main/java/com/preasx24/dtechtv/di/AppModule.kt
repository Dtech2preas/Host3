package com.preasx24.dtechtv.di

import android.content.Context
import androidx.room.Room
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
    fun provideIptvOrgApi(): IptvOrgApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
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
        api: IptvOrgApi
    ): ChannelRepository {
        return ChannelRepositoryImpl(dao, api)
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(api: IptvOrgApi): PlaylistRepository {
        return PlaylistRepositoryImpl(api)
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
