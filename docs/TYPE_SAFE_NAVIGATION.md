# Type-Safe Navigation Documentation

## ✅ **Type-Safe Navigation Implementation**

This project uses **Type-Safe Navigation** with sealed classes instead of string-based routes, providing compile-time safety and better developer experience.

---

## 🎯 **What is Type-Safe Navigation?**

Type-Safe Navigation uses **sealed classes** to represent screens instead of string routes. This ensures:
- ✅ **Compile-time safety** - No typos or invalid routes
- ✅ **Type checking** - IDE autocomplete and validation
- ✅ **Parameter safety** - Required parameters are enforced
- ✅ **Refactoring support** - Rename screens safely
- ✅ **No string routes** - Eliminates route management overhead

---

## 📐 **Architecture Components**

### 1. **Screen Sealed Class** ✅
**Location:** `app/src/main/java/com/lavariyalabs/snapy/android/navigation/Screen.kt`

```kotlin
sealed class Screen {
    object Splash : Screen()
    object OnboardingLanguage : Screen()
    object OnboardingName : Screen()
    object OnboardingGrade : Screen()
    object OnboardingSubject : Screen()
    object Home : Screen()
    object Profile : Screen()
    data class Flashcard(val unitId: Long) : Screen()  // With parameter
}
```

**Key Features:**
- `object` for screens without parameters
- `data class` for screens with parameters
- All screens are type-safe and checked at compile time

### 2. **NavigationState** ✅
**Location:** `app/src/main/java/com/lavariyalabs/snapy/android/navigation/NavigationState.kt`

Manages navigation state using a stack-based approach:

```kotlin
class NavigationState(initialScreen: Screen = Screen.Splash) {
    var currentScreen: Screen by mutableStateOf(initialScreen)
    
    fun navigateTo(screen: Screen)
    fun navigateBack(): Boolean
    fun navigateToAndClearStack(screen: Screen)
    fun canGoBack(): Boolean
}
```

**Methods:**
- `navigateTo()` - Push new screen to stack
- `navigateBack()` - Pop current screen from stack
- `navigateToAndClearStack()` - Clear stack and navigate
- `canGoBack()` - Check if back navigation is possible

### 3. **NavGraph** ✅
**Location:** `app/src/main/java/com/lavariyalabs/snapy/android/navigation/NavGraph.kt`

Uses `when` expression to render screens based on current state:

```kotlin
@Composable
fun NavGraph(
    navigationState: NavigationState,
    appStateViewModel: AppStateViewModel,
    viewModelFactory: ViewModelFactory
) {
    when (val screen = navigationState.currentScreen) {
        is Screen.Splash -> { /* Render SplashScreen */ }
        is Screen.Home -> { /* Render HomeScreen */ }
        is Screen.Flashcard -> { /* Render FlashcardStudyScreen with unitId */ }
        // ... all screens
    }
}
```

---

## 🔄 **How It Works**

### Navigation Flow

```
┌─────────────┐
│   Screen    │  User action triggers navigation
│  (View)     │
└──────┬──────┘
       │
       │ Calls callback
       ▼
┌─────────────┐
│ Navigation  │  Updates currentScreen state
│   State     │
└──────┬──────┘
       │
       │ State change
       ▼
┌─────────────┐
│  NavGraph   │  when expression matches screen
└──────┬──────┘
       │
       │ Renders
       ▼
┌─────────────┐
│   Screen    │  New screen displayed
│  (View)     │
└─────────────┘
```

### Example: Navigating to Flashcard Screen

```kotlin
// 1. User clicks unit card in HomeScreen
HomeScreen(
    onNavigateToFlashcard = { unitId ->
        navigationState.navigateTo(Screen.Flashcard(unitId))
    }
)

// 2. NavigationState updates currentScreen
navigationState.navigateTo(Screen.Flashcard(123L))
// currentScreen = Screen.Flashcard(unitId = 123L)

// 3. NavGraph detects state change and renders
when (navigationState.currentScreen) {
    is Screen.Flashcard -> {
        FlashcardStudyScreen(
            unitId = screen.unitId,  // Type-safe parameter access
            onNavigateBack = { navigationState.navigateBack() }
        )
    }
}
```

---

## 📊 **Comparison: Type-Safe vs String Routes**

### ❌ **Old Way (String Routes)**

