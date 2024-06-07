package com.chatbot.presentation.base.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatbot.presentation.base.MyTopAppBar
import com.chatbot.presentation.base.findActivity

@Composable
fun ApplicationScaffold(
    scaffoldViewModel: ScaffoldViewModel = hiltViewModel(LocalContext.current.findActivity()),
    content: @Composable (PaddingValues) -> Unit
) {
    val title by scaffoldViewModel.title.collectAsState()
    val onBackPress by scaffoldViewModel.onBackPress.collectAsState()

    Scaffold(
        topBar = { MyTopAppBar(title, onBackPress) },
    ) { paddingValues ->
        content(paddingValues)
    }
}