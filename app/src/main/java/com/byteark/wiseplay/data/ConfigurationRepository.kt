package com.byteark.wiseplay.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.byteark.wiseplay.drm.WisePlayConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class DrmConfiguration(
    val videoUrl: String = WisePlayConstants.SampleContent.DEFAULT_VIDEO_URL,
    val licenseUrl: String = WisePlayConstants.SampleContent.DEFAULT_LICENSE_URL,
    val authToken: String = "",
    val userAgent: String = WisePlayConstants.DefaultHeaders.USER_AGENT,
    val customHeaders: Map<String, String> = emptyMap()
)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drm_config")

class ConfigurationRepository(private val context: Context) {
    
    companion object {
        val VIDEO_URL_KEY = stringPreferencesKey(WisePlayConstants.ConfigKeys.VIDEO_URL)
        val LICENSE_URL_KEY = stringPreferencesKey(WisePlayConstants.ConfigKeys.LICENSE_URL)
        val AUTH_TOKEN_KEY = stringPreferencesKey(WisePlayConstants.ConfigKeys.AUTH_TOKEN)
        val USER_AGENT_KEY = stringPreferencesKey(WisePlayConstants.ConfigKeys.USER_AGENT)
        val CUSTOM_HEADERS_KEY = stringPreferencesKey(WisePlayConstants.ConfigKeys.CUSTOM_HEADERS)
    }
    
    val configurationFlow: Flow<DrmConfiguration> = context.dataStore.data.map { preferences ->
        DrmConfiguration(
            videoUrl = preferences[VIDEO_URL_KEY] ?: WisePlayConstants.SampleContent.DEFAULT_VIDEO_URL,
            licenseUrl = preferences[LICENSE_URL_KEY] ?: WisePlayConstants.SampleContent.DEFAULT_LICENSE_URL,
            authToken = preferences[AUTH_TOKEN_KEY] ?: "",
            userAgent = preferences[USER_AGENT_KEY] ?: WisePlayConstants.DefaultHeaders.USER_AGENT,
            customHeaders = parseCustomHeaders(preferences[CUSTOM_HEADERS_KEY] ?: "")
        )
    }
    
    suspend fun updateVideoUrl(videoUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[VIDEO_URL_KEY] = videoUrl
        }
    }
    
    suspend fun updateLicenseUrl(licenseUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[LICENSE_URL_KEY] = licenseUrl
        }
    }
    
    suspend fun updateAuthToken(authToken: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = authToken
        }
    }
    
    suspend fun updateUserAgent(userAgent: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_AGENT_KEY] = userAgent
        }
    }
    
    suspend fun updateCustomHeaders(headers: Map<String, String>) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOM_HEADERS_KEY] = serializeCustomHeaders(headers)
        }
    }
    
    suspend fun updateConfiguration(config: DrmConfiguration) {
        context.dataStore.edit { preferences ->
            preferences[VIDEO_URL_KEY] = config.videoUrl
            preferences[LICENSE_URL_KEY] = config.licenseUrl
            preferences[AUTH_TOKEN_KEY] = config.authToken
            preferences[USER_AGENT_KEY] = config.userAgent
            preferences[CUSTOM_HEADERS_KEY] = serializeCustomHeaders(config.customHeaders)
        }
    }
    
    private fun parseCustomHeaders(headerString: String): Map<String, String> {
        if (headerString.isEmpty()) return emptyMap()
        
        return try {
            headerString.split("|").mapNotNull { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) {
                    parts[0].trim() to parts[1].trim()
                } else null
            }.toMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    private fun serializeCustomHeaders(headers: Map<String, String>): String {
        return headers.entries.joinToString("|") { "${it.key}:${it.value}" }
    }
}