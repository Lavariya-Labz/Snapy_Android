# Data Models Documentation

This document provides a comprehensive overview of all data models used in the Snapy Android application.

---

## Table of Contents

- [Overview](#overview)
- [Data Hierarchy](#data-hierarchy)
- [Core Models](#core-models)
  - [User](#user)
  - [Grade](#grade)
  - [Subject](#subject)
  - [Term](#term)
  - [StudyUnit](#studyunit)
  - [Flashcard](#flashcard)
- [Supporting Models](#supporting-models)
  - [QuizOption](#quizoption)
  - [UserProgress](#userprogress)
  - [QuizResponse](#quizresponse)
  - [QuizSession](#quizsession)
- [Enumerations](#enumerations)
- [Database Schema](#database-schema)
- [Relationships](#relationships)

---

## Overview

Snapy uses a hierarchical data structure to organize educational content:

```
User
  └─ Grade
      └─ Subject
          └─ Term
              └─ StudyUnit
                  └─ Flashcard
                      ├─ QuizOptions (for MCQ)
                      └─ UserProgress (for spaced repetition)
```

All models are:
- **Serializable** using Kotlinx Serialization
- **Immutable** using `data class`
- **Null-safe** with explicit nullable types
- **Type-safe** with sealed classes and enums

---

## Data Hierarchy

### Educational Content Flow

```
┌─────────────┐
│    Grade    │  Educational level (e.g., Grade 10)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Subject   │  Study subject (e.g., Mathematics)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│    Term     │  Academic term (e.g., Term 1, Semester 1)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Study Unit  │  Collection of related flashcards
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Flashcard  │  Individual study card
└─────────────┘
```

---

## Core Models

### User

Represents a user of the application.

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/User.kt`

```kotlin
@Serializable
data class User(
    val id: Long,
    val name: String,
    val language: String = "en",
    val gradeId: Long? = null,
    val subjectId: Long? = null,
    val createdAt: String? = null
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique user identifier | No |
| `name` | `String` | User's display name | No |
| `language` | `String` | Preferred language code (default: "en") | No |
| `gradeId` | `Long?` | Selected grade ID | Yes |
| `subjectId` | `Long?` | Selected subject ID | Yes |
| `createdAt` | `String?` | Account creation timestamp | Yes |

**Example:**
```kotlin
val user = User(
    id = 12345L,
    name = "John Doe",
    language = "en",
    gradeId = 10L,
    subjectId = 2L,
    createdAt = "2025-01-22T10:30:00Z"
)
```

---

### Grade

Represents an educational grade level.

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/Grade.kt`

```kotlin
@Serializable
data class Grade(
    val id: Long,
    val name: String,
    val level: Int,
    val description: String? = null
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique grade identifier | No |
| `name` | `String` | Grade display name (e.g., "Grade 10") | No |
| `level` | `Int` | Numeric grade level | No |
| `description` | `String?` | Optional grade description | Yes |

**Example:**
```kotlin
val grade = Grade(
    id = 10L,
    name = "Grade 10",
    level = 10,
    description = "Secondary education level 10"
)
```

---

### Subject

Represents an academic subject.

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/Subject.kt`

```kotlin
@Serializable
data class Subject(
    val id: Long,
    val name: String,
    val description: String? = null,
    val icon: String? = null
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique subject identifier | No |
| `name` | `String` | Subject name (e.g., "Mathematics") | No |
| `description` | `String?` | Subject description | Yes |
| `icon` | `String?` | Icon identifier or URL | Yes |

**Example:**
```kotlin
val subject = Subject(
    id = 2L,
    name = "Mathematics",
    description = "Study of numbers, quantities, and shapes",
    icon = "math_icon"
)
```

---

### Term

Represents an academic term or semester.

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/Term.kt`

```kotlin
@Serializable
data class Term(
    val id: Long,
    val name: String,
    val gradeId: Long,
    val startDate: String? = null,
    val endDate: String? = null
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique term identifier | No |
| `name` | `String` | Term name (e.g., "Term 1", "Semester 1") | No |
| `gradeId` | `Long` | Associated grade ID | No |
| `startDate` | `String?` | Term start date (ISO 8601) | Yes |
| `endDate` | `String?` | Term end date (ISO 8601) | Yes |

**Example:**
```kotlin
val term = Term(
    id = 1L,
    name = "Term 1",
    gradeId = 10L,
    startDate = "2025-01-01",
    endDate = "2025-04-30"
)
```

---

### StudyUnit

Represents a collection of related flashcards (also called a library).

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/StudyUnit.kt`

```kotlin
@Serializable
data class StudyUnit(
    val id: Long,
    val name: String,
    val description: String? = null,
    val termId: Long,
    val subjectId: Long,
    val flashcardCount: Int = 0,
    val createdAt: String? = null
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique unit identifier | No |
| `name` | `String` | Unit name (e.g., "Algebra Basics") | No |
| `description` | `String?` | Unit description | Yes |
| `termId` | `Long` | Associated term ID | No |
| `subjectId` | `Long` | Associated subject ID | No |
| `flashcardCount` | `Int` | Number of flashcards in unit | No |
| `createdAt` | `String?` | Creation timestamp | Yes |

**Example:**
```kotlin
val unit = StudyUnit(
    id = 101L,
    name = "Quadratic Equations",
    description = "Study quadratic equations and their solutions",
    termId = 1L,
    subjectId = 2L,
    flashcardCount = 25,
    createdAt = "2025-01-15T08:00:00Z"
)
```

---

### Flashcard

Represents an individual flashcard with question and answer.

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/Flashcard.kt`

```kotlin
@Serializable
data class Flashcard(
    val id: Long,
    val unitId: Long,
    val question: String,
    val answer: String,
    val type: FlashcardType = FlashcardType.SELF_EVAL,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val options: List<QuizOption>? = null,
    val explanation: String? = null,
    val createdAt: String? = null
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique flashcard identifier | No |
| `unitId` | `Long` | Associated study unit ID | No |
| `question` | `String` | Question text | No |
| `answer` | `String` | Correct answer | No |
| `type` | `FlashcardType` | Card type (SELF_EVAL or MCQ) | No |
| `difficulty` | `Difficulty` | Difficulty level | No |
| `options` | `List<QuizOption>?` | MCQ options (required for MCQ type) | Yes |
| `explanation` | `String?` | Additional explanation | Yes |
| `createdAt` | `String?` | Creation timestamp | Yes |

**Example (Self-Evaluation):**
```kotlin
val selfEvalCard = Flashcard(
    id = 1001L,
    unitId = 101L,
    question = "What is the quadratic formula?",
    answer = "x = (-b ± √(b²-4ac)) / 2a",
    type = FlashcardType.SELF_EVAL,
    difficulty = Difficulty.MEDIUM,
    explanation = "Used to solve ax² + bx + c = 0"
)
```

**Example (Multiple Choice):**
```kotlin
val mcqCard = Flashcard(
    id = 1002L,
    unitId = 101L,
    question = "What is 2 + 2?",
    answer = "4",
    type = FlashcardType.MCQ,
    difficulty = Difficulty.EASY,
    options = listOf(
        QuizOption(id = 1L, optionText = "3", isCorrect = false),
        QuizOption(id = 2L, optionText = "4", isCorrect = true),
        QuizOption(id = 3L, optionText = "5", isCorrect = false),
        QuizOption(id = 4L, optionText = "6", isCorrect = false)
    )
)
```

---

## Supporting Models

### QuizOption

Represents a multiple-choice option for MCQ flashcards.

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/QuizOption.kt`

```kotlin
@Serializable
data class QuizOption(
    val id: Long,
    val optionText: String,
    val isCorrect: Boolean
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique option identifier | No |
| `optionText` | `String` | Option text (e.g., "A) 3") | No |
| `isCorrect` | `Boolean` | Whether this is the correct answer | No |

**Example:**
```kotlin
val option = QuizOption(
    id = 1L,
    optionText = "The Pythagorean Theorem",
    isCorrect = true
)
```

---

### UserProgress

Tracks user's progress with spaced repetition (SM2 algorithm).

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/UserProgress.kt`

```kotlin
@Serializable
data class UserProgress(
    val id: Long,
    val userId: Long,
    val flashcardId: Long,
    val easeFactor: Float = 2.5f,
    val interval: Int = 0,
    val repetitions: Int = 0,
    val nextReviewDate: String? = null,
    val lastReviewDate: String? = null
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique progress record identifier | No |
| `userId` | `Long` | Associated user ID | No |
| `flashcardId` | `Long` | Associated flashcard ID | No |
| `easeFactor` | `Float` | SM2 ease factor (default: 2.5) | No |
| `interval` | `Int` | Days until next review | No |
| `repetitions` | `Int` | Number of successful repetitions | No |
| `nextReviewDate` | `String?` | Next scheduled review date | Yes |
| `lastReviewDate` | `String?` | Last review date | Yes |

**Example:**
```kotlin
val progress = UserProgress(
    id = 5001L,
    userId = 12345L,
    flashcardId = 1001L,
    easeFactor = 2.6f,
    interval = 7,
    repetitions = 3,
    nextReviewDate = "2025-01-29",
    lastReviewDate = "2025-01-22"
)
```

**See also**: [Spaced Repetition Documentation](SPACED_REPETITION.md)

---

### QuizResponse

Records a user's response to a flashcard.

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/QuizResponse.kt`

```kotlin
@Serializable
data class QuizResponse(
    val id: Long,
    val userId: Long,
    val flashcardId: Long,
    val sessionId: Long,
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val responseTime: Int,
    val timestamp: String
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique response identifier | No |
| `userId` | `Long` | User who responded | No |
| `flashcardId` | `Long` | Flashcard answered | No |
| `sessionId` | `Long` | Associated quiz session | No |
| `selectedAnswer` | `String` | User's selected answer | No |
| `isCorrect` | `Boolean` | Whether answer was correct | No |
| `responseTime` | `Int` | Time taken to answer (seconds) | No |
| `timestamp` | `String` | Response timestamp | No |

**Example:**
```kotlin
val response = QuizResponse(
    id = 8001L,
    userId = 12345L,
    flashcardId = 1002L,
    sessionId = 3001L,
    selectedAnswer = "4",
    isCorrect = true,
    responseTime = 5,
    timestamp = "2025-01-22T14:30:00Z"
)
```

---

### QuizSession

Tracks a study session.

**File**: `app/src/main/java/com/lavariyalabs/snapy/android/data/model/QuizSession.kt`

```kotlin
@Serializable
data class QuizSession(
    val id: Long,
    val userId: Long,
    val unitId: Long,
    val startTime: String,
    val endTime: String? = null,
    val totalCards: Int,
    val cardsCompleted: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0
)
```

**Fields:**

| Field | Type | Description | Nullable |
|-------|------|-------------|----------|
| `id` | `Long` | Unique session identifier | No |
| `userId` | `Long` | User conducting session | No |
| `unitId` | `Long` | Study unit being reviewed | No |
| `startTime` | `String` | Session start timestamp | No |
| `endTime` | `String?` | Session end timestamp | Yes |
| `totalCards` | `Int` | Total cards in session | No |
| `cardsCompleted` | `Int` | Cards completed so far | No |
| `correctAnswers` | `Int` | Number of correct answers | No |
| `incorrectAnswers` | `Int` | Number of incorrect answers | No |

**Example:**
```kotlin
val session = QuizSession(
    id = 3001L,
    userId = 12345L,
    unitId = 101L,
    startTime = "2025-01-22T14:00:00Z",
    endTime = "2025-01-22T14:45:00Z",
    totalCards = 25,
    cardsCompleted = 25,
    correctAnswers = 20,
    incorrectAnswers = 5
)
```

---

## Enumerations

### FlashcardType

Defines the type of flashcard.

```kotlin
enum class FlashcardType {
    SELF_EVAL,  // User self-evaluates if they got it right
    MCQ         // Multiple choice question
}
```

### Difficulty

Defines the difficulty level of a flashcard.

```kotlin
enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}
```

---

## Database Schema

The app uses Supabase (PostgreSQL) as the backend. Here's the database schema:

### Tables

```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    language VARCHAR(10) DEFAULT 'en',
    grade_id BIGINT,
    subject_id BIGINT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Grades table
CREATE TABLE grades (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    level INTEGER NOT NULL,
    description TEXT
);

-- Subjects table
CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(255)
);

-- Terms table
CREATE TABLE terms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    grade_id BIGINT REFERENCES grades(id),
    start_date DATE,
    end_date DATE
);

-- Study units table
CREATE TABLE study_units (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    term_id BIGINT REFERENCES terms(id),
    subject_id BIGINT REFERENCES subjects(id),
    flashcard_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Flashcards table
CREATE TABLE flashcards (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT REFERENCES study_units(id),
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    type VARCHAR(20) DEFAULT 'SELF_EVAL',
    difficulty VARCHAR(20) DEFAULT 'MEDIUM',
    explanation TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Quiz options table
CREATE TABLE quiz_options (
    id BIGSERIAL PRIMARY KEY,
    flashcard_id BIGINT REFERENCES flashcards(id),
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE
);

-- User progress table (spaced repetition)
CREATE TABLE user_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    flashcard_id BIGINT REFERENCES flashcards(id),
    ease_factor FLOAT DEFAULT 2.5,
    interval INTEGER DEFAULT 0,
    repetitions INTEGER DEFAULT 0,
    next_review_date DATE,
    last_review_date DATE,
    UNIQUE(user_id, flashcard_id)
);

-- Quiz responses table
CREATE TABLE quiz_responses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    flashcard_id BIGINT REFERENCES flashcards(id),
    session_id BIGINT REFERENCES quiz_sessions(id),
    selected_answer TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    response_time INTEGER,
    timestamp TIMESTAMP DEFAULT NOW()
);

-- Quiz sessions table
CREATE TABLE quiz_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    unit_id BIGINT REFERENCES study_units(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    total_cards INTEGER NOT NULL,
    cards_completed INTEGER DEFAULT 0,
    correct_answers INTEGER DEFAULT 0,
    incorrect_answers INTEGER DEFAULT 0
);
```

---

## Relationships

### Entity Relationship Diagram

```
┌─────────┐
│  User   │
└────┬────┘
     │
     ├─────────────┐
     │             │
     ▼             ▼
┌─────────┐   ┌──────────────┐
│  Grade  │   │UserProgress  │
└────┬────┘   └──────────────┘
     │
     ▼
┌─────────┐
│ Subject │◄──────┐
└────┬────┘       │
     │            │
     ▼            │
┌─────────┐       │
│  Term   │       │
└────┬────┘       │
     │            │
     ▼            │
┌────────────┐    │
│ StudyUnit  │────┘
└─────┬──────┘
      │
      ▼
┌───────────┐
│ Flashcard │◄────────────┐
└─────┬─────┘             │
      │                   │
      ├──────┐            │
      ▼      ▼            │
┌─────────┐ ┌────────────────┐
│QuizOption│ │QuizResponse    │
└─────────┘ └────────────────┘
            ┌────────────────┐
            │ QuizSession    │
            └────────────────┘
```

### Relationship Types

1. **One-to-Many**:
   - Grade → Term (one grade has many terms)
   - Subject → StudyUnit (one subject has many units)
   - Term → StudyUnit (one term has many units)
   - StudyUnit → Flashcard (one unit has many flashcards)
   - Flashcard → QuizOption (one flashcard has many options)
   - User → QuizSession (one user has many sessions)
   - QuizSession → QuizResponse (one session has many responses)

2. **Many-to-One**:
   - User → Grade (many users in one grade)
   - User → Subject (many users studying one subject)

3. **One-to-One**:
   - User + Flashcard → UserProgress (unique progress per user-flashcard pair)

---

## Best Practices

### 1. Immutability
All models use `data class` for immutability. To modify, create a copy:

```kotlin
val updatedUser = user.copy(name = "Jane Doe")
```

### 2. Null Safety
Use nullable types (`?`) only when necessary:

```kotlin
// Good
val name: String  // Always required

// Use nullable only when optional
val description: String? = null
```

### 3. Default Values
Provide sensible defaults:

```kotlin
val easeFactor: Float = 2.5f  // SM2 default
val language: String = "en"   // Default language
```

### 4. Serialization
All models must be annotated with `@Serializable`:

```kotlin
@Serializable
data class MyModel(...)
```

### 5. Naming Conventions
- Use camelCase for field names
- Use descriptive names (avoid abbreviations)
- Match database column names (snake_case → camelCase)

---

## Additional Resources

- [Supabase Integration](SUPABASE_INTEGRATION.md) - API and database details
- [Spaced Repetition](SPACED_REPETITION.md) - SM2 algorithm implementation
- [MVVM Architecture](MVVM_ARCHITECTURE_SUMMARY.md) - How models fit into MVVM

---

**Last Updated**: January 2025