```kotlin
// NavRoutes.kt
object NavRoutes {
    const val HOME = "home"
    const val FLASHCARD = "flashcard/{unitId}"
    
    fun flashcardRoute(unitId: Long): String = "flashcard/$unitId"
}

// Usage
navController.navigate(NavRoutes.flashcardRoute(123L))

// Problems:
// ❌ Typos possible: "flascard" instead of "flashcard"
// ❌ No compile-time checking
// ❌ Parameters passed as strings
// ❌ Runtime errors if route doesn't exist
```

### ✅ **New Way (Type-Safe)**

```kotlin
// Screen.kt
sealed class Screen {
    object Home : Screen()
    data class Flashcard(val unitId: Long) : Screen()
}

// Usage
navigationState.navigateTo(Screen.Flashcard(123L))

// Benefits:
// ✅ Compile-time safety
// ✅ IDE autocomplete
// ✅ Type-checked parameters
// ✅ Refactoring support
// ✅ No string management
```

---

## 💡 **Code Examples**

### Example 1: Simple Navigation (No Parameters)

```kotlin
// Screen Definition
sealed class Screen {
    object Home : Screen()
    object Profile : Screen()
}

// Navigation
navigationState.navigateTo(Screen.Profile)

// NavGraph
when (navigationState.currentScreen) {
    is Screen.Home -> HomeScreen(
        onNavigateToProfile = {
            navigationState.navigateTo(Screen.Profile)
        }
    )
    is Screen.Profile -> ProfileScreen(
        onNavigateBack = {
            navigationState.navigateBack()
        }
    )
}
```

### Example 2: Navigation with Parameters

```kotlin
// Screen Definition
sealed class Screen {
    data class Flashcard(val unitId: Long) : Screen()
    data class UserProfile(val userId: String) : Screen()
}

// Navigation
navigationState.navigateTo(Screen.Flashcard(unitId = 123L))
navigationState.navigateTo(Screen.UserProfile(userId = "user123"))

// NavGraph
when (navigationState.currentScreen) {
    is Screen.Flashcard -> {
        FlashcardStudyScreen(
            unitId = screen.unitId,  // Type-safe parameter
            onNavigateBack = { navigationState.navigateBack() }
        )
    }
    is Screen.UserProfile -> {
        UserProfileScreen(
            userId = screen.userId,  // Type-safe parameter
            onNavigateBack = { navigationState.navigateBack() }
        )
    }
}
```

### Example 3: Back Navigation

```kotlin
// Screen with back button
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit
) {
    Text(
        text = "← Back",
        modifier = Modifier.clickable { onNavigateBack() }
    )
}

// NavigationState handles back stack
fun navigateBack(): Boolean {
    if (navigationStack.size > 1) {
        navigationStack.removeLast()
        currentScreen = navigationStack.last()
        return true
    }
    return false
}
```

### Example 4: Clear Stack Navigation

```kotlin
// After onboarding, clear stack and go to home
OnboardingSubjectScreen(
    onNavigateToHome = {
        navigationState.navigateToAndClearStack(Screen.Home)
    }
)

// Implementation
fun navigateToAndClearStack(screen: Screen) {
    navigationStack.clear()
    navigationStack.add(screen)
    currentScreen = screen
}
```

---

## ✅ **Benefits**

### 1. **Compile-Time Safety** ✅
```kotlin
// ❌ This won't compile (typo)
navigationState.navigateTo(Screen.Hom)  // Error: Unresolved reference

// ✅ This compiles
navigationState.navigateTo(Screen.Home)  // OK
```

### 2. **Type-Safe Parameters** ✅
```kotlin
// ❌ Old way - runtime error possible
navController.navigate("flashcard/abc")  // Wrong type, fails at runtime

// ✅ New way - compile-time error
Screen.Flashcard(unitId = "abc")  // Error: Type mismatch, Long expected
Screen.Flashcard(unitId = 123L)   // OK
```

### 3. **IDE Support** ✅
- Autocomplete for all screens
- Parameter hints
- Refactoring support
- Find usages

### 4. **No String Management** ✅
- No route constants file needed
- No string concatenation
- No route validation

### 5. **Exhaustive When Expressions** ✅
```kotlin
when (screen) {
    is Screen.Home -> { /* ... */ }
    is Screen.Profile -> { /* ... */ }
    // Compiler forces you to handle all cases
}
```

---

## 🎯 **Best Practices**

### 1. **Use `object` for Simple Screens**
```kotlin
sealed class Screen {
    object Home : Screen()      // ✅ No parameters
    object Profile : Screen()   // ✅ No parameters
}
```

