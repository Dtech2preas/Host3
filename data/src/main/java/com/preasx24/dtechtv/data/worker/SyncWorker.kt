package com.preasx24.dtechtv.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.preasx24.dtechtv.domain.repository.ChannelRepository

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val channelRepository: ChannelRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val countryCode = inputData.getString("countryCode") ?: return Result.failure()
            channelRepository.syncChannels(countryCode)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
