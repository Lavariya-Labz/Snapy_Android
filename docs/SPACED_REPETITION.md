# Spaced Repetition (SM2 Algorithm)

This document explains the spaced repetition system used in Snapy, based on the SuperMemo 2 (SM2) algorithm.

---

## Table of Contents

- [Introduction](#introduction)
- [What is Spaced Repetition?](#what-is-spaced-repetition)
- [The SM2 Algorithm](#the-sm2-algorithm)
- [Implementation in Snapy](#implementation-in-snapy)
- [Algorithm Details](#algorithm-details)
- [User Progress Tracking](#user-progress-tracking)
- [Review Schedule](#review-schedule)
- [Code Examples](#code-examples)
- [Best Practices](#best-practices)

---

## Introduction

Snapy uses the **SuperMemo 2 (SM2)** spaced repetition algorithm to optimize learning and memory retention. This proven algorithm schedules flashcard reviews at optimal intervals to maximize long-term retention while minimizing study time.

### Key Benefits

- 📈 **Improved Retention**: Review cards just before you're likely to forget them
- ⏱️ **Time Efficient**: Focus on cards you struggle with, less time on easy cards
- 🧠 **Optimized Learning**: Scientifically-proven method for long-term memory
- 📊 **Personalized**: Adapts to your individual learning pace

---

## What is Spaced Repetition?

**Spaced repetition** is a learning technique that involves reviewing information at increasing intervals over time. The core principle is based on the **forgetting curve**:

```
Retention
   100% │
        │●
        │ ●
        │  ●
        │   ●
        │    ●●
        │      ●●●
        │         ●●●●●●●●●
      0%└───────────────────────► Time
```

Without review, you forget information rapidly. Spaced repetition combats this by:

1. **Initial Review**: Shortly after learning (e.g., 1 day)
2. **Subsequent Reviews**: At increasing intervals (e.g., 3 days, 7 days, 14 days)
3. **Adaptive Scheduling**: Adjust intervals based on recall success

---

## The SM2 Algorithm

The **SuperMemo 2 (SM2)** algorithm, developed by Piotr Woźniak in 1988, is one of the most widely-used spaced repetition algorithms.

### Core Concepts

1. **Ease Factor (EF)**: How "easy" a card is for you (default: 2.5)
2. **Interval (I)**: Days until next review
3. **Repetitions (n)**: Number of consecutive successful reviews
4. **Quality (Q)**: Your self-assessment of recall quality (0-5)

### Quality Scale

| Quality | Description | Meaning |
|---------|-------------|---------|
| 5 | Perfect | Perfect response, instant recall |
| 4 | Correct | Correct response after hesitation |
| 3 | Difficult | Correct response with difficulty |
| 2 | Wrong | Incorrect but easy to recall |
| 1 | Wrong | Incorrect, difficult to recall |
| 0 | Blackout | Complete blackout, no recall |

---

## Implementation in Snapy

Snapy implements SM2 with two difficulty ratings for simplicity:

### Simplified Quality Mapping

| User Response | SM2 Quality | Action |
|---------------|-------------|--------|
| **Easy** (✅) | 5 | Increase interval significantly |
| **Medium** (👍) | 4 | Increase interval moderately |
| **Hard** (😓) | 3 | Increase interval slightly |
| **Wrong** (❌) | 0-2 | Reset interval, review soon |

### UserProgress Model

```kotlin
@Serializable
data class UserProgress(
    val id: Long,
    val userId: Long,
    val flashcardId: Long,
    val easeFactor: Float = 2.5f,    // SM2 ease factor
    val interval: Int = 0,            // Days until next review
    val repetitions: Int = 0,          // Successful review count
    val nextReviewDate: String? = null,
    val lastReviewDate: String? = null
)
```

---

## Algorithm Details

### Step-by-Step Process

#### 1. Initial State (New Card)

```kotlin
val newProgress = UserProgress(
    easeFactor = 2.5f,
    interval = 0,
    repetitions = 0,
    nextReviewDate = today
)
```

#### 2. After User Review

The algorithm updates based on user response quality (Q):

```kotlin
fun updateProgress(
    progress: UserProgress,
    quality: Int  // 0-5
): UserProgress {
    // Step 1: Update ease factor
    val newEF = calculateNewEaseFactor(progress.easeFactor, quality)

    // Step 2: Calculate interval and repetitions
    val (newInterval, newRepetitions) = if (quality < 3) {
        // Failed: Reset progress
        Pair(1, 0)
    } else {
        // Passed: Calculate next interval
        calculateNextInterval(progress.interval, progress.repetitions, newEF)
    }

    // Step 3: Calculate next review date
    val nextReviewDate = today.plusDays(newInterval)

    return progress.copy(
        easeFactor = newEF,
        interval = newInterval,
        repetitions = newRepetitions,
        nextReviewDate = nextReviewDate.toString(),
        lastReviewDate = today.toString()
    )
}
```

### Ease Factor Calculation

```kotlin
fun calculateNewEaseFactor(currentEF: Float, quality: Int): Float {
    val newEF = currentEF + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))

    // Minimum ease factor is 1.3
    return max(1.3f, newEF)
}
```

**Examples:**
- Quality 5: EF = 2.5 + 0.1 = **2.6** (gets easier)
- Quality 4: EF = 2.5 + 0.02 = **2.52** (slightly easier)
- Quality 3: EF = 2.5 - 0.14 = **2.36** (slightly harder)
- Quality 0: EF = 2.5 - 0.8 = **1.7** (much harder)

### Interval Calculation

```kotlin
fun calculateNextInterval(
    currentInterval: Int,
    repetitions: Int,
    easeFactor: Float
): Pair<Int, Int> {
    return when (repetitions) {
        0 -> Pair(1, 1)        // First review: 1 day
        1 -> Pair(6, 2)        // Second review: 6 days
        else -> {
            // Subsequent reviews: multiply by ease factor
            val newInterval = (currentInterval * easeFactor).roundToInt()
            Pair(newInterval, repetitions + 1)
        }
    }
}
```

**Example Progression (EF = 2.5):**
```
Review 1: 1 day
Review 2: 6 days
Review 3: 6 × 2.5 = 15 days
Review 4: 15 × 2.5 = 38 days
Review 5: 38 × 2.5 = 95 days
Review 6: 95 × 2.5 = 238 days
```

---

## User Progress Tracking

### Database Storage

```sql
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
```

### Progress States

```
┌─────────────┐
│   New Card  │ interval = 0, repetitions = 0
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Learning   │ interval = 1-6, repetitions = 0-1
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Review    │ interval > 6, repetitions > 1
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Mature    │ interval > 21, repetitions > 3
└─────────────┘
```

---

## Review Schedule

### Daily Review Algorithm

```kotlin
fun getCardsForReview(userId: Long, today: LocalDate): List<Flashcard> {
    return repository.getFlashcardsWhere(
        userId = userId,
        condition = "next_review_date <= '$today' OR next_review_date IS NULL"
    )
}
```

### Priority Order

Cards are reviewed in this priority:

1. **New Cards**: Never reviewed before
2. **Failed Cards**: Recently failed (interval = 1)
3. **Due Cards**: Scheduled for today
4. **Overdue Cards**: Scheduled for earlier dates

```kotlin
fun sortCardsByPriority(cards: List<Flashcard>): List<Flashcard> {
    return cards.sortedWith(
        compareBy<Flashcard> { card ->
            when {
                card.progress == null -> 0           // New cards first
                card.progress.interval == 1 -> 1     // Failed cards second
                card.progress.nextReviewDate!! < today -> 2  // Overdue third
                else -> 3                            // Regular due cards last
            }
        }.thenBy { it.progress?.nextReviewDate }   // Then by date
    )
}
```

---

## Code Examples

### Example 1: Recording a Review

```kotlin
suspend fun recordReview(
    userId: Long,
    flashcardId: Long,
    quality: Int  // 0-5
) {
    // Get current progress or create new
    val currentProgress = repository.getUserProgress(userId, flashcardId)
        ?: UserProgress(
            userId = userId,
            flashcardId = flashcardId,
            easeFactor = 2.5f,
            interval = 0,
            repetitions = 0
        )

    // Calculate new progress using SM2
    val updatedProgress = calculateSM2(currentProgress, quality)

    // Save to database
    repository.saveUserProgress(updatedProgress)

    // Record response
    repository.saveQuizResponse(
        QuizResponse(
            userId = userId,
            flashcardId = flashcardId,
            quality = quality,
            timestamp = Clock.System.now().toString()
        )
    )
}
```

### Example 2: Complete SM2 Implementation

```kotlin
object SM2Algorithm {

    fun calculateSM2(
        progress: UserProgress,
        quality: Int
    ): UserProgress {
        require(quality in 0..5) { "Quality must be between 0 and 5" }

        // Calculate new ease factor
        val newEF = calculateEaseFactor(progress.easeFactor, quality)

        // Determine if card was recalled successfully
        val successful = quality >= 3

        val (newInterval, newRepetitions) = if (successful) {
            // Success: calculate next interval
            when (progress.repetitions) {
                0 -> Pair(1, 1)
                1 -> Pair(6, 2)
                else -> {
                    val interval = (progress.interval * newEF).roundToInt()
                    Pair(interval, progress.repetitions + 1)
                }
            }
        } else {
            // Failure: reset progress
            Pair(1, 0)
        }

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val nextReviewDate = today.plus(newInterval, DateTimeUnit.DAY)

        return progress.copy(
            easeFactor = newEF,
            interval = newInterval,
            repetitions = newRepetitions,
            nextReviewDate = nextReviewDate.toString(),
            lastReviewDate = today.toString()
        )
    }

    private fun calculateEaseFactor(currentEF: Float, quality: Int): Float {
        val adjustment = 0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f)
        val newEF = currentEF + adjustment
        return max(1.3f, newEF)
    }
}
```

### Example 3: User Interaction Flow

```kotlin
// In FlashcardViewModel
fun onUserResponse(difficulty: Difficulty) {
    viewModelScope.launch {
        val quality = when (difficulty) {
            Difficulty.EASY -> 5      // Perfect recall
            Difficulty.MEDIUM -> 4    // Good recall
            Difficulty.HARD -> 3      // Difficult recall
        }

        val flashcard = currentFlashcard.value
        recordReview(
            userId = currentUserId,
            flashcardId = flashcard.id,
            quality = quality
        )

        // Move to next card
        loadNextCard()
    }
}
```

---

## Best Practices

### 1. Honest Self-Assessment

Encourage users to be honest about their recall:
- **Easy**: Instant, effortless recall
- **Medium**: Correct but required thinking
- **Hard**: Struggled but eventually recalled

### 2. Consistency

Review cards daily for best results:
```kotlin
// Recommend reviewing at the same time each day
val reviewReminderTime = "09:00 AM"
```

### 3. New Cards Limit

Limit new cards per day to avoid overwhelming users:
```kotlin
const val MAX_NEW_CARDS_PER_DAY = 20
```

### 4. Failed Cards

Review failed cards again in the same session:
```kotlin
if (quality < 3) {
    addToRetryQueue(flashcard)
}
```

### 5. Mature Cards

Cards with long intervals (21+ days) are "mature":
```kotlin
fun isMature(progress: UserProgress): Boolean {
    return progress.interval >= 21 && progress.repetitions >= 3
}
```

---

## Performance Metrics

### Tracking Learning Efficiency

```kotlin
data class StudyStats(
    val totalCards: Int,
    val matureCards: Int,
    val averageEaseFactor: Float,
    val averageInterval: Int,
    val retentionRate: Float  // % of cards answered correctly
)

fun calculateStats(userId: Long): StudyStats {
    val allProgress = repository.getAllUserProgress(userId)

    return StudyStats(
        totalCards = allProgress.size,
        matureCards = allProgress.count { it.interval >= 21 },
        averageEaseFactor = allProgress.map { it.easeFactor }.average().toFloat(),
        averageInterval = allProgress.map { it.interval }.average().toInt(),
        retentionRate = calculateRetentionRate(userId)
    )
}
```

---

## Visualizing Progress

### Review Heatmap

Track daily review counts:
```kotlin
data class DailyReview(
    val date: LocalDate,
    val cardCount: Int,
    val correctCount: Int
)

fun getReviewHeatmap(userId: Long, days: Int = 30): List<DailyReview> {
    val startDate = LocalDate.now().minusDays(days)
    return repository.getReviewHistory(userId, startDate)
}
```

### Card Distribution

```
New:      ████████░░ (40%)
Learning: ██████░░░░ (30%)
Review:   ████░░░░░░ (20%)
Mature:   ██░░░░░░░░ (10%)
```

---

## Advanced Features

### 1. Leech Detection

Identify cards that are repeatedly failed:
```kotlin
fun isLeech(flashcardId: Long, userId: Long): Boolean {
    val failureCount = repository.getRecentFailures(flashcardId, userId, days = 30)
    return failureCount >= 8  // Failed 8+ times in 30 days
}
```

### 2. Load Balancing

Distribute reviews evenly across days:
```kotlin
fun adjustReviewDate(targetDate: LocalDate, dailyLimit: Int): LocalDate {
    val cardsOnDate = repository.getReviewCountForDate(targetDate)
    return if (cardsOnDate >= dailyLimit) {
        targetDate.plusDays(1)  // Push to next day
    } else {
        targetDate
    }
}
```

### 3. Fuzz Factor

Add randomness to intervals to avoid review clustering:
```kotlin
fun applyFuzzFactor(interval: Int): Int {
    if (interval < 3) return interval

    val fuzzRange = interval * 0.05  // ±5%
    val fuzz = Random.nextInt(-fuzzRange.toInt(), fuzzRange.toInt())
    return max(1, interval + fuzz)
}
```

---

## References

- [SuperMemo 2 Algorithm](https://super-memory.com/english/ol/sm2.htm) - Original SM2 documentation
- [Anki Manual](https://docs.ankiweb.net/studying.html) - Popular SRS implementation
- [Spaced Repetition Research](https://www.gwern.net/Spaced-repetition) - Comprehensive research overview

---

## Related Documentation

- [Data Models](DATA_MODELS.md) - UserProgress model details
- [Supabase Integration](SUPABASE_INTEGRATION.md) - How progress is stored
- [Development Guide](DEVELOPMENT_GUIDE.md) - Implementing new SRS features

---

**Last Updated**: January 2025
