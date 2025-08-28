package com.byteark.wiseplay.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.byteark.wiseplay.drm.DrmCapability
import com.byteark.wiseplay.drm.DrmCapabilityChecker
import com.byteark.wiseplay.ui.theme.SuccessGreen
import com.byteark.wiseplay.ui.theme.ErrorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceCapabilityScreen(
    onNavigateToPlayer: () -> Unit
) {
    val context = LocalContext.current
    val capabilityChecker = remember { DrmCapabilityChecker() }
    
    var drmCapability by remember { mutableStateOf<DrmCapability?>(null) }
    var deviceInfo by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Check DRM capabilities in a coroutine to avoid blocking UI
        try {
            drmCapability = capabilityChecker.checkWisePlaySupport()
            deviceInfo = capabilityChecker.getDeviceInfo()
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "WisePlay DRM Capability Check",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Checking device capabilities...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // WisePlay DRM Support Status
                item {
                    WisePlaySupportCard(drmCapability)
                }

                // Device Information
                item {
                    DeviceInfoCard(deviceInfo)
                }

                // DRM Attributes (only if supported)
                drmCapability?.let { capability ->
                    if (capability.isWisePlaySupported) {
                        item {
                            DrmAttributesCard(capability)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Button
            drmCapability?.let { capability ->
                if (capability.isWisePlaySupported) {
                    Button(
                        onClick = onNavigateToPlayer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Go to Video Player",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    Button(
                        onClick = { /* No action - disabled */ },
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "WisePlay DRM Not Supported",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WisePlaySupportCard(capability: DrmCapability?) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "WisePlay DRM Support",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            capability?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (statusText, statusColor) = if (it.isWisePlaySupported) {
                        "✓ Supported" to SuccessGreen
                    } else {
                        "✗ Not Supported" to ErrorRed
                    }
                    
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                if (!it.isWisePlaySupported && it.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceInfoCard(deviceInfo: Map<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Device Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            deviceInfo.forEach { (key, value) ->
                InfoRow(label = key, value = value)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun DrmAttributesCard(capability: DrmCapability) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "WisePlay DRM Attributes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            InfoRow("Vendor", capability.vendor ?: "N/A")
            InfoRow("Version", capability.version ?: "N/A")
            InfoRow("Description", capability.description ?: "N/A")
            InfoRow("Security Level", capability.securityLevel ?: "N/A")
            InfoRow("System ID", capability.systemId ?: "N/A")
            InfoRow("HDCP Level", capability.hdcpLevel ?: "N/A")
            InfoRow("Max HDCP Level", capability.maxHdcpLevel ?: "N/A")
            InfoRow("Max Sessions", capability.maxNumberOfSessions ?: "N/A")
            InfoRow("Usage Reporting", capability.usageReportingSupport ?: "N/A")
            
            if (capability.algorithms.isNotEmpty()) {
                InfoRow("Algorithms", capability.algorithms.joinToString(", "))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}