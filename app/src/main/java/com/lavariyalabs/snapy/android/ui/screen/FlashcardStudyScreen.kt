//package com.lavariyalabs.snapy.android.ui.screen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.lavariyalabs.snapy.android.navigation.NavRoutes
//import com.lavariyalabs.snapy.android.ui.components.AnswerButtonsSection
//import com.lavariyalabs.snapy.android.ui.components.FlashcardComponent
//import com.lavariyalabs.snapy.android.ui.components.ProgressSection
//import com.lavariyalabs.snapy.android.ui.viewmodel.FlashcardViewModel
//
///**
// * FlashcardStudyScreen - Main study interface with your flashcard component
// *
// * This integrates your existing FlashcardComponent
// * Wraps it with navigation functionality
// */
//@Composable
//fun FlashcardStudyScreen(
//    navController: NavController,
//    unitId: Long,
//    viewModel: FlashcardViewModel = viewModel()
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF8FAFC))
//    ) {
//        // ========== HEADER WITH BACK BUTTON ==========
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(
//                    color = Color(0xFF6366F1),
//                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
//                        bottomStart = 24.dp,
//                        bottomEnd = 24.dp
//                    )
//                )
//                .padding(16.dp),
//            //contentAlignment = Alignment.SpaceBetween
//        ) {
//            Text(
//                text = "← Back",
//                fontSize = 16.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = Color.White,
//                modifier = Modifier.clickable {
//                    navController.popBackStack()
//                }
//            )
//
//            Text(
//                text = "Unit $unitId",
//                fontSize = 16.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = Color.White
//            )
//
//            Text("", fontSize = 16.sp)  // Empty space for alignment
//        }
//
//        // ========== EXISTING FLASHCARD COMPONENTS ==========
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)),
//            verticalArrangement = Arrangement.SpaceBetween,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Progress bar (your existing component)
//            val quizSession by viewModel.quizSession
//            val isCardFlipped by viewModel.currentCardFlipped
//            val currentCard = viewModel.getCurrentCard()
//
//            ProgressSection(
//                currentCard = quizSession.currentCardIndex + 1,
//                totalCards = quizSession.totalCards,
//                progressPercent = quizSession.progressPercent
//            )
//
//            // Flashcard (your existing component)
//            if (currentCard != null) {
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxWidth()
//                        .padding(horizontal = 8.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    FlashcardComponent(
//                        isFlipped = isCardFlipped,
//                        question = currentCard.question,
//                        answer = currentCard.answer,
//                        onCardClick = { viewModel.toggleCardFlip() },
//                        cardIndex = quizSession.currentCardIndex
//                    )
//                }
//            }
//
//            // Answer buttons (your existing component)
//            AnswerButtonsSection(
//                onDidntKnow = { viewModel.recordAnswer(isCorrect = false) },
//                onKnew = { viewModel.recordAnswer(isCorrect = true) }
//            )
//        }
//    }
//}
