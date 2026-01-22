# Snapy Android

<div align="center">

**An interactive educational flashcard application for Android**

Built by [Lavariya Labs](https://github.com/Lavariya-Labz)

[Features](#features) • [Tech Stack](#tech-stack) • [Getting Started](#getting-started) • [Documentation](#documentation) • [Architecture](#architecture)

</div>

---

## About

**Snapy** is a modern Android application designed to revolutionize the way students study through interactive flashcard decks. Built with cutting-edge Android technologies and following best practices, Snapy provides an optimized learning experience using spaced repetition algorithms and a clean, intuitive interface.

### Key Highlights

- 📚 **Hierarchical Learning Structure**: Grade → Subject → Term → Unit → Flashcards
- 🧠 **Spaced Repetition Algorithm**: SM2 algorithm for optimized retention
- 🎯 **Multiple Question Types**: Self-evaluation and multiple-choice flashcards
- 🎨 **Modern UI**: Built with Jetpack Compose and Material Design 3
- 🏗️ **Clean Architecture**: MVVM pattern with proper separation of concerns
- ☁️ **Cloud Backend**: Supabase integration for real-time data sync
- 🔒 **Type-Safe Navigation**: Compile-time safe navigation system

---

## Features

### 📖 Study Features

- **Interactive Flashcards**
  - Self-evaluation mode (flip to reveal answer)
  - Multiple-choice questions with instant feedback
  - Progress tracking with percentage completion
  - Correct/incorrect answer counting

- **Smart Learning**
  - Spaced repetition using SM2 algorithm
  - Difficulty-based card scheduling (Easy, Medium, Hard)
  - Review history tracking
  - User progress persistence

- **Content Organization**
  - Filter by Grade, Subject, and Term
  - Browse units in a responsive grid layout
  - Hierarchical content structure
  - Quick access to study materials

### 🎨 User Experience

- **Smooth Onboarding**
  - Language selection
  - Personalized name entry
  - Grade and subject selection
  - Seamless flow to home screen

- **Enhanced Interactions**
  - Button click sound effects
  - Smooth animations with Lottie
  - Responsive touch feedback
  - Loading states with visual indicators

- **User Profile**
  - View and manage personal information
  - Track study progress
  - Customize preferences

---

## Screenshots

> _Screenshots coming soon_

<!-- Add screenshots here:
- Splash screen
- Onboarding flow
- Home screen
- Flashcard study interface
- Profile screen
-->

---

## Tech Stack

### Core Technologies

- **Language**: Kotlin 2.2.21
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36 (Android 15)
- **Build System**: Gradle 8.13.1 with Kotlin DSL

### UI Framework

- **Jetpack Compose** (BOM 2025.12.00)
  - Compose Material3 - Material Design 3 components
  - Compose UI - Core UI toolkit
  - Compose Material Icons Extended - Comprehensive icon library
- **Lottie Compose 6.7.1** - Animation library

### Architecture & Libraries

- **MVVM Architecture** - Model-View-ViewModel pattern
- **AndroidX Lifecycle** - ViewModel and lifecycle-aware components
- **Kotlin Coroutines** - Asynchronous programming
- **Kotlinx Serialization 1.9.0** - JSON serialization

### Backend

- **Supabase 3.2.6** - Backend as a Service
  - PostgreSQL database
  - PostgREST API for data queries
  - Real-time data sync capabilities
- **Ktor Client 3.3.3** - HTTP client for API communication

### Testing

- **JUnit 4** - Unit testing framework
- **Espresso** - UI testing
- **Compose UI Test** - Composable testing

---

## Getting Started

### Prerequisites

- Android Studio Ladybug or newer
- JDK 17 or higher
- Android SDK with API level 28+
- Gradle 8.13.1

### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/Lavariya-Labz/Snapy_Android.git
   cd Snapy_Android
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically prompt to sync
   - Wait for dependencies to download

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button (▶️) or press `Shift + F10`

For detailed setup instructions, see [Setup Guide](docs/SETUP_GUIDE.md).

---

## Project Structure

```
Snapy_Android/
├── app/
│   ├── src/main/
│   │   ├── java/com/lavariyalabs/snapy/android/
│   │   │   ├── config/           # Configuration (Supabase)
│   │   │   ├── data/             # Data layer (Repository, Models, DataSource)
│   │   │   ├── navigation/       # Type-safe navigation system
│   │   │   ├── ui/               # UI layer (Screens, ViewModels, Components, Theme)
│   │   │   ├── utils/            # Utilities (SoundManager)
│   │   │   └── MainActivity.kt   # App entry point
│   │   └── res/                  # Resources (layouts, drawables, strings)
│   └── build.gradle.kts          # App-level build configuration
├── docs/                         # Documentation
├── gradle/                       # Gradle wrapper and version catalog
├── build.gradle.kts              # Project-level build configuration
└── README.md                     # This file
```

---

## Architecture

Snapy follows **MVVM (Model-View-ViewModel)** architecture pattern with clean separation of concerns:

### Architecture Layers

```
┌─────────────────┐
│  View (Screen)  │  Jetpack Compose UI
└────────┬────────┘
         │ Observes State
         ▼
┌─────────────────┐
│   ViewModel     │  Business Logic & State Management
└────────┬────────┘
         │ Calls
         ▼
┌─────────────────┐
│   Repository    │  Data Abstraction Layer
└────────┬────────┘
         │ Fetches
         ▼
┌─────────────────┐
│   DataSource    │  Supabase API Integration
└─────────────────┘
```

### Key Architectural Decisions

- **Type-Safe Navigation**: Uses sealed classes instead of string routes
- **State-Based UI**: Reactive UI with Compose state management
- **Repository Pattern**: Single source of truth for data access
- **Dependency Injection**: ViewModelFactory for clean DI
- **Separation of Concerns**: Clear boundaries between layers

### Documentation

- [MVVM Architecture](docs/MVVM_ARCHITECTURE_SUMMARY.md) - Detailed MVVM implementation guide
- [Type-Safe Navigation](docs/TYPE_SAFE_NAVIGATION.md) - Navigation system documentation
- [Data Models](docs/DATA_MODELS.md) - Data structure and model documentation
- [Spaced Repetition](docs/SPACED_REPETITION.md) - SM2 algorithm implementation
- [Supabase Integration](docs/SUPABASE_INTEGRATION.md) - Backend API documentation
- [Development Guide](docs/DEVELOPMENT_GUIDE.md) - Development workflow and best practices

---

## Documentation

Comprehensive documentation is available in the `docs/` folder:

| Document | Description |
|----------|-------------|
| [Setup Guide](docs/SETUP_GUIDE.md) | Installation, configuration, and environment setup |
| [MVVM Architecture](docs/MVVM_ARCHITECTURE_SUMMARY.md) | Architecture patterns and implementation details |
| [Type-Safe Navigation](docs/TYPE_SAFE_NAVIGATION.md) | Navigation system and routing |
| [Data Models](docs/DATA_MODELS.md) | Database schema and data structures |
| [Spaced Repetition](docs/SPACED_REPETITION.md) | SM2 algorithm and learning optimization |
| [Supabase Integration](docs/SUPABASE_INTEGRATION.md) | Backend API and database integration |
| [Development Guide](docs/DEVELOPMENT_GUIDE.md) | Development workflow and contribution guidelines |

---

## Development

### Building the Project

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Code Style

This project follows [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) and [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).

### Contributing

We welcome contributions! Please see our [Development Guide](docs/DEVELOPMENT_GUIDE.md) for details on:

- Setting up the development environment
- Code style guidelines
- Submitting pull requests
- Reporting issues

---

## Testing

The project includes:

- **Unit Tests**: Business logic and ViewModel tests
- **Integration Tests**: Repository and data layer tests
- **UI Tests**: Compose screen and component tests

Run all tests:
```bash
./gradlew test connectedAndroidTest
```

---

## Team

Built with ❤️ by [Lavariya Labs](https://github.com/Lavariya-Labz)

---

## License

This project is proprietary software developed by Lavariya Labs.

---

## Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI toolkit
- [Supabase](https://supabase.com/) - Open source Firebase alternative
- [Lottie](https://airbnb.design/lottie/) - Animation library by Airbnb
- [Material Design 3](https://m3.material.io/) - Google's design system

---

## Support

For issues, questions, or suggestions:

- 🐛 [Report a Bug](https://github.com/Lavariya-Labz/Snapy_Android/issues)
- 💡 [Request a Feature](https://github.com/Lavariya-Labz/Snapy_Android/issues)
- 📧 Contact: [Lavariya Labs](https://github.com/Lavariya-Labz)

---

<div align="center">

**Made with Kotlin and Jetpack Compose**

⭐️ Star this repository if you find it helpful!

</div>
