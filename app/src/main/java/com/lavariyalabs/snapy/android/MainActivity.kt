package com.lavariyalabs.snapy.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.lavariyalabs.snapy.android.ui.theme.Snapy_AndroidTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.lavariyalabs.snapy.android.navigation.NavGraph
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            Snapy_AndroidTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Box(modifier = Modifier
//                        .fillMaxSize()
//                        .padding(innerPadding)
//                    ) {
//                        LottieLoadingAnimation()
//                    }
//                }
//            }
//        }
//    }
//}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Snapy_AndroidTheme {
                SnapApp()
            }
        }
    }
}

@Composable
fun LottieLoadingAnimation(modifier: Modifier = Modifier) {
    // 1. Load the Lottie composition from the raw resource
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading_animation)
    )

    // 2. Control the animation playback (e.g., infinite loop, auto-play)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever, // Loop infinitely
        isPlaying = true // Start playing automatically
    )

    // 3. Display the animation
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(200.dp)
    )
}

/**
 * SnapApp - Main app container
 *
 * Sets up navigation and shared state
 */

@Composable
fun SnapApp() {
    // Navigation controller
    val navController = rememberNavController()

    // Shared app state (available to all screens)
    val appStateViewModel: AppStateViewModel = viewModel()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // Apply the padding to a container wrapping your content
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                appStateViewModel = appStateViewModel
            )
        }
    }
}

