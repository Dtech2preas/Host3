package com.preasx24.dtechtv.mobileui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preasx24.dtechtv.core.preferences.AppPreferences
import com.preasx24.dtechtv.domain.model.Country
import com.preasx24.dtechtv.domain.usecase.GetCountriesUseCase
import com.preasx24.dtechtv.domain.usecase.SyncPlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase,
    private val syncPlaylistUseCase: SyncPlaylistUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    init {
        fetchCountries()
    }

    private fun fetchCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            _countries.value = getCountriesUseCase()
            _isLoading.value = false
        }
    }

    fun selectCountryAndSync(countryCode: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _syncStatus.value = "Downloading and parsing playlist..."

            try {
                syncPlaylistUseCase(countryCode)
                appPreferences.setOnboardingCompleted(true)
                onComplete()
            } catch (e: Exception) {
                _syncStatus.value = "Failed to sync. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
