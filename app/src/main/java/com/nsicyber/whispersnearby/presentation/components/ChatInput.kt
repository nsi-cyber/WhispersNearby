package com.nsicyber.whispersnearby.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.nsicyber.whispersnearby.utils.DeviceColorProvider
import com.nsicyber.whispersnearby.utils.getContrastingTextColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    maxChar: Int, holder: String,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val deviceColor = DeviceColorProvider.getDeviceColor(context)
    val textColor = Color(getContrastingTextColor(deviceColor))

    var text by remember { mutableStateOf("") }
    val isTextNotEmpty = text.isNotEmpty()

    val textFieldWeight by animateFloatAsState(targetValue = if (isTextNotEmpty) 0.85f else 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(99.dp))
            .border(BorderStroke(1.dp, Color(deviceColor)), RoundedCornerShape(99.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TextField tamamen transparan
            TextField(
                value = text,
                onValueChange = {
                    if (text.length <= maxChar)
                        text = it
                },
                modifier = Modifier
                    .weight(textFieldWeight)
                    .background(Color.Transparent) // TextField'ın arkaplanı transparan
                    .padding(horizontal = 8.dp),
                placeholder = {
                    Text(
                        holder,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                },
                textStyle = TextStyle(color = Color.Black), // Yazı rengi
                colors = TextFieldDefaults.colors(
                ).copy(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            AnimatedVisibility(
                visible = text.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(deviceColor))
                        .vibrateClickable {
                            if (isTextNotEmpty) {
                                onSend(text)
                                text = ""
                            }
                        }
                        .padding(8.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = rememberVectorPainter(Icons.Default.Send),
                        contentDescription = "",
                        tint = textColor
                    )
                }
            }
        }


    }
}