### 2. **Use `data class` for Screens with Parameters**
```kotlin
sealed class Screen {
    data class Flashcard(val unitId: Long) : Screen()
    data class UserDetail(val userId: String, val tab: Int) : Screen()
}
```

### 3. **Keep Navigation Logic in NavGraph**
```kotlin
// ✅ Good - Navigation logic centralized
NavGraph(
    navigationState = navigationState,
    // ...
) {
    when (screen) {
        is Screen.Home -> HomeScreen(
            onNavigateToProfile = {
                navigationState.navigateTo(Screen.Profile)
            }
        )
    }
}

// ❌ Bad - Navigation logic scattered
// Don't pass navigationState directly to screens
```

### 4. **Use Callbacks for Navigation**
```kotlin
// ✅ Good - Callback-based
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToFlashcard: (Long) -> Unit
)

// ❌ Bad - Direct access
// Don't pass NavigationState to screens
```

### 5. **Handle Back Navigation Properly**
```kotlin
// Check if back navigation is possible
if (navigationState.canGoBack()) {
    navigationState.navigateBack()
}
```

---

## 📁 **File Structure**

```
app/src/main/java/com/lavariyalabs/snapy/android/navigation/
│
├── Screen.kt              # Sealed class for all screens
├── NavigationState.kt     # Navigation state manager
└── NavGraph.kt            # Navigation graph (when expression)
```

---

## 🔧 **Adding a New Screen**

### Step 1: Add Screen to Sealed Class
```kotlin
// Screen.kt
sealed class Screen {
    // ... existing screens
    object Settings : Screen()  // New screen
}
```

### Step 2: Add Case to NavGraph
```kotlin
// NavGraph.kt
when (navigationState.currentScreen) {
    // ... existing cases
    is Screen.Settings -> {
        SettingsScreen(
            onNavigateBack = {
                navigationState.navigateBack()
            }
        )
    }
}
```

### Step 3: Create Screen Composable
```kotlin
// SettingsScreen.kt
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    // Screen implementation
}
```

### Step 4: Navigate to New Screen
```kotlin
// From any screen
navigationState.navigateTo(Screen.Settings)
```

---

## 🚀 **Advanced Features**

### 1. **Deep Linking Support**
```kotlin
// Handle deep links
fun handleDeepLink(uri: Uri) {
    when {
        uri.path == "/flashcard" -> {
            val unitId = uri.getQueryParameter("unitId")?.toLongOrNull()
            unitId?.let {
                navigationState.navigateTo(Screen.Flashcard(it))
            }
        }
    }
}
```

### 2. **Navigation Guards**
```kotlin
fun navigateToProfile() {
    if (isUserLoggedIn()) {
        navigationState.navigateTo(Screen.Profile)
    } else {
        navigationState.navigateTo(Screen.Login)
    }
}
```

### 3. **Navigation History**
```kotlin
class NavigationState {
    private val navigationStack = mutableListOf<Screen>()
    
    fun getNavigationHistory(): List<Screen> {
        return navigationStack.toList()
    }
}
```

---

## 📊 **Comparison Table**

| Feature | String Routes | Type-Safe Navigation |
|---------|--------------|---------------------|
| **Compile-time safety** | ❌ | ✅ |
| **Type checking** | ❌ | ✅ |
| **IDE autocomplete** | ⚠️ Partial | ✅ Full |
| **Refactoring support** | ⚠️ Limited | ✅ Full |
| **Parameter safety** | ❌ | ✅ |
| **Runtime errors** | ⚠️ Possible | ✅ Prevented |
| **Code complexity** | ⚠️ Medium | ✅ Low |
| **Maintainability** | ⚠️ Medium | ✅ High |

---

## ✅ **Conclusion**

Your project uses **Type-Safe Navigation** which provides:

- ✅ **Compile-time safety** - Catch errors before runtime
- ✅ **Better developer experience** - IDE support and autocomplete
- ✅ **Type-safe parameters** - No string-based parameter passing
- ✅ **Clean architecture** - Separation of concerns
- ✅ **Maintainable code** - Easy to add/modify screens
- ✅ **No route management** - Eliminates string route overhead

**The navigation system is production-ready and follows modern Android best practices!** 🎉

---

## 📚 **Additional Resources**

- [Kotlin Sealed Classes](https://kotlinlang.org/docs/sealed-classes.html)
- [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- [State Management in Compose](https://developer.android.com/jetpack/compose/state)
