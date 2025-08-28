package com.byteark.wiseplay.drm

import android.media.MediaDrm
import android.media.UnsupportedSchemeException
import android.os.Build

data class DrmCapability(
    val isWisePlaySupported: Boolean,
    val vendor: String?,
    val version: String?,
    val description: String?,
    val algorithms: List<String>,
    val securityLevel: String?,
    val systemId: String?,
    val hdcpLevel: String?,
    val maxHdcpLevel: String?,
    val maxNumberOfSessions: String?,
    val usageReportingSupport: String?,
    val errorMessage: String? = null
)

class DrmCapabilityChecker {
    
    fun checkWisePlaySupport(): DrmCapability {
        return try {
            val isSupported = MediaDrm.isCryptoSchemeSupported(WisePlayConstants.WISEPLAY_UUID)
            
            if (!isSupported) {
                return DrmCapability(
                    isWisePlaySupported = false,
                    vendor = null,
                    version = null,
                    description = null,
                    algorithms = emptyList(),
                    securityLevel = null,
                    systemId = null,
                    hdcpLevel = null,
                    maxHdcpLevel = null,
                    maxNumberOfSessions = null,
                    usageReportingSupport = null,
                    errorMessage = "WisePlay DRM is not supported on this device"
                )
            }
            
            // Get detailed DRM information
            val mediaDrm = MediaDrm(WisePlayConstants.WISEPLAY_UUID)
            val attributes = getDrmAttributes(mediaDrm)
            mediaDrm.release()
            
            DrmCapability(
                isWisePlaySupported = true,
                vendor = attributes["vendor"],
                version = attributes["version"],
                description = attributes["description"],
                algorithms = getAlgorithms(attributes["algorithms"]),
                securityLevel = attributes["securityLevel"],
                systemId = attributes["systemId"],
                hdcpLevel = attributes["hdcpLevel"],
                maxHdcpLevel = attributes["maxHdcpLevel"],
                maxNumberOfSessions = attributes["maxNumberOfSessions"],
                usageReportingSupport = attributes["usageReportingSupport"]
            )
            
        } catch (e: UnsupportedSchemeException) {
            DrmCapability(
                isWisePlaySupported = false,
                vendor = null,
                version = null,
                description = null,
                algorithms = emptyList(),
                securityLevel = null,
                systemId = null,
                hdcpLevel = null,
                maxHdcpLevel = null,
                maxNumberOfSessions = null,
                usageReportingSupport = null,
                errorMessage = "WisePlay DRM scheme is not supported: ${e.message}"
            )
        } catch (e: Exception) {
            DrmCapability(
                isWisePlaySupported = false,
                vendor = null,
                version = null,
                description = null,
                algorithms = emptyList(),
                securityLevel = null,
                systemId = null,
                hdcpLevel = null,
                maxHdcpLevel = null,
                maxNumberOfSessions = null,
                usageReportingSupport = null,
                errorMessage = "Error checking WisePlay DRM support: ${e.message}"
            )
        }
    }
    
    private fun getDrmAttributes(mediaDrm: MediaDrm): Map<String, String> {
        val attributes = mutableMapOf<String, String>()
        
        // List of property names to query
        val propertyNames = listOf(
            "vendor",
            "version",
            "description",
            "algorithms",
            "securityLevel",
            "systemId",
            "hdcpLevel",
            "maxHdcpLevel",
            "maxNumberOfSessions",
            "usageReportingSupport"
        )
        
        propertyNames.forEach { property ->
            try {
                val value = mediaDrm.getPropertyString(property)
                attributes[property] = value ?: "N/A"
            } catch (e: Exception) {
                attributes[property] = "Not available"
            }
        }
        
        return attributes
    }
    
    private fun getAlgorithms(algorithmsString: String?): List<String> {
        return algorithmsString?.split(",")?.map { it.trim() } ?: emptyList()
    }
    
    fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "Device Model" to "${Build.MANUFACTURER} ${Build.MODEL}",
            "Android Version" to "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            "Build ID" to Build.ID,
            "Hardware" to Build.HARDWARE,
            "Board" to Build.BOARD,
            "Brand" to Build.BRAND
        )
    }
}