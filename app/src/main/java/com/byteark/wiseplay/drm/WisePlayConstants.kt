package com.byteark.wiseplay.drm

import java.util.UUID

object WisePlayConstants {
    /**
     * WisePlay DRM UUID as defined in the WisePlay specification
     * UUID: 3D5E6D35-9B9A-41E8-B843-DD3C6E72C42C
     */
    val WISEPLAY_UUID: UUID = UUID.fromString("3d5e6d35-9b9a-41e8-b843-dd3c6e72c42c")

    /**
     * Sample WisePlay DRM test video URLs for testing
     * These should be replaced with actual WisePlay DRM protected content
     */
    object SampleContent {
        // Default test content (replace with actual WisePlay DRM content)
        const val DEFAULT_VIDEO_URL = "https://byteark-dev-video-sample.st-th-1.byteark.com/wiseplay/big_buck_bunny/playlist.mpd"
        const val DEFAULT_LICENSE_URL = "https://byteark-drm.develop.poring.arkcube.com/wp/license/testwiseplayservice/bunnywiseplay"
    }
    
    /**
     * Configuration keys for SharedPreferences
     */
    object ConfigKeys {
        const val VIDEO_URL = "video_url"
        const val LICENSE_URL = "license_url"
        const val CUSTOM_HEADERS = "custom_headers"
        const val AUTH_TOKEN = "auth_token"
        const val USER_AGENT = "user_agent"
    }
    
    /**
     * Default headers for DRM requests
     */
    object DefaultHeaders {
        const val USER_AGENT = "WisePlayDRMExampleApp/1.0"
        const val CONTENT_TYPE = "application/json"
    }
}