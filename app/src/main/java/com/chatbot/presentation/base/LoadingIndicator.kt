package com.chatbot.presentation.base

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

/**
 * A Circular Progress Indicator that blocks background touches
 * and also blocks "back button" press while loading for critical loading operations.
 */
@Composable
fun LoadingIndicator(isLoading: Boolean, isCritical: Boolean) {
    if (isLoading) {
        if (isCritical) {
            BackHandler(enabled = true) { }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
                .pointerInput(Unit) {
                    detectTapGestures { }
                }
        ) {
            CircularProgressIndicator()
        }
    }
}