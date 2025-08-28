package com.byteark.wiseplay.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteark.wiseplay.data.DrmConfiguration
import com.byteark.wiseplay.drm.WisePlayConstants
import com.byteark.wiseplay.ui.viewmodels.ConfigurationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    onNavigateBack: () -> Unit,
    viewModel: ConfigurationViewModel = viewModel()
) {
    val configuration by viewModel.configuration.collectAsState()
    var videoUrl by remember { mutableStateOf(configuration.videoUrl) }
    var licenseUrl by remember { mutableStateOf(configuration.licenseUrl) }
    var authToken by remember { mutableStateOf(configuration.authToken) }
    var userAgent by remember { mutableStateOf(configuration.userAgent) }
    var customHeaders by remember { mutableStateOf(configuration.customHeaders.toMutableMap()) }
    var newHeaderKey by remember { mutableStateOf("") }
    var newHeaderValue by remember { mutableStateOf("") }
    var showAddHeaderDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    
    // Update local state when configuration changes
    LaunchedEffect(configuration) {
        videoUrl = configuration.videoUrl
        licenseUrl = configuration.licenseUrl
        authToken = configuration.authToken
        userAgent = configuration.userAgent
        customHeaders = configuration.customHeaders.toMutableMap()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Configuration") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Video URL Section
            item {
                ConfigurationSection(title = "Video Configuration") {
                    OutlinedTextField(
                        value = videoUrl,
                        onValueChange = { videoUrl = it },
                        label = { Text("Video URL") },
                        placeholder = { Text(WisePlayConstants.SampleContent.DEFAULT_VIDEO_URL) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            }

            // License URL Section
            item {
                ConfigurationSection(title = "DRM Configuration") {
                    OutlinedTextField(
                        value = licenseUrl,
                        onValueChange = { licenseUrl = it },
                        label = { Text("License Server URL") },
                        placeholder = { Text(WisePlayConstants.SampleContent.DEFAULT_LICENSE_URL) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        singleLine = false,
                        maxLines = 3
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = authToken,
                        onValueChange = { authToken = it },
                        label = { Text("Authentication Token (Optional)") },
                        placeholder = { Text("Bearer token or API key") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 2
                    )
                }
            }

            // User Agent Section
            item {
                ConfigurationSection(title = "Request Configuration") {
                    OutlinedTextField(
                        value = userAgent,
                        onValueChange = { userAgent = it },
                        label = { Text("User Agent") },
                        placeholder = { Text("Custom User Agent") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Custom Headers Section
            item {
                ConfigurationSection(title = "Custom Headers") {
                    if (customHeaders.isEmpty()) {
                        Text(
                            text = "No custom headers configured",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        customHeaders.forEach { (key, value) ->
                            HeaderItem(
                                key = key,
                                value = value,
                                onDelete = { customHeaders.remove(key) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = { showAddHeaderDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Custom Header")
                    }
                }
            }

            // Preset Configurations
            item {
                ConfigurationSection(title = "Quick Setup") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(getPresetConfigurations()) { preset ->
                            FilterChip(
                                onClick = {
                                    videoUrl = preset.videoUrl
                                    licenseUrl = preset.licenseUrl
                                    authToken = preset.authToken
                                    userAgent = preset.userAgent
                                    customHeaders = preset.customHeaders.toMutableMap()
                                },
                                label = { Text(preset.name) },
                                selected = false
                            )
                        }
                    }
                }
            }
        }

        // Save Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    isSaving = true
                    val newConfig = DrmConfiguration(
                        videoUrl = videoUrl.trim(),
                        licenseUrl = licenseUrl.trim(),
                        authToken = authToken.trim(),
                        userAgent = userAgent.trim(),
                        customHeaders = customHeaders.toMap()
                    )
                    viewModel.updateConfiguration(newConfig)
                    isSaving = false
                },
                enabled = !isSaving && isValidConfiguration(videoUrl, licenseUrl),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isSaving) "Saving..." else "Save Configuration",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    // Add Header Dialog
    if (showAddHeaderDialog) {
        AlertDialog(
            onDismissRequest = { showAddHeaderDialog = false },
            title = { Text("Add Custom Header") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newHeaderKey,
                        onValueChange = { newHeaderKey = it },
                        label = { Text("Header Name") },
                        placeholder = { Text("e.g., Authorization") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newHeaderValue,
                        onValueChange = { newHeaderValue = it },
                        label = { Text("Header Value") },
                        placeholder = { Text("e.g., Bearer token123") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newHeaderKey.isNotBlank() && newHeaderValue.isNotBlank()) {
                            customHeaders[newHeaderKey.trim()] = newHeaderValue.trim()
                            newHeaderKey = ""
                            newHeaderValue = ""
                            showAddHeaderDialog = false
                        }
                    },
                    enabled = newHeaderKey.isNotBlank() && newHeaderValue.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddHeaderDialog = false
                    newHeaderKey = ""
                    newHeaderValue = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ConfigurationSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun HeaderItem(
    key: String,
    value: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = key,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete header",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

data class PresetConfiguration(
    val name: String,
    val videoUrl: String,
    val licenseUrl: String,
    val authToken: String = "",
    val userAgent: String,
    val customHeaders: Map<String, String> = emptyMap()
)

private fun getPresetConfigurations(): List<PresetConfiguration> {
    return listOf(
        PresetConfiguration(
            name = "Default WisePlay DRM Content",
            videoUrl = WisePlayConstants.SampleContent.DEFAULT_VIDEO_URL,
            licenseUrl = WisePlayConstants.SampleContent.DEFAULT_LICENSE_URL,
            userAgent = "WisePlayDRMExampleApp/1.0"
        )
    )
}

private fun isValidConfiguration(videoUrl: String, licenseUrl: String): Boolean {
    return videoUrl.isNotBlank() && 
           (videoUrl.startsWith("http://") || videoUrl.startsWith("https://"))
}