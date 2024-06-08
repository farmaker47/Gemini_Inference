package com.chatbot.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false,
    backgroundColor: Color = Color.Transparent,
    textColor: Color = Color.Black,
    enabled: Boolean = true
) {
    val buttonModifier = modifier
        .fillMaxWidth()
        .padding(8.dp)

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = textColor,
                disabledContentColor = Color.LightGray
            ),
            border = BorderStroke(2.dp, if (enabled) textColor else Color.Gray),
            shape = RoundedCornerShape(24.dp),
            enabled = enabled
        ) {
            Text(text = text, style = TextStyle(color = if (enabled) textColor else Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold))
        }
    } else {
        Button(
            onClick = onClick,
            modifier = buttonModifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = textColor,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.Gray
            ),
            shape = RoundedCornerShape(24.dp),
            enabled = enabled
        ) {
            Text(text = text, style = TextStyle(color = if (enabled) textColor else Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold))
        }
    }
}

@Preview
@Composable
fun AppButtonPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {

            Text(
                text = "Normal Buttons",
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            Text("Enabled / Disabled")
            AppButton(
                text = "Start New Chat",
                onClick = { /*TODO*/ },
                textColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.primary),
                isOutlined = false,
                backgroundColor = MaterialTheme.colorScheme.primary,

                )

            AppButton(
                text = "Start New Chat",
                onClick = { /*TODO*/ },
                textColor = MaterialTheme.colorScheme.primaryContainer,
                isOutlined = false,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                enabled = false
            )


            Text(
                text = "Outlined Buttons",
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(top = 64.dp, bottom = 16.dp)
            )

            Text("Enabled / Disabled")
            AppButton(
                text = "Continue Existing Chat",
                onClick = { /*TODO*/ },
                textColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.primaryContainer),
                isOutlined = true
            )

            AppButton(
                text = "Continue Existing Chat",
                onClick = { /*TODO*/ },
                textColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.primaryContainer),
                isOutlined = true,
                enabled = false
            )
        }
    }
}