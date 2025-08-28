package com.byteark.wiseplay.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.byteark.wiseplay.data.ConfigurationRepository
import com.byteark.wiseplay.data.DrmConfiguration
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfigurationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = ConfigurationRepository(application)
    
    val configuration: StateFlow<DrmConfiguration> = repository.configurationFlow
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = DrmConfiguration()
        )
    
    fun updateConfiguration(config: DrmConfiguration) {
        viewModelScope.launch {
            repository.updateConfiguration(config)
        }
    }
    
    fun updateVideoUrl(videoUrl: String) {
        viewModelScope.launch {
            repository.updateVideoUrl(videoUrl)
        }
    }
    
    fun updateLicenseUrl(licenseUrl: String) {
        viewModelScope.launch {
            repository.updateLicenseUrl(licenseUrl)
        }
    }
    
    fun updateAuthToken(authToken: String) {
        viewModelScope.launch {
            repository.updateAuthToken(authToken)
        }
    }
    
    fun updateUserAgent(userAgent: String) {
        viewModelScope.launch {
            repository.updateUserAgent(userAgent)
        }
    }
    
    fun updateCustomHeaders(headers: Map<String, String>) {
        viewModelScope.launch {
            repository.updateCustomHeaders(headers)
        }
    }
}