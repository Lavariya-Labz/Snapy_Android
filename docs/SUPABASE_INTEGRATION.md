# Supabase Integration Documentation

This document provides a comprehensive guide to the Supabase backend integration in the Snapy Android application.

---

## Table of Contents

- [Overview](#overview)
- [Supabase Setup](#supabase-setup)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [Data Source Layer](#data-source-layer)
- [API Methods](#api-methods)
- [Authentication](#authentication)
- [Error Handling](#error-handling)
- [Best Practices](#best-practices)
- [Testing](#testing)

---

## Overview

Snapy uses **Supabase** as its Backend-as-a-Service (BaaS) solution, providing:

- **PostgreSQL Database**: Relational database with full SQL support
- **PostgREST API**: Auto-generated RESTful API from database schema
- **Real-time Subscriptions**: Live data updates (future feature)
- **Authentication**: User authentication system (future feature)
- **Row Level Security**: Database-level access control

### Tech Stack

- **Supabase Kotlin SDK** v3.2.6
- **Postgrest-kt**: Database queries via PostgREST
- **Ktor Client** v3.3.3: HTTP client for API communication
- **Kotlinx Serialization**: JSON serialization/deserialization

---

## Supabase Setup

### Project Information

```kotlin
URL: https://lriggiqgikqhsfqlojlj.supabase.co
Anonymous Key: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Note**: The anonymous key is safe to expose in client-side code. It only grants access to public tables and respects Row Level Security policies.

### Installed Modules

1. **Postgrest**: Database queries
2. **Auth**: Authentication (currently installed, not yet used)

---

## Configuration

### SupabaseConfig.kt

**Location**: `app/src/main/java/com/lavariyalabs/snapy/android/config/SupabaseConfig.kt`

```kotlin
object SupabaseConfig {

    private const val SUPABASE_URL = "https://lriggiqgikqhsfqlojlj.supabase.co"
    private const val SUPABASE_ANON_KEY = "your-anon-key"

    val supabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        // Install modules
        install(Postgrest)  // Database queries
        install(Auth)       // Authentication

        // Configure timeout
        requestTimeout = 60.seconds

        // Configure JSON serialization
        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true    // Ignore unknown JSON fields
            coerceInputValues = true    // Coerce invalid inputs to defaults
            encodeDefaults = true       // Include default values in JSON
        })
    }
}
```

### Configuration Options

| Option | Value | Description |
|--------|-------|-------------|
| `requestTimeout` | 60 seconds | Maximum time for API requests |
| `ignoreUnknownKeys` | true | Ignore extra fields in JSON responses |
| `coerceInputValues` | true | Convert invalid values to defaults |
| `encodeDefaults` | true | Include default values when encoding |

---

## Database Schema

### Tables Overview

```
grades
  ├─ subjects (via grade_id)
  │   └─ terms (via subject_id)
  │       └─ units (via term_id)
  │           └─ flashcards (via unit_id)
  │               ├─ quiz_options (via flashcard_id)
  │               └─ user_progress (via flashcard_id)
  │
  └─ users (via grade_id)
      ├─ user_progress (via user_id)
      ├─ quiz_responses (via user_id)
      └─ quiz_sessions (via user_id)
```

### Table Definitions

#### 1. grades

```sql
CREATE TABLE grades (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    level INTEGER NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);
```

**Columns:**
- `id`: Unique identifier
- `name`: Grade name (e.g., "Grade 10")
- `level`: Numeric level (e.g., 10)
- `description`: Optional description

#### 2. subjects

```sql
CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(255),
    grade_id BIGINT REFERENCES grades(id),
    created_at TIMESTAMP DEFAULT NOW()
);
```

**Columns:**
- `id`: Unique identifier
- `name`: Subject name (e.g., "Mathematics")
- `grade_id`: Associated grade
- `icon`: Icon identifier

#### 3. terms

```sql
CREATE TABLE terms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    subject_id BIGINT REFERENCES subjects(id),
    term_number INTEGER,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT NOW()
);
```

**Columns:**
- `id`: Unique identifier
- `name`: Term name (e.g., "Term 1")
- `subject_id`: Associated subject
- `term_number`: Numeric order (1, 2, 3...)

#### 4. units

```sql
CREATE TABLE units (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    term_id BIGINT REFERENCES terms(id),
    order_index INTEGER,
    flashcard_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);
```

**Columns:**
- `id`: Unique identifier
- `name`: Unit name
- `term_id`: Associated term
- `order_index`: Display order
- `flashcard_count`: Number of flashcards

#### 5. flashcards

```sql
CREATE TABLE flashcards (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT REFERENCES units(id),
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    type VARCHAR(20) DEFAULT 'SELF_EVAL',
    difficulty VARCHAR(20) DEFAULT 'MEDIUM',
    explanation TEXT,
    order_index INTEGER,
    created_at TIMESTAMP DEFAULT NOW()
);
```

**Columns:**
- `id`: Unique identifier
- `unit_id`: Associated unit
- `question`: Question text
- `answer`: Answer text
- `type`: "SELF_EVAL" or "MCQ"
- `difficulty`: "EASY", "MEDIUM", or "HARD"
- `order_index`: Display order

#### 6. quiz_options

```sql
CREATE TABLE quiz_options (
    id BIGSERIAL PRIMARY KEY,
    flashcard_id BIGINT REFERENCES flashcards(id),
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    order_index INTEGER
);
```

**Columns:**
- `id`: Unique identifier
- `flashcard_id`: Associated flashcard
- `option_text`: Option text
- `is_correct`: Whether this is the correct answer
- `order_index`: Display order (A, B, C, D)

#### 7. user_progress

```sql
CREATE TABLE user_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    flashcard_id BIGINT REFERENCES flashcards(id),
    ease_factor FLOAT DEFAULT 2.5,
    interval INTEGER DEFAULT 0,
    repetitions INTEGER DEFAULT 0,
    next_review_date DATE,
    last_review_date DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, flashcard_id)
);
```

**Columns:**
- `id`: Unique identifier
- `user_id`: User identifier
- `flashcard_id`: Associated flashcard
- `ease_factor`: SM2 ease factor
- `interval`: Days until next review
- `repetitions`: Successful review count
- `next_review_date`: Next scheduled review
- `last_review_date`: Last review date

#### 8. quiz_responses

```sql
CREATE TABLE quiz_responses (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    flashcard_id BIGINT REFERENCES flashcards(id),
    session_id BIGINT,
    selected_answer TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    response_time INTEGER,
    timestamp TIMESTAMP DEFAULT NOW()
);
```

**Columns:**
- `id`: Unique identifier
- `user_id`: User who responded
- `flashcard_id`: Flashcard answered
- `selected_answer`: User's answer
- `is_correct`: Whether answer was correct
- `response_time`: Time taken (seconds)

---

## Data Source Layer

### SupabaseDataSource.kt

**Location**: `app/src/main/java/com/lavariyalabs/snapy/android/data/remote/SupabaseDataSource.kt`

The `SupabaseDataSource` class handles all API calls to Supabase.

```kotlin
class SupabaseDataSource {
    // Methods for:
    // - Fetching grades, subjects, terms, units
    // - Fetching flashcards and quiz options
    // - Managing user progress
    // - Saving quiz responses
}
```

---

## API Methods

### 1. Grades & Subjects

#### Get All Grades

```kotlin
suspend fun getAllGrades(): List<Grade> {
    return SupabaseConfig.supabaseClient
        .from("grades")
        .select()
        .decodeList<Grade>()
}
```

**Returns**: List of all available grades

**Example**:
```kotlin
val grades = dataSource.getAllGrades()
// Result: [Grade(id=10, name="Grade 10", level=10), ...]
```

#### Get Subjects by Grade

```kotlin
suspend fun getSubjectsByGrade(gradeId: Long): List<Subject> {
    return SupabaseConfig.supabaseClient
        .from("subjects")
        .select {
            filter {
                eq("grade_id", gradeId)
            }
        }
        .decodeList<Subject>()
}
```

**Parameters**:
- `gradeId`: Grade identifier

**Returns**: List of subjects for the specified grade

**Example**:
```kotlin
val subjects = dataSource.getSubjectsByGrade(10L)
// Result: [Subject(id=2, name="Mathematics"), ...]
```

### 2. Terms & Units

#### Get Terms by Subject

```kotlin
suspend fun getTermsBySubject(subjectId: Long): List<Term> {
    return SupabaseConfig.supabaseClient
        .from("terms")
        .select {
            filter {
                eq("subject_id", subjectId)
            }
            order("term_number", Order.ASCENDING)
        }
        .decodeList<Term>()
}
```

**Parameters**:
- `subjectId`: Subject identifier

**Returns**: List of terms ordered by term number

#### Get Units by Term

```kotlin
suspend fun getUnitsByTerm(termId: Long): List<StudyUnit> {
    return SupabaseConfig.supabaseClient
        .from("units")
        .select {
            filter {
                eq("term_id", termId)
            }
            order("order_index", Order.ASCENDING)
        }
        .decodeList<StudyUnit>()
}
```

**Parameters**:
- `termId`: Term identifier

**Returns**: List of units ordered by order_index

### 3. Flashcards & Options

#### Get Flashcards by Unit

```kotlin
suspend fun getFlashcardsByUnit(unitId: Long): List<Flashcard> {
    val flashcards = SupabaseConfig.supabaseClient
        .from("flashcards")
        .select {
            filter {
                eq("unit_id", unitId)
            }
            order("order_index", Order.ASCENDING)
        }
        .decodeList<Flashcard>()

    // For MCQ flashcards, fetch quiz options
    return flashcards.map { card ->
        if (card.type == "MCQ") {
            val options = getQuizOptionsByFlashcard(card.id)
            card.copy(quizOptions = options)
        } else {
            card
        }
    }
}
```

**Parameters**:
- `unitId`: Study unit identifier

**Returns**: List of flashcards with options (for MCQ type)

**Example**:
```kotlin
val flashcards = dataSource.getFlashcardsByUnit(101L)
// Result: List of flashcards with embedded quiz options
```

#### Get Quiz Options

```kotlin
private suspend fun getQuizOptionsByFlashcard(
    flashcardId: Long
): List<QuizOption> {
    return SupabaseConfig.supabaseClient
        .from("quiz_options")
        .select {
            filter {
                eq("flashcard_id", flashcardId)
            }
            order("order_index", Order.ASCENDING)
        }
        .decodeList<QuizOption>()
}
```

**Parameters**:
- `flashcardId`: Flashcard identifier

**Returns**: List of quiz options ordered by order_index

### 4. User Progress

#### Get User Progress

```kotlin
suspend fun getUserProgress(
    userId: String,
    flashcardId: Long
): UserProgress? {
    return SupabaseConfig.supabaseClient
        .from("user_progress")
        .select {
            filter {
                eq("user_id", userId)
                eq("flashcard_id", flashcardId)
            }
        }
        .decodeSingle<UserProgress>()
}
```

**Parameters**:
- `userId`: User identifier
- `flashcardId`: Flashcard identifier

**Returns**: User's progress for the flashcard, or null if not found

#### Save User Progress

```kotlin
suspend fun saveUserProgress(progress: UserProgress) {
    SupabaseConfig.supabaseClient
        .from("user_progress")
        .upsert(progress)  // Insert or update
}
```

**Parameters**:
- `progress`: UserProgress object

**Note**: Uses `upsert` to insert new or update existing progress

**Example**:
```kotlin
val progress = UserProgress(
    userId = "user123",
    flashcardId = 1001L,
    easeFactor = 2.6f,
    interval = 7,
    repetitions = 3,
    nextReviewDate = "2025-01-29"
)
dataSource.saveUserProgress(progress)
```

### 5. Quiz Responses

#### Save Quiz Response

```kotlin
suspend fun saveQuizResponse(response: QuizResponse) {
    SupabaseConfig.supabaseClient
        .from("quiz_responses")
        .insert(response)
}
```

**Parameters**:
- `response`: QuizResponse object

**Example**:
```kotlin
val response = QuizResponse(
    userId = "user123",
    flashcardId = 1002L,
    selectedAnswer = "4",
    isCorrect = true,
    responseTime = 5,
    timestamp = "2025-01-22T14:30:00Z"
)
dataSource.saveQuizResponse(response)
```

---

## Authentication

### Current Implementation

Currently, the app uses **anonymous access** with the anonymous key. All data is public and accessible to any user.

### Future Implementation

The Auth module is installed but not yet used. Future authentication will include:

```kotlin
// Sign up
supabaseClient.auth.signUpWith(Email) {
    email = "user@example.com"
    password = "password123"
}

// Sign in
supabaseClient.auth.signInWith(Email) {
    email = "user@example.com"
    password = "password123"
}

// Get current user
val user = supabaseClient.auth.currentUserOrNull()

// Sign out
supabaseClient.auth.signOut()
```

### Row Level Security (Future)

Once authentication is implemented, enable RLS policies:

```sql
-- Enable RLS on user_progress table
ALTER TABLE user_progress ENABLE ROW LEVEL SECURITY;

-- Policy: Users can only access their own progress
CREATE POLICY "Users can view own progress"
    ON user_progress
    FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can update own progress"
    ON user_progress
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);
```

---

## Error Handling

### Try-Catch Pattern

All API methods use try-catch for error handling:

```kotlin
suspend fun getAllGrades(): List<Grade> {
    return try {
        SupabaseConfig.supabaseClient
            .from("grades")
            .select()
            .decodeList<Grade>()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()  // Return empty list on error
    }
}
```

### Common Errors

| Error Type | Cause | Solution |
|------------|-------|----------|
| `TimeoutException` | Request took > 60s | Check internet connection |
| `SerializationException` | JSON parsing failed | Check data model matches schema |
| `RestException` | API error (4xx/5xx) | Check query syntax and permissions |
| `NetworkException` | No internet | Show offline message |

### Custom Error Handling

For production, implement custom error handling:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

suspend fun getAllGrades(): Result<List<Grade>> {
    return try {
        val grades = SupabaseConfig.supabaseClient
            .from("grades")
            .select()
            .decodeList<Grade>()
        Result.Success(grades)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

---

## Best Practices

### 1. Use Suspend Functions

All data source methods are suspend functions:

```kotlin
// ✅ Good - Use suspend
suspend fun getAllGrades(): List<Grade>

// ❌ Bad - Don't block threads
fun getAllGrades(): List<Grade>
```

### 2. Error Handling

Always handle errors gracefully:

```kotlin
// ✅ Good - Return empty list on error
return try {
    // API call
} catch (e: Exception) {
    e.printStackTrace()
    emptyList()
}

// ❌ Bad - Let exceptions crash the app
return SupabaseConfig.supabaseClient.from("grades").select()
```

### 3. Use Filters

Filter data server-side, not client-side:

```kotlin
// ✅ Good - Filter in query
.from("flashcards")
.select {
    filter {
        eq("unit_id", unitId)
    }
}

// ❌ Bad - Fetch all and filter locally
.from("flashcards")
.select()
.filter { it.unitId == unitId }
```

### 4. Use Ordering

Order data in the query:

```kotlin
// ✅ Good - Order in query
.select {
    order("order_index", Order.ASCENDING)
}

// ❌ Bad - Order locally
.select()
.sortedBy { it.orderIndex }
```

### 5. Pagination (Future)

For large datasets, use pagination:

```kotlin
.select {
    range(0, 9)  // First 10 items
}
```

### 6. Caching

Cache frequently accessed data:

```kotlin
private var gradesCache: List<Grade>? = null
private var cacheTime: Long = 0

suspend fun getAllGrades(): List<Grade> {
    val now = System.currentTimeMillis()
    if (gradesCache != null && now - cacheTime < 5 * 60 * 1000) {
        return gradesCache!!  // Return cached data if < 5 minutes old
    }

    val grades = fetchGradesFromSupabase()
    gradesCache = grades
    cacheTime = now
    return grades
}
```

---

## Testing

### Unit Testing

Mock the data source for testing:

```kotlin
class MockSupabaseDataSource : SupabaseDataSource() {
    override suspend fun getAllGrades(): List<Grade> {
        return listOf(
            Grade(id = 10L, name = "Grade 10", level = 10),
            Grade(id = 11L, name = "Grade 11", level = 11)
        )
    }
}

@Test
fun testGetAllGrades() = runBlocking {
    val dataSource = MockSupabaseDataSource()
    val grades = dataSource.getAllGrades()
    assertEquals(2, grades.size)
}
```

### Integration Testing

Test actual API calls (requires internet):

```kotlin
@Test
fun testSupabaseConnection() = runBlocking {
    val dataSource = SupabaseDataSource()
    val grades = dataSource.getAllGrades()
    assertTrue(grades.isNotEmpty())
}
```

---

## Performance Optimization

### 1. Batch Requests

Instead of multiple single requests:

```kotlin
// ❌ Bad - Multiple requests
val grade = getGradeById(10L)
val subjects = getSubjectsByGrade(10L)
val terms = getTermsBySubject(2L)

// ✅ Good - Batch with joins (future feature)
// Supabase supports foreign table selection
```

### 2. Select Specific Columns

Only fetch needed columns:

```kotlin
// ✅ Good - Select specific columns
.select("id, name, level")

// ❌ Bad - Select all columns (default)
.select()
```

### 3. Use Indexes

Ensure database has proper indexes:

```sql
CREATE INDEX idx_flashcards_unit_id ON flashcards(unit_id);
CREATE INDEX idx_user_progress_user_flashcard ON user_progress(user_id, flashcard_id);
```

---

## Monitoring

### Request Logging

Log all API requests for debugging:

```kotlin
suspend fun getAllGrades(): List<Grade> {
    Log.d("Supabase", "Fetching grades...")
    val startTime = System.currentTimeMillis()

    val grades = try {
        SupabaseConfig.supabaseClient
            .from("grades")
            .select()
            .decodeList<Grade>()
    } catch (e: Exception) {
        Log.e("Supabase", "Error fetching grades", e)
        emptyList()
    }

    val duration = System.currentTimeMillis() - startTime
    Log.d("Supabase", "Fetched ${grades.size} grades in ${duration}ms")

    return grades
}
```

---

## Additional Resources

- [Supabase Documentation](https://supabase.com/docs)
- [Supabase Kotlin SDK](https://github.com/supabase-community/supabase-kt)
- [PostgREST API](https://postgrest.org/en/stable/)
- [Data Models Documentation](DATA_MODELS.md)
- [MVVM Architecture](MVVM_ARCHITECTURE_SUMMARY.md)

---

**Last Updated**: January 2025
