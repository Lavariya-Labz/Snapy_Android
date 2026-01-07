package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lavariyalabs.snapy.android.ui.components.*
import com.lavariyalabs.snapy.android.ui.viewmodel.FlashcardViewModel
import com.lavariyalabs.snapy.android.ui.components.MCQFlashcardComponent
import com.lavariyalabs.snapy.android.ui.components.MCQAnswerButtonsSection


/**
 * FlashcardStudyScreen - Main study interface
 *
 * Supports both SELF_EVAL and MCQ flashcards
 * Integrates with Supabase data
 */
@Composable
fun FlashcardStudyScreen(
    navController: NavController,
    unitId: Long,
    viewModel: FlashcardViewModel
) {
    // Load flashcards from Supabase
    LaunchedEffect(unitId) {
        viewModel.loadFlashcardsByUnit(unitId)
    }

    val quizSession by viewModel.quizSession
    val isCardFlipped by viewModel.currentCardFlipped
    val isAnswered by viewModel.isAnswered
    val selectedOptionLetter by viewModel.selectedOptionLetter
    val currentCard = viewModel.getCurrentCard()
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // LOADING STATE
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading cards...")
        }
        return
    }

    // ERROR STATE
    if (errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            Text("Error: $errorMessage")
        }
        return
    }

    // MAIN CONTENT
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF6366F1),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "← Back",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "Unit $unitId",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Content
        if (currentCard != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProgressSection(
                    currentCard = quizSession.currentCardIndex + 1,
                    totalCards = quizSession.totalCards,
                    progressPercent = quizSession.progressPercent
                )

                if (currentCard.type == "SELF_EVAL") {
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
                            answer = currentCard.correctAnswer ?: "No answer",
                            onCardClick = { viewModel.toggleCardFlip() },
                            cardIndex = quizSession.currentCardIndex
                        )
                    }

                    AnswerButtonsSection(
                        onDidntKnow = {
                            viewModel.recordAnswer(isCorrect = false)
                            viewModel.goToNextCard()
                        },
                        onKnew = {
                            viewModel.recordAnswer(isCorrect = true)
                            viewModel.goToNextCard()
                        }
                    )
                } else if (currentCard.type == "MCQ") {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MCQFlashcardComponent(
                            flashcard = currentCard,
                            cardIndex = quizSession.currentCardIndex,
                            isAnswered = isAnswered,
                            selectedOptionLetter = selectedOptionLetter
                        )
                    }

                    MCQAnswerButtonsSection(
                        options = currentCard.quizOptions,
                        isAnswered = isAnswered,
                        onOptionSelected = { option ->
                            viewModel.submitMCQAnswer(
                                optionLetter = option.optionLetter,
                                isCorrect = option.isCorrect
                            )
                        }
                    )

                    if (isAnswered) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.goToNextCardMCQ() }
                                .background(Color.White)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Next →",
                                color = Color(0xFF6366F1),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
