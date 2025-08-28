package com.byteark.wiseplay.drm

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.drm.MediaDrmCallback
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@UnstableApi
class WisePlayMediaDrmCallback(
    private val licenseUrl: String,
    private val authToken: String = "",
    private val userAgent: String = WisePlayConstants.DefaultHeaders.USER_AGENT,
    private val customHeaders: Map<String, String> = emptyMap()
) : MediaDrmCallback {

    override fun executeProvisionRequest(uuid: java.util.UUID, request: androidx.media3.exoplayer.drm.ExoMediaDrm.ProvisionRequest): ByteArray {
        // Handle provisioning request for WisePlay DRM
        val url = request.defaultUrl
        val data = request.data
        
        return executeHttpRequest(url, data, "application/json")
    }

    override fun executeKeyRequest(uuid: java.util.UUID, request: androidx.media3.exoplayer.drm.ExoMediaDrm.KeyRequest): ByteArray {
        // Handle key request for WisePlay DRM
        val url = if (request.licenseServerUrl?.isNotEmpty() == true) {
            request.licenseServerUrl
        } else {
            licenseUrl
        }
        
        val data = request.data
        
        return executeHttpRequest(url, data, "application/octet-stream")
    }

    private fun executeHttpRequest(url: String, data: ByteArray, contentType: String): ByteArray {
        var connection: HttpURLConnection? = null
        
        try {
            connection = URL(url).openConnection() as HttpURLConnection
            
            // Set request method and properties
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", contentType)
            connection.setRequestProperty("User-Agent", userAgent)
            
            // Add authentication if available
            if (authToken.isNotEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer $authToken")
            }
            
            // Add custom headers
            customHeaders.forEach { (key, value) ->
                connection.setRequestProperty(key, value)
            }
            
            connection.doOutput = true
            connection.connectTimeout = 10000 // 10 seconds
            connection.readTimeout = 10000 // 10 seconds
            
            // Write request data
            connection.outputStream.use { outputStream ->
                outputStream.write(data)
                outputStream.flush()
            }
            
            // Check response code
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("HTTP request failed with response code: $responseCode")
            }
            
            // Read response
            return connection.inputStream.use { inputStream ->
                inputStream.readBytes()
            }
            
        } catch (e: Exception) {
            throw IOException("Failed to execute DRM request: ${e.message}", e)
        } finally {
            connection?.disconnect()
        }
    }
}