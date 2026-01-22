# Development Guide

This guide covers development workflow, coding standards, and best practices for contributing to the Snapy Android project.

---

## Table of Contents

- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Architecture Guidelines](#architecture-guidelines)
- [Git Workflow](#git-workflow)
- [Testing](#testing)
- [Code Review](#code-review)
- [Contributing](#contributing)
- [Common Tasks](#common-tasks)

---

## Getting Started

### Prerequisites

Before you begin development, ensure you have:

1. Completed the [Setup Guide](SETUP_GUIDE.md)
2. Read the [MVVM Architecture](MVVM_ARCHITECTURE_SUMMARY.md) documentation
3. Familiarized yourself with [Type-Safe Navigation](TYPE_SAFE_NAVIGATION.md)
4. Understood the [Data Models](DATA_MODELS.md)

### Your First Contribution

1. **Fork the repository** (for external contributors)
2. **Clone your fork** or the main repository
3. **Create a feature branch** from `main`
4. **Make your changes** following the guidelines below
5. **Test thoroughly**
6. **Submit a pull request**

---

## Development Workflow

### Branch Strategy

We follow a simplified Git Flow:

```
main (production-ready code)
  ├─ feature/feature-name
  ├─ bugfix/bug-description
  ├─ hotfix/urgent-fix
  └─ refactor/refactor-description
```

### Branch Naming

- `feature/` - New features (e.g., `feature/dark-mode`)
- `bugfix/` - Bug fixes (e.g., `bugfix/flashcard-crash`)
- `hotfix/` - Urgent fixes for production
- `refactor/` - Code refactoring
- `docs/` - Documentation updates

### Development Cycle

```
1. Create branch
2. Write code
3. Write tests
4. Run tests locally
5. Commit changes
6. Push to remote
7. Create Pull Request
8. Address review comments
9. Merge to main
```

---

## Coding Standards

### Kotlin Style Guide

Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) and [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).

### Key Principles

1. **Readability over cleverness**
2. **Consistency** throughout the codebase
3. **Self-documenting code** with descriptive names
4. **Comments for "why", not "what"**
5. **SOLID principles**

### Naming Conventions

#### Classes & Interfaces

```kotlin
// ✅ Good - PascalCase
class FlashcardViewModel
interface DataSource
data class UserProgress

// ❌ Bad
class flashcardViewModel
class flashcard_view_model
```

#### Functions & Variables

```kotlin
// ✅ Good - camelCase
fun loadFlashcards()
val isLoading: Boolean
private val _flashcards = mutableStateOf<List<Flashcard>>(emptyList())

// ❌ Bad
fun LoadFlashcards()
val IsLoading: Boolean
private val flashcards_list
```

#### Constants

```kotlin
// ✅ Good - UPPER_SNAKE_CASE
const val MAX_FLASHCARDS_PER_SESSION = 20
const val DEFAULT_EASE_FACTOR = 2.5f

// ❌ Bad
const val maxFlashcardsPerSession = 20
const val defaultEaseFactor = 2.5f
```

#### Resources

```kotlin
// ✅ Good - snake_case
// res/values/strings.xml
<string name="flashcard_title">Flashcard</string>
<string name="answer_correct">Correct!</string>

// res/drawable/ic_flashcard.xml
// res/layout/fragment_flashcard.xml (if used)
```

### File Organization

#### Package Structure

```
com.lavariyalabs.snapy.android/
├── config/           # Configuration (Supabase)
├── data/             # Data layer
│   ├── model/        # Data models
│   └── remote/       # Remote data sources
├── navigation/       # Navigation system
├── ui/               # UI layer
│   ├── components/   # Reusable UI components
│   ├── screen/       # Screens
│   ├── theme/        # Theme & styling
│   └── viewmodel/    # ViewModels
└── utils/            # Utilities
```

#### File Structure

Organize code within files:

```kotlin
// 1. Package declaration
package com.lavariyalabs.snapy.android.ui.viewmodel

// 2. Imports (sorted)
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

// 3. Class documentation
/**
 * FlashcardViewModel manages the state for the flashcard study screen.
 */
class FlashcardViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    // 4. Companion object (if needed)
    companion object {
        const val MAX_FLASHCARDS = 20
    }

    // 5. Public properties
    val flashcards: StateFlow<List<Flashcard>> = _flashcards

    // 6. Private properties
    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())

    // 7. Init block (if needed)
    init {
        loadFlashcards()
    }

    // 8. Public functions
    fun loadFlashcards() { }

    // 9. Private functions
    private fun updateState() { }
}
```

---

## Architecture Guidelines

### MVVM Compliance

Always follow MVVM architecture:

```
View (Screen) → ViewModel → Repository → DataSource
       ↑            │
       └────────────┘
         State
```

### View Layer (Screens & Components)

**DO:**
- Keep screens pure and stateless
- Only display UI based on ViewModel state
- Pass callbacks for user actions
- Use Composable functions

**DON'T:**
- Put business logic in screens
- Make API calls directly
- Store state in screens
- Access data sources

```kotlin
// ✅ Good
@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel,
    onNavigateBack: () -> Unit
) {
    val flashcards by viewModel.flashcards.collectAsState()

    if (flashcards.isEmpty()) {
        Text("No flashcards")
    } else {
        FlashcardList(flashcards, onCardClick = viewModel::onCardClick)
    }
}

// ❌ Bad - Business logic in screen
@Composable
fun FlashcardScreen() {
    val flashcards = remember { mutableStateOf<List<Flashcard>>(emptyList()) }

    LaunchedEffect(Unit) {
        flashcards.value = SupabaseDataSource().getFlashcards()  // ❌
    }
}
```

### ViewModel Layer

**DO:**
- Hold and manage UI state
- Handle business logic
- Use coroutines for async operations
- Expose immutable state

**DON'T:**
- Hold Context references
- Make UI decisions
- Access Android framework classes directly

```kotlin
// ✅ Good
class FlashcardViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    fun loadFlashcards(unitId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _flashcards.value = repository.getFlashcardsByUnit(unitId)
            _isLoading.value = false
        }
    }
}

// ❌ Bad - Mutable state exposed, no repository
class FlashcardViewModel : ViewModel() {
    val flashcards = mutableStateOf<List<Flashcard>>(emptyList())

    fun loadFlashcards(unitId: Long) {
        flashcards.value = SupabaseDataSource().getFlashcards(unitId)  // ❌
    }
}
```

### Repository Layer

**DO:**
- Abstract data sources
- Provide single source of truth
- Handle data caching (if needed)
- Transform data for ViewModels

**DON'T:**
- Know about UI
- Make UI decisions

```kotlin
// ✅ Good
class FlashcardRepository(
    private val dataSource: SupabaseDataSource
) {
    suspend fun getFlashcardsByUnit(unitId: Long): List<Flashcard> {
        return dataSource.getFlashcardsByUnit(unitId)
    }
}
```

### Data Source Layer

**DO:**
- Handle API calls
- Handle errors
- Parse responses

**DON'T:**
- Know about ViewModels
- Handle business logic

---

## Git Workflow

### Commit Messages

Follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding/updating tests
- `chore`: Build process or auxiliary tool changes

**Examples:**

```bash
# Good commits
git commit -m "feat(flashcards): add MCQ support"
git commit -m "fix(navigation): resolve back button crash"
git commit -m "docs(readme): update setup instructions"
git commit -m "refactor(viewmodel): extract common state logic"

# Detailed commit
git commit -m "feat(spaced-repetition): implement SM2 algorithm

- Add UserProgress model
- Create SM2 calculation functions
- Update FlashcardViewModel to use SM2
- Add unit tests for SM2 calculations

Closes #42"
```

### Pull Request Process

1. **Create PR** with descriptive title and description
2. **Link related issues** (e.g., "Closes #42")
3. **Request reviewers**
4. **Address feedback** promptly
5. **Squash commits** if requested
6. **Merge** once approved

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Manual testing completed
- [ ] No new warnings

## Screenshots (if applicable)
Add screenshots for UI changes

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes
```

---

## Testing

### Unit Tests

Write unit tests for:
- ViewModels
- Repository logic
- Utility functions
- SM2 algorithm

```kotlin
@Test
fun `loadFlashcards updates state correctly`() = runBlocking {
    // Given
    val mockRepository = MockFlashcardRepository()
    val viewModel = FlashcardViewModel(mockRepository)

    // When
    viewModel.loadFlashcards(unitId = 1L)

    // Then
    assertEquals(3, viewModel.flashcards.value.size)
    assertFalse(viewModel.isLoading.value)
}
```

### Integration Tests

Test interactions between components:

```kotlin
@Test
fun `repository fetches flashcards from data source`() = runBlocking {
    val dataSource = MockSupabaseDataSource()
    val repository = FlashcardRepository(dataSource)

    val flashcards = repository.getFlashcardsByUnit(1L)

    assertTrue(flashcards.isNotEmpty())
}
```

### UI Tests

Test Composable screens:

```kotlin
@Test
fun testFlashcardScreen() {
    composeTestRule.setContent {
        FlashcardScreen(
            viewModel = mockViewModel,
            onNavigateBack = {}
        )
    }

    composeTestRule
        .onNodeWithText("Question text")
        .assertIsDisplayed()
}
```

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests FlashcardViewModelTest

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

---

## Code Review

### As a Reviewer

**Check for:**
- [ ] Code follows architecture guidelines
- [ ] Business logic in ViewModels, not Views
- [ ] Proper error handling
- [ ] No hardcoded strings (use resources)
- [ ] Tests included
- [ ] Documentation updated
- [ ] No breaking changes
- [ ] Performance considerations

**Provide:**
- Constructive feedback
- Specific suggestions
- Code examples when helpful
- Praise for good work

### As an Author

**Before requesting review:**
- [ ] Self-review your code
- [ ] Run all tests
- [ ] Update documentation
- [ ] Add screenshots for UI changes
- [ ] Ensure builds successfully

**During review:**
- Respond to comments promptly
- Ask for clarification if needed
- Don't take feedback personally
- Thank reviewers

---

## Contributing

### Bug Reports

When reporting bugs, include:

1. **Description**: What went wrong?
2. **Steps to Reproduce**: How to recreate the bug
3. **Expected Behavior**: What should happen
4. **Actual Behavior**: What actually happens
5. **Screenshots**: If applicable
6. **Device/Emulator Info**: Android version, device model
7. **App Version**: Version or commit hash

### Feature Requests

When requesting features, include:

1. **Problem**: What problem does this solve?
2. **Proposed Solution**: How should it work?
3. **Alternatives**: Other approaches considered
4. **Additional Context**: Screenshots, mockups

### Code Contributions

1. **Discuss first**: For large changes, open an issue first
2. **Follow guidelines**: Adhere to this guide
3. **Write tests**: Include unit tests
4. **Update docs**: Keep documentation current
5. **Be responsive**: Address review feedback

---

## Common Tasks

### Adding a New Screen

1. **Create Screen Composable**:
   ```kotlin
   // ui/screen/NewScreen.kt
   @Composable
   fun NewScreen(
       viewModel: NewViewModel,
       onNavigateBack: () -> Unit
   ) {
       // UI implementation
   }
   ```

2. **Create ViewModel**:
   ```kotlin
   // ui/viewmodel/NewViewModel.kt
   class NewViewModel(
       private val repository: Repository
   ) : ViewModel() {
       // State and logic
   }
   ```

3. **Add to Navigation**:
   ```kotlin
   // navigation/Screen.kt
   sealed class Screen {
       object New : Screen()
   }

   // navigation/NavGraph.kt
   when (screen) {
       is Screen.New -> NewScreen(...)
   }
   ```

4. **Update ViewModelFactory**:
   ```kotlin
   // ui/viewmodel/ViewModelFactory.kt
   override fun <T : ViewModel> create(modelClass: Class<T>): T {
       return when {
           modelClass.isAssignableFrom(NewViewModel::class.java) -> {
               NewViewModel(repository) as T
           }
           // ...
       }
   }
   ```

### Adding a New Data Model

1. **Create Model**:
   ```kotlin
   // data/model/NewModel.kt
   @Serializable
   data class NewModel(
       val id: Long,
       val name: String,
       // ... fields
   )
   ```

2. **Add to DataSource**:
   ```kotlin
   // data/remote/SupabaseDataSource.kt
   suspend fun getNewModels(): List<NewModel> {
       return SupabaseConfig.supabaseClient
           .from("new_models")
           .select()
           .decodeList<NewModel>()
   }
   ```

3. **Add to Repository**:
   ```kotlin
   // data/FlashcardRepository.kt
   suspend fun getNewModels(): List<NewModel> {
       return dataSource.getNewModels()
   }
   ```

4. **Update Database Schema** (Supabase dashboard):
   ```sql
   CREATE TABLE new_models (
       id BIGSERIAL PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       created_at TIMESTAMP DEFAULT NOW()
   );
   ```

### Adding a New Feature Flag

1. **Define constant**:
   ```kotlin
   // utils/FeatureFlags.kt
   object FeatureFlags {
       const val ENABLE_DARK_MODE = true
       const val ENABLE_SOUND_EFFECTS = true
   }
   ```

2. **Use in code**:
   ```kotlin
   if (FeatureFlags.ENABLE_DARK_MODE) {
       // Show dark mode toggle
   }
   ```

### Running Specific Build Variants

```bash
# Debug build (default)
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install debug APK
./gradlew installDebug

# Clean build
./gradlew clean assembleDebug
```

---

## Performance Best Practices

### Compose Performance

1. **Use remember** for expensive operations:
   ```kotlin
   val expensiveValue = remember(key) { expensiveCalculation() }
   ```

2. **Use derivedStateOf** for computed state:
   ```kotlin
   val filteredList = remember(flashcards, filter) {
       derivedStateOf { flashcards.filter { it.type == filter } }
   }
   ```

3. **Avoid recomposition**:
   - Use stable types
   - Pass lambdas as stable references
   - Use keys in lists

### Coroutines Best Practices

1. **Use viewModelScope**:
   ```kotlin
   fun loadData() {
       viewModelScope.launch {
           // Coroutine code
       }
   }
   ```

2. **Handle exceptions**:
   ```kotlin
   viewModelScope.launch {
       try {
           val data = repository.getData()
           _state.value = data
       } catch (e: Exception) {
           _error.value = e.message
       }
   }
   ```

3. **Use appropriate dispatchers**:
   ```kotlin
   withContext(Dispatchers.IO) {
       // IO operations
   }
   ```

---

## Debugging

### Logging

Use Android's Log class:

```kotlin
import android.util.Log

private const val TAG = "FlashcardViewModel"

Log.d(TAG, "Loading flashcards for unit: $unitId")
Log.e(TAG, "Error loading flashcards", exception)
```

### Debugging ViewModels

Add debug logs:

```kotlin
private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    .also { flow ->
        flow.onEach { list ->
            Log.d(TAG, "Flashcards updated: ${list.size} items")
        }
    }
```

### Network Debugging

Use Charles Proxy or Android Studio's Network Profiler to inspect API calls.

---

## Additional Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose Guidelines](https://developer.android.com/jetpack/compose/guidelines)
- [Effective Kotlin](https://kt.academy/article/ek-introduction)
- [Android Architecture Samples](https://github.com/android/architecture-samples)

---

## Questions?

If you have questions about development:

1. Check existing documentation
2. Search closed issues on GitHub
3. Ask in team discussions
4. Contact the maintainers

---

**Happy Coding!** 🚀

**Last Updated**: January 2025
