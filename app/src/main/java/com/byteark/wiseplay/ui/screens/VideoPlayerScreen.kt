package com.byteark.wiseplay.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.byteark.wiseplay.data.DrmConfiguration
import com.byteark.wiseplay.drm.WisePlayConstants
import com.byteark.wiseplay.drm.WisePlayDrmSessionManagerFactory
import com.byteark.wiseplay.ui.theme.White
import com.byteark.wiseplay.ui.theme.Black

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    configuration: DrmConfiguration,
    onNavigateToConfig: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var playerState by remember { mutableStateOf<Int>(Player.STATE_IDLE) }

    // Create ExoPlayer instance with WisePlay DRM support
    val exoPlayer = remember {
        // Create DRM session manager
        val drmSessionManager = WisePlayDrmSessionManagerFactory.create(context, configuration)
        
        // Create media source factory with DRM support
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(dataSourceFactory)
            .setDrmSessionManagerProvider { drmSessionManager }

        // Create ExoPlayer
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                // Set up player listener
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        playerState = playbackState
                        isLoading = playbackState == Player.STATE_BUFFERING
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        errorMessage = when (error.errorCode) {
                            PlaybackException.ERROR_CODE_DRM_PROVISIONING_FAILED -> 
                                "DRM provisioning failed. Please check your network connection."
                            PlaybackException.ERROR_CODE_DRM_LICENSE_ACQUISITION_FAILED -> 
                                "Failed to acquire DRM license. Please check your license server configuration."
                            PlaybackException.ERROR_CODE_DRM_DISALLOWED_OPERATION -> 
                                "DRM operation not allowed. Please check device capabilities."
                            PlaybackException.ERROR_CODE_DRM_DEVICE_REVOKED -> 
                                "Device has been revoked for DRM playback."
                            else -> "Playback error: ${error.message}"
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            errorMessage = null // Clear error when playback starts
                        }
                    }
                })
            }
    }

    // Setup media item and start playback
    LaunchedEffect(configuration.videoUrl) {
        try {
            val mediaItemBuilder = MediaItem.Builder()
                .setUri(configuration.videoUrl)

            // Add DRM configuration if this is WisePlay content
            if (configuration.licenseUrl.isNotEmpty()) {
                val drmConfiguration = MediaItem.DrmConfiguration.Builder(WisePlayConstants.WISEPLAY_UUID)
                    .setLicenseUri(configuration.licenseUrl)
                    .build()
                    
                mediaItemBuilder.setDrmConfiguration(drmConfiguration)
            }

            val mediaItem = mediaItemBuilder.build()
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            
        } catch (e: Exception) {
            errorMessage = "Failed to load video: ${e.message}"
        }
    }

    // Cleanup player on dispose
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar with settings button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WisePlay DRM Player",
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onNavigateToConfig) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }

            // Player container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Black)
            ) {
                // ExoPlayer View
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            player = exoPlayer
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            useController = true
                            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Loading overlay
                if (isLoading && errorMessage == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading video...",
                                color = White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Error overlay
                errorMessage?.let { error ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Playback Error",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            errorMessage = null
                                            exoPlayer.prepare()
                                            exoPlayer.playWhenReady = true
                                        }
                                    ) {
                                        Text("Retry")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedButton(
                                        onClick = onNavigateToConfig
                                    ) {
                                        Text("Settings")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Video information footer
            VideoInfoFooter(
                configuration = configuration,
                playerState = playerState
            )
        }
    }
}

@Composable
private fun VideoInfoFooter(
    configuration: DrmConfiguration,
    playerState: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Current Configuration",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Video: ${configuration.videoUrl.takeLast(50)}${if (configuration.videoUrl.length > 50) "..." else ""}",
                style = MaterialTheme.typography.bodySmall
            )
            
            if (configuration.licenseUrl.isNotEmpty()) {
                Text(
                    text = "License: ${configuration.licenseUrl.takeLast(40)}${if (configuration.licenseUrl.length > 40) "..." else ""}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            val stateText = when (playerState) {
                Player.STATE_IDLE -> "Idle"
                Player.STATE_BUFFERING -> "Buffering"
                Player.STATE_READY -> "Ready"
                Player.STATE_ENDED -> "Ended"
                else -> "Unknown"
            }
            
            Text(
                text = "Player State: $stateText",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}