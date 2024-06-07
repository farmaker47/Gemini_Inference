package com.chatbot.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatbot.R
import com.chatbot.presentation.base.LoadingConfig
import com.chatbot.presentation.base.ScreenWithLoadingIndicator
import com.chatbot.presentation.base.TopAppBarConfig
import com.chatbot.presentation.components.AppButton
import com.chatbot.presentation.utils.UiText

@Composable
fun MainScreenRoot(
    paddingValues: PaddingValues,
    onStartNewChat: () -> Unit,
    onContinueExistingChat: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    ScreenWithLoadingIndicator(
        topAppBarConfig = TopAppBarConfig(title = UiText.StringResource(R.string.app_name).asString()),
        // if set to critical content, blocks back button while loader is spinning (useful for scenarios like spinning during checkout process)
        loadingConfig = LoadingConfig(viewModel.state.isLoading, criticalContent = true),
        paddingValues = paddingValues
    ) {
        MainScreen(
            state = viewModel.state,
            onAction = { action ->
                when (action) {
                    MainViewAction.ContinueExistingChat -> onContinueExistingChat()
                    MainViewAction.StartNewChat -> onStartNewChat()
                }
            }
        )
    }
}

@Composable
fun MainScreen(
    state: MainViewState,
    onAction: (MainViewAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Gemini Inference App",
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            AppButton(
                text = "Start New Chat",
                onClick = { onAction(MainViewAction.StartNewChat) },
                textColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.primary),
                isOutlined = false,
                backgroundColor = MaterialTheme.colorScheme.primary,

            )

            AppButton(
                text = "Continue Existing Chat",
                onClick = { onAction(MainViewAction.ContinueExistingChat) },
                textColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.primary),
                isOutlined = false,
                backgroundColor = MaterialTheme.colorScheme.primary,
                enabled = state.continueChatButtonEnabled
            )

        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(
        state = MainViewState(),
        onAction = {}
    )
}