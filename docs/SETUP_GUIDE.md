# Setup Guide

This guide will walk you through setting up the Snapy Android development environment from scratch.

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation Steps](#installation-steps)
- [Configuration](#configuration)
- [Running the App](#running-the-app)
- [Troubleshooting](#troubleshooting)
- [IDE Setup](#ide-setup)
- [Build Variants](#build-variants)

---

## Prerequisites

### Required Software

1. **Android Studio**
   - Version: Ladybug (2024.2.1) or newer
   - Download: [https://developer.android.com/studio](https://developer.android.com/studio)

2. **Java Development Kit (JDK)**
   - Version: JDK 17 or higher
   - Recommendation: Use the JDK bundled with Android Studio

3. **Android SDK**
   - Min SDK: API 28 (Android 9.0 Pie)
   - Target SDK: API 36 (Android 15)
   - Build Tools: Latest version

4. **Git**
   - Version: 2.x or higher
   - Download: [https://git-scm.com/](https://git-scm.com/)

### System Requirements

- **Operating System**: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **RAM**: 8 GB minimum (16 GB recommended)
- **Disk Space**: 10 GB free space
- **Display**: 1280x800 minimum resolution

---

## Installation Steps

### 1. Install Android Studio

1. Download Android Studio from the [official website](https://developer.android.com/studio)
2. Run the installer and follow the setup wizard
3. During installation, ensure the following components are selected:
   - Android SDK
   - Android SDK Platform
   - Android Virtual Device (AVD)

### 2. Configure Android SDK

1. Open Android Studio
2. Go to **Settings/Preferences** > **Appearance & Behavior** > **System Settings** > **Android SDK**
3. In the **SDK Platforms** tab, ensure the following are installed:
   - Android 15.0 (API 36) - Target SDK
   - Android 9.0 (API 28) - Min SDK
   - Android SDK Build-Tools (latest version)

4. In the **SDK Tools** tab, ensure the following are installed:
   - Android SDK Build-Tools
   - Android SDK Platform-Tools
   - Android Emulator
   - Android SDK Tools
   - Intel x86 Emulator Accelerator (HAXM) - for Intel processors
   - Google Play Services

### 3. Clone the Repository

Open a terminal/command prompt and run:

```bash
# Clone via HTTPS
git clone https://github.com/Lavariya-Labz/Snapy_Android.git

# Or clone via SSH
git clone git@github.com:Lavariya-Labz/Snapy_Android.git

# Navigate to the project directory
cd Snapy_Android
```

### 4. Open Project in Android Studio

1. Launch Android Studio
2. Select **File** > **Open**
3. Navigate to the `Snapy_Android` folder
4. Click **OK**

Android Studio will:
- Index the project
- Download Gradle wrapper (if needed)
- Download dependencies
- Build the project

**Note**: First-time setup may take 5-10 minutes depending on your internet connection.

### 5. Gradle Sync

Android Studio should automatically trigger a Gradle sync. If not:

1. Click **File** > **Sync Project with Gradle Files**
2. Wait for the sync to complete
3. Check the **Build** window for any errors

---

## Configuration

### Gradle Configuration

The project uses Gradle 8.13.1 with Kotlin DSL. Configuration files:

- **Project-level**: `build.gradle.kts`
- **App-level**: `app/build.gradle.kts`
- **Dependencies**: `gradle/libs.versions.toml`

### Supabase Configuration

The app connects to Supabase backend. Configuration is located at:

```
app/src/main/java/com/lavariyalabs/snapy/android/config/SupabaseConfig.kt
```

**Current Configuration:**
- **URL**: `https://lriggiqgikqhsfqlojlj.supabase.co`
- **Anonymous Key**: Embedded in the config file
- **Timeout**: 60 seconds

**Note**: For production, consider using environment variables or BuildConfig for sensitive keys.

### local.properties (Optional)

Create a `local.properties` file in the project root if you need to specify custom SDK path:

```properties
sdk.dir=/path/to/your/Android/sdk
```

**Note**: This file is auto-generated and should not be committed to version control.

---

## Running the App

### Option 1: Physical Device

1. **Enable Developer Options on your Android device:**
   - Go to **Settings** > **About Phone**
   - Tap **Build Number** 7 times
   - Go back to **Settings** > **Developer Options**
   - Enable **USB Debugging**

2. **Connect device via USB**
   - Connect your device to your computer
   - Accept the USB debugging prompt on your device

3. **Run the app:**
   - Click the **Run** button (▶️) in Android Studio
   - Select your device from the device dropdown
   - Click **OK**

### Option 2: Android Emulator

1. **Create a Virtual Device:**
   - Click **Tools** > **Device Manager**
   - Click **Create Device**
   - Select a device definition (e.g., Pixel 8)
   - Select a system image:
     - Recommended: Android 15 (API 36)
     - Minimum: Android 9 (API 28)
   - Click **Finish**

2. **Run the app:**
   - Click the **Run** button (▶️)
   - Select the emulator from the dropdown
   - Wait for the emulator to boot
   - App will automatically install and launch

### Keyboard Shortcuts

- **Run**: `Shift + F10` (Windows/Linux) or `Ctrl + R` (macOS)
- **Debug**: `Shift + F9` (Windows/Linux) or `Ctrl + D` (macOS)
- **Stop**: `Ctrl + F2` (Windows/Linux) or `Cmd + F2` (macOS)

---

## Troubleshooting

### Common Issues

#### 1. Gradle Sync Failed

**Problem**: Gradle sync fails with "Could not resolve dependencies"

**Solution**:
```bash
# Clear Gradle cache
./gradlew clean

# Or from Android Studio
Build > Clean Project
Build > Rebuild Project
```

#### 2. SDK Not Found

**Problem**: "SDK location not found"

**Solution**:
- Create `local.properties` file with your SDK path:
  ```properties
  sdk.dir=/path/to/Android/sdk
  ```

#### 3. Out of Memory Error

**Problem**: Gradle build fails with OutOfMemoryError

**Solution**:
- Edit `gradle.properties` and increase heap size:
  ```properties
  org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=1024m
  ```

#### 4. Emulator Won't Start

**Problem**: AVD fails to start or is very slow

**Solution**:
- Enable hardware acceleration (HAXM for Intel, WHPX for Windows)
- Allocate more RAM to the emulator (2-4 GB recommended)
- Use x86 system images instead of ARM

#### 5. USB Debugging Not Working

**Problem**: Device not recognized

**Solution**:
- Install device-specific USB drivers (Windows only)
- Revoke and re-authorize USB debugging on device
- Try a different USB cable or port
- Check if `adb devices` lists your device

#### 6. Build Failed: "Manifest merger failed"

**Problem**: Multiple manifest files conflict

**Solution**:
```bash
# Check merged manifest
Build > Analyze APK > Select merged manifest

# Or add to app/build.gradle.kts
android {
    packagingOptions {
        resources {
            excludes += "META-INF/*"
        }
    }
}
```

#### 7. Compose Preview Not Working

**Problem**: Compose previews not rendering

**Solution**:
- Click **Build** > **Rebuild Project**
- Invalidate caches: **File** > **Invalidate Caches / Restart**
- Ensure Compose version matches in `libs.versions.toml`

### Getting Help

If you encounter issues not listed here:

1. Check the [GitHub Issues](https://github.com/Lavariya-Labz/Snapy_Android/issues)
2. Search [Stack Overflow](https://stackoverflow.com/) for similar problems
3. Consult [Android Documentation](https://developer.android.com/docs)

---

## IDE Setup

### Recommended Plugins

Install these plugins for better development experience:

1. **Kotlin** (Pre-installed)
2. **Android** (Pre-installed)
3. **Jetpack Compose** (Pre-installed in recent versions)
4. **Rainbow Brackets** - Visual bracket matching
5. **Key Promoter X** - Learn keyboard shortcuts
6. **.ignore** - Better gitignore support

**To install plugins:**
- Go to **Settings/Preferences** > **Plugins**
- Search for the plugin name
- Click **Install**

### Code Style

Configure code style for the project:

1. Go to **Settings/Preferences** > **Editor** > **Code Style** > **Kotlin**
2. Click **Set from...** > **Kotlin style guide**
3. Enable **Auto-format on save** (optional):
   - **Settings** > **Tools** > **Actions on Save**
   - Check **Reformat code**

### Live Templates

Useful live templates for faster development:

- `comp` - Create a Composable function
- `prev` - Create a Compose Preview
- `vm` - Create a ViewModel
- `lce` - LaunchedEffect block
- `remember` - remember block

---

## Build Variants

The project supports two build variants:

### Debug Build

Default variant for development:

```bash
./gradlew assembleDebug
```

**Features:**
- Debugging enabled
- Source code included
- Faster build time
- Larger APK size

**APK Location**: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

Optimized for production:

```bash
./gradlew assembleRelease
```

**Features:**
- Code obfuscation (ProGuard/R8)
- Optimized resources
- Smaller APK size
- Requires signing configuration

**Note**: Release builds require a keystore for signing. Configure in `app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/keystore.jks")
            storePassword = "password"
            keyAlias = "alias"
            keyPassword = "password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

**APK Location**: `app/build/outputs/apk/release/app-release.apk`

---

## Verification

After setup, verify everything works:

1. **Build the project:**
   ```bash
   ./gradlew build
   ```

2. **Run tests:**
   ```bash
   ./gradlew test
   ```

3. **Run the app:**
   - Launch on emulator or device
   - Verify onboarding flow works
   - Check network connectivity to Supabase
   - Test flashcard study functionality

---

## Next Steps

Now that your environment is set up:

1. Read the [MVVM Architecture](MVVM_ARCHITECTURE_SUMMARY.md) documentation
2. Explore the [Type-Safe Navigation](TYPE_SAFE_NAVIGATION.md) system
3. Check out the [Development Guide](DEVELOPMENT_GUIDE.md)
4. Review [Data Models](DATA_MODELS.md) documentation
5. Start coding!

---

## Additional Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)
- [Supabase Documentation](https://supabase.com/docs)

---

**Happy Coding!** 🚀
