# WisePlay DRM ExoPlayer/Media3 Example App

An example application demonstrating how to integrate Huawei WisePlay DRM with the latest ExoPlayer/Media3 library (v1.8.0).

## Features

### Device Capability Detection
- **WisePlay DRM Support Check**: Automatically detects if the device supports WisePlay DRM
- **Device Attributes Display**: Shows detailed DRM capabilities including:
  - Vendor information
  - Version details
  - Security levels
  - HDCP support
  - Available algorithms

### Video Player
- **Latest Media3 1.8.0**: Uses the Media3 library with latest optimizations
- **Custom DRM Session Manager**: Tailored for WisePlay DRM requirements

### Configuration
- **License Server Setup**: Configurable license server URLs for WisePlay DRM
- **Authentication Support**: Bearer token and custom authentication headers
- **Custom Headers**: Add any additional headers required for your setup
- **Persistent Settings**: Configuration saved using Android DataStore
- **Sample Content Library**: Pre-configured test URLs for DASH, HLS, and clear content
- **Quick Setup Presets**: Pre-configured settings for common scenarios

## Architecture

### Project Structure
```
app/src/main/java/com/byteark/wiseplay/
├── data/
│   └── ConfigurationRepository.kt          # Data persistence and management
├── drm/
│   ├── WisePlayConstants.kt                # WisePlay DRM constants and UUIDs
│   ├── DrmCapabilityChecker.kt             # Device capability detection
│   ├── WisePlayMediaDrmCallback.kt         # Custom DRM callback implementation
│   └── WisePlayDrmSessionManagerProvider.kt # DRM session management
├── navigation/
│   └── WisePlayNavigation.kt               # Compose navigation setup
├── ui/
│   ├── screens/
│   │   ├── DeviceCapabilityScreen.kt       # Screen 1: Device capability check
│   │   ├── VideoPlayerScreen.kt            # Screen 2: Video player
│   │   └── ConfigurationScreen.kt          # Screen 3: Configuration
│   └── viewmodels/
│       └── ConfigurationViewModel.kt       # Configuration state management
└── MainActivity.kt                         # Main application entry point
```

### Key Components

#### WisePlay DRM Integration
- **Custom MediaDrmCallback**: Handles license requests and authentication
- **DRM Session Manager**: Manages WisePlay DRM sessions with proper UUID
- **Capability Checker**: Detects device support and attributes
- **Error Handling**: Comprehensive error handling for DRM scenarios

#### Modern Android Architecture
- **Jetpack Compose UI**: Modern declarative UI framework
- **Navigation Component**: Type-safe navigation between screens
- **DataStore**: Modern preference storage solution
- **MVVM Pattern**: Clean separation of concerns with ViewModels

---

## Setup Instructions

### Prerequisites
- **Android SDK**: API 24+ (Android 7.0+) 
- **Target SDK**: API 36 (Android 15)
- **Kotlin**: 2.0.21 or compatible
- **Device**: Huawei device with Mobile Services (HMS) for WisePlay DRM testing
- **Network**: Internet connection for streaming content

### Building the App
1. **Clone the repository**
   ```bash
   git clone https://github.com/your-repo/huawei-wiseplay-example-app.git
   cd huawei-wiseplay-example-app
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle dependencies**
   ```bash
   ./gradlew --refresh-dependencies
   ```

4. **Build and run**
   ```bash
   # Debug build
   ./gradlew assembleDebug
   
   # Release build  
   ./gradlew assembleRelease
   
   # Install on connected device
   ./gradlew installDebug
   ```

### Configuration

#### Required Permissions
The app automatically includes these permissions in `AndroidManifest.xml`:
- `INTERNET`: For streaming video content
- `ACCESS_NETWORK_STATE`: For network status monitoring
- `MODIFY_AUDIO_SETTINGS`: For DRM-related audio configuration

## Technical Details

### WisePlay DRM UUID
```kotlin
val WISEPLAY_UUID: UUID = UUID.fromString("3d5e6d35-9b9a-41e8-b843-dd3c6e72c42c")
```

### Media3 Dependencies (v1.8.0)
```gradle
// Core ExoPlayer
implementation("androidx.media3:media3-exoplayer:1.8.0")
implementation("androidx.media3:media3-ui:1.8.0") 
implementation("androidx.media3:media3-common:1.8.0")
implementation("androidx.media3:media3-session:1.8.0")

// DASH and HLS Support
implementation("androidx.media3:media3-exoplayer-dash:1.8.0")
implementation("androidx.media3:media3-exoplayer-hls:1.8.0") 
implementation("androidx.media3:media3-datasource:1.8.0")
```
