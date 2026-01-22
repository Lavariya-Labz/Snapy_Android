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
import com.lavariyalabs.snapy.android.ui.components.*
import com.lavariyalabs.snapy.android.ui.viewmodel.FlashcardViewModel
import com.lavariyalabs.snapy.android.ui.components.MCQFlashcardComponent
import com.lavariyalabs.snapy.android.ui.components.MCQAnswerButtonsSection
import com.lavariyalabs.snapy.android.utils.SoundManager

/**
 * FlashcardStudyScreen - Main study interface
 *
 * Supports both SELF_EVAL and MCQ flashcards
 * Integrates with Supabase data
 */
@Composable
fun FlashcardStudyScreen(
    unitId: Long,
    onNavigateBack: () -> Unit,
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
                    .clickable {
                        onNavigateBack()
                        SoundManager.playClickSound()
                    }
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

                    // Show difficulty buttons only after card is flipped
                    if (isCardFlipped) {
                        DifficultyButtonsSection(
                            onDifficulty = { difficulty ->
                                val knew = difficulty != "FAILED"
                                viewModel.recordSelfEvalAnswer(knew, difficulty)
                                viewModel.goToNextCard()
                            }
                        )
                    } else {
                        // Show flip instruction
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tap card to reveal answer",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
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
                                .clickable { 
                                    SoundManager.playClickSound()
                                    viewModel.goToNextCardMCQ() 
                                }
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

/**
 * DifficultyButtonsSection - Difficulty rating for SELF_EVAL cards
 *
 * @param onDifficulty Callback with difficulty: "FAILED", "HARD", "MEDIUM", or "EASY"
 */
@Composable
private fun DifficultyButtonsSection(
    onDifficulty: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "How well did you know this?",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Failed button
        DifficultyButton(
            text = "Didn't Know",
            color = Color(0xFFEF4444),
            description = "Show sooner"
        ) {
            SoundManager.playClickSound()
            onDifficulty("FAILED")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Hard button
            DifficultyButton(
                text = "Hard",
                color = Color(0xFFF59E0B),
                description = "< 1 day",
                modifier = Modifier.weight(1f)
            ) {
                SoundManager.playClickSound()
                onDifficulty("HARD")
            }

            // Medium button
            DifficultyButton(
                text = "Medium",
                color = Color(0xFF3B82F6),
                description = "1-6 days",
                modifier = Modifier.weight(1f)
            ) {
                SoundManager.playClickSound()
                onDifficulty("MEDIUM")
            }

            // Easy button
            DifficultyButton(
                text = "Easy",
                color = Color(0xFF10B981),
                description = "> 6 days",
                modifier = Modifier.weight(1f)
            ) {
                SoundManager.playClickSound()
                onDifficulty("EASY")
            }
        }
    }
}

/**
 * DifficultyButton - Individual difficulty button
 */
@Composable
private fun DifficultyButton(
    text: String,
    color: Color,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
