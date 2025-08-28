package com.byteark.wiseplay.drm

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.drm.ExoMediaDrm
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import com.byteark.wiseplay.data.DrmConfiguration

@UnstableApi
class WisePlayDrmSessionManagerProvider(
    private val context: Context,
    private val configuration: DrmConfiguration
) : DrmSessionManagerProvider {

    override fun get(mediaDrm: androidx.media3.common.MediaItem): DrmSessionManager {
        // Check if the media item requires WisePlay DRM
        val drmScheme = mediaDrm.localConfiguration?.drmConfiguration?.scheme
        
        return if (drmScheme == WisePlayConstants.WISEPLAY_UUID) {
            createWisePlayDrmSessionManager()
        } else {
            // Return default DRM session manager for other schemes
            DrmSessionManager.DRM_UNSUPPORTED
        }
    }

    private fun createWisePlayDrmSessionManager(): DrmSessionManager {
        try {
            // Create MediaDrm instance for WisePlay
            val mediaDrm: ExoMediaDrm = FrameworkMediaDrm.newInstance(WisePlayConstants.WISEPLAY_UUID)
            
            // Create custom callback for WisePlay license requests
            val callback = WisePlayMediaDrmCallback(
                licenseUrl = configuration.licenseUrl,
                authToken = configuration.authToken,
                userAgent = configuration.userAgent,
                customHeaders = configuration.customHeaders
            )
            
            // Create and configure DRM session manager
            return DefaultDrmSessionManager.Builder()
                .setUuidAndExoMediaDrmProvider(WisePlayConstants.WISEPLAY_UUID) { uuid ->
                    mediaDrm
                }
                .build(callback)
                
        } catch (e: Exception) {
            // Log error and return unsupported DRM session manager
            android.util.Log.e("WisePlayDRM", "Failed to create WisePlay DRM session manager", e)
            return DrmSessionManager.DRM_UNSUPPORTED
        }
    }
}

@UnstableApi
class WisePlayDrmSessionManagerFactory {
    
    companion object {
        fun create(
            context: Context,
            configuration: DrmConfiguration
        ): DrmSessionManager {
            return try {
                // Check if WisePlay DRM is supported
                if (!android.media.MediaDrm.isCryptoSchemeSupported(WisePlayConstants.WISEPLAY_UUID)) {
                    android.util.Log.w("WisePlayDRM", "WisePlay DRM is not supported on this device")
                    return DrmSessionManager.DRM_UNSUPPORTED
                }
                
                // Create MediaDrm instance
                val mediaDrm: ExoMediaDrm = FrameworkMediaDrm.newInstance(WisePlayConstants.WISEPLAY_UUID)
                
                // Create callback with configuration
                val callback = WisePlayMediaDrmCallback(
                    licenseUrl = configuration.licenseUrl,
                    authToken = configuration.authToken,
                    userAgent = configuration.userAgent,
                    customHeaders = configuration.customHeaders
                )
                
                // Create session manager with multi-session support
                DefaultDrmSessionManager.Builder()
                    .setUuidAndExoMediaDrmProvider(WisePlayConstants.WISEPLAY_UUID) { uuid ->
                        mediaDrm
                    }
                    .setMultiSession(true) // Enable key rotation support
                    .build(callback)
                    
            } catch (e: Exception) {
                android.util.Log.e("WisePlayDRM", "Failed to create WisePlay DRM session manager: ${e.message}", e)
                DrmSessionManager.DRM_UNSUPPORTED
            }
        }
    }
}