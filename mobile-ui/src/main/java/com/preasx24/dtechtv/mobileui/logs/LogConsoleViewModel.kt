package com.preasx24.dtechtv.mobileui.logs

import androidx.lifecycle.ViewModel
import com.preasx24.dtechtv.core.logger.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LogConsoleViewModel @Inject constructor(
    private val appLogger: AppLogger
) : ViewModel() {
    val logs: StateFlow<List<String>> = appLogger.logs
}
