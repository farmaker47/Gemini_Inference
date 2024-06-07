package com.chatbot.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatbot.R
import com.chatbot.domain.ChatMessage
import com.chatbot.presentation.base.LoadingConfig
import com.chatbot.presentation.base.ScreenWithLoadingIndicator
import com.chatbot.presentation.base.TopAppBarConfig
import com.chatbot.presentation.components.AppBar
import com.chatbot.presentation.components.StyledBasicTextField
import com.chatbot.presentation.utils.ObserveAsEvents
import com.chatbot.presentation.utils.UiText
import kotlinx.coroutines.launch
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
internal fun ChatRoot(
    paddingValues: PaddingValues,
    useExistingChat: Boolean = false,
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = Unit) {
        viewModel.initialize(useExistingChat)
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ChatEvent.Error -> TODO()
            is ChatEvent.OnMessageReceived -> TODO()
            ChatEvent.OnSendMessage -> TODO()
        }
    }

    ScreenWithLoadingIndicator(
        topAppBarConfig = TopAppBarConfig(
            title = UiText.StringResource(if (useExistingChat) R.string.existing_chat else R.string.new_chat).asString(),
            onBackPress = { onNavigateUp() }
        ),
        // if set to critical content, blocks back button while loader is spinning (useful for scenarios like spinning during checkout process)
        loadingConfig = LoadingConfig(viewModel.state.isLoading, criticalContent = true),
        paddingValues = paddingValues
    ) {
        ChatScreen(
            viewModel.state,
            viewModel::onAction
        )
    }
}

private const val s = "textState"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatState,
    onAction: (ChatAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showButton by rememberSaveable { mutableStateOf(false) }
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    // Monitor changes in messages to handle scroll behavior
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val preLastItemIndex = state.messages.size - 2 // Get the index of the last item before the new one is added
            val lastVisibleItem = state.messages.size - 1 // Get the index of the last item

            if (lastVisibleItemIndex == preLastItemIndex) {
                // If the last item before the new one was visible, scroll to the new bottom
                listState.animateScrollToItem(state.messages.size - 1)
            }

            showButton = (lastVisibleItemIndex != preLastItemIndex && lastVisibleItemIndex != lastVisibleItem)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Adjust padding when the keyboard is visible
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumnScrollbar(
                state = listState,
                settings = ScrollbarSettings.Default,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        state = listState
                    ) {
                        items(
                            items = state.messages,
                            key = { it.id }
                        ) {
                            ChatItem(it)
                        }
                    }

                    if (showButton) {
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = stringResource(R.string.action_microphone),
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    MaterialTheme.colorScheme.inversePrimary,
                                    CircleShape
                                )
                                .clip(CircleShape)
                                .clickable {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(state.messages.size - 1)
                                        showButton = false
                                    }
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(4.dp)
                    .padding(8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = stringResource(R.string.action_microphone),
                    modifier = Modifier
                        .background(
                            Color.Transparent,
                            CircleShape
                        )
                        .clip(CircleShape)
                        .clickable {
                            onAction(ChatAction.OnMicPressed)
                        }
                        .padding(8.dp)
                )

                StyledBasicTextField(
                    textState = textState,
                    onTextChange = { textState = it },
                    underlineColor = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.action_send),
                    modifier = Modifier
                        .background(
                            Color.Transparent,
                            CircleShape
                        )
                        .clip(CircleShape)
                        .clickable {
                            // send the text
                            onAction(ChatAction.OnSendMessage(textState.text))
                            // and clear the text field
                            textState = textState.copy(
                                text = ""
                            )
                            // finally, dismiss the keyboard
                            keyboardController?.hide()
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun ChatItem(
    chatMessage: ChatMessage
) {
    val backgroundColor = if (chatMessage.isFromUser) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val bubbleShape = if (chatMessage.isFromUser) {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    }

    val horizontalAlignment = if (chatMessage.isFromUser) {
        Alignment.End
    } else {
        Alignment.Start
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        val author = if (chatMessage.isFromUser) {
            stringResource(R.string.user_label)
        } else {
            stringResource(R.string.model_label)
        }
        Text(
            text = author,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row {
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                ) {
                    if (chatMessage.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        Text(
                            text = chatMessage.text,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        state = ChatState(),
        onAction = {}
    )
}