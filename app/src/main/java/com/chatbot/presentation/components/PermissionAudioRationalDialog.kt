package com.chatbot.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun PermissionAudioRationalDialog(onOpenSettings: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Microphone Access Request",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(lineHeight = 32.sp),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "To use the microphone you need to grant access.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 24.dp),
                    style = TextStyle(lineHeight = 24.sp),
                    color = Color.Black
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    // .padding(top = 0.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel", style = TextStyle(color = Color.Gray), fontSize = 16.sp)
                    }
                    TextButton(
                        onClick = {
                            onDismiss()
                            onOpenSettings()
                        }
                    ) {
                        Text("OK", style = TextStyle(color = Color.Black), fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
