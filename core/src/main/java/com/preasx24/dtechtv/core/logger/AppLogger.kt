package com.preasx24.dtechtv.core.logger

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLogger @Inject constructor() {
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    fun log(message: String) {
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] $message"
        _logs.update { currentLogs ->
            val newLogs = currentLogs.toMutableList()
            newLogs.add(logEntry)
            if (newLogs.size > 1000) {
                newLogs.removeAt(0)
            }
            newLogs
        }
        android.util.Log.d("DTechTV_Logs", message)
    }

    fun clear() {
        _logs.value = emptyList()
    }
}
