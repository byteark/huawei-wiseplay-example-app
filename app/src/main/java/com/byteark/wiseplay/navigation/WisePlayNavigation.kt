package com.byteark.wiseplay.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.byteark.wiseplay.ui.screens.ConfigurationScreen
import com.byteark.wiseplay.ui.screens.DeviceCapabilityScreen
import com.byteark.wiseplay.ui.screens.VideoPlayerScreen
import com.byteark.wiseplay.ui.viewmodels.ConfigurationViewModel

sealed class Screen(val route: String) {
    object DeviceCapability : Screen("device_capability")
    object VideoPlayer : Screen("video_player")
    object Configuration : Screen("configuration")
}

@Composable
fun WisePlayNavigation(
    navController: NavHostController,
    configurationViewModel: ConfigurationViewModel = viewModel()
) {
    val configuration by configurationViewModel.configuration.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.DeviceCapability.route
    ) {
        composable(Screen.DeviceCapability.route) {
            DeviceCapabilityScreen(
                onNavigateToPlayer = {
                    navController.navigate(Screen.VideoPlayer.route)
                }
            )
        }

        composable(Screen.VideoPlayer.route) {
            VideoPlayerScreen(
                configuration = configuration,
                onNavigateToConfig = {
                    navController.navigate(Screen.Configuration.route)
                }
            )
        }

        composable(Screen.Configuration.route) {
            ConfigurationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = configurationViewModel
            )
        }
    }
}