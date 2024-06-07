package com.chatbot.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StyledBasicTextField(
    textState: TextFieldValue,
    modifier: Modifier = Modifier,
    underlineColor: Color = Color.Blue,
    onTextChange: (TextFieldValue) -> Unit
) {
    BasicTextField(
        value = textState,
        onValueChange = onTextChange,
        textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
        cursorBrush = SolidColor(Color.Blue),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val y: Float = (size.height * 1.5).toFloat()
                        drawLine(
                            color = underlineColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
            ) {
                if (textState.text.isEmpty()) {
                    Text(
                        text = "Put your text here",
                        style = TextStyle(color = Color.Gray, fontSize = 18.sp)
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier
    )
}