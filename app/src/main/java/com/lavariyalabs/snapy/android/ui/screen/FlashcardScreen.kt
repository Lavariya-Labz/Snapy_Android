package com.lavariyalabs.snapy.android.ui.screen

// File: com/lavariyalabs/snapy/android/ui/screen/FlashcardScreen.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lavariyalabs.snapy.android.ui.components.AnswerButtonsSection
import com.lavariyalabs.snapy.android.ui.components.FlashcardComponent
import com.lavariyalabs.snapy.android.ui.components.ProgressSection
import com.lavariyalabs.snapy.android.ui.viewmodel.FlashcardViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight

/**
 * FlashcardScreen - Main screen composable
 *
 * STRUCTURE:
 * 1. Progress bar (top)
 * 2. Flashcard (center, main focus)
 * 3. Answer buttons (bottom)
 *
 * NEW STATES:
 * 1. Loading - show spinner while fetching data
 * 2. Error - show error message if fetch fails
 * 3. Success - show flashcards normally
 * 4. Empty - show message if no cards available
 */
@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel = viewModel()
) {
    // ========== LOAD DATA ON FIRST LAUNCH ==========
    /**
     * LaunchedEffect runs once when screen loads
     * Triggers data fetch from Supabase
     */
    LaunchedEffect(Unit) {
        viewModel.loadFlashcards()  // Fetch all flashcards from database
    }

    // ========== OBSERVE STATE FROM VIEWMODEL ==========
    /**
     * .value = Read the current value from State<T>
     * Composable automatically recomposes when value changes
     */
    val quizSession by viewModel.quizSession
    val isCardFlipped by viewModel.currentCardFlipped
    val currentCard = viewModel.getCurrentCard()
    val isLoading by viewModel.isLoading  // Loading indicator
    val errorMessage by viewModel.errorMessage  // Error message

    // ========== MAIN UI LAYOUT ==========
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) { innerPadding ->
        // ========== LOADING STATE ==========
        /**
         * Show spinner while fetching data
         * User sees feedback that app is working
         */
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = Color(0xFF6366F1)
                    )
                    Text(
                        text = "Loading flashcards...",
                        modifier = Modifier.padding(top = 16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }
            }
            return@Scaffold
        }

        // ========== ERROR STATE ==========
        /**
         * Show error message if fetch failed
         * User knows something went wrong
         */
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "⚠️ Error",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = errorMessage ?: "Unknown error",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    // Retry button
                    Text(
                        text = "Retry",
                        modifier = Modifier
                            .background(
                                color = Color(0xFF6366F1),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.loadFlashcards() }
                            .padding(12.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            return@Scaffold
        }

        // ========== EMPTY STATE ==========
        /**
         * Show message if no flashcards available
         */
        if (quizSession.totalCards == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No flashcards available",
                    fontSize = 16.sp,
                    color = Color(0xFF64748B)
                )
            }
            return@Scaffold
        }

        // ========== SUCCESS STATE - SHOW FLASHCARDS ==========
        /**
         * Normal display with progress, card, buttons
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC)),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress bar
            ProgressSection(
                currentCard = quizSession.currentCardIndex + 1,
                totalCards = quizSession.totalCards,
                progressPercent = quizSession.progressPercent
            )

            // Flashcard
            if (currentCard != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FlashcardComponent(
                        isFlipped = isCardFlipped,
                        question = currentCard.question,
                        answer = currentCard.answer,
                        onCardClick = { viewModel.toggleCardFlip() },
                        cardIndex = quizSession.currentCardIndex
                    )
                }
            }

            // Answer buttons
            AnswerButtonsSection(
                onDidntKnow = { viewModel.recordAnswer(isCorrect = false) },
                onKnew = { viewModel.recordAnswer(isCorrect = true) }
            )
        }
    }
}
