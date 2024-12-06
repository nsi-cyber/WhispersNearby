package com.nsicyber.whispersnearby.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nsicyber.whispersnearby.data.remote.ChatMessage
import com.nsicyber.whispersnearby.utils.MessageEncryptor
import com.nsicyber.whispersnearby.utils.getContrastingTextColor

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: ChatMessage,
    isUser: Boolean,
    onLongPress: (ChatMessage) -> Unit
) {
    val decryptedMessage = MessageEncryptor.decryptMessage(message.content)
    val backgroundColor = Color(message.color)
    val textColor = Color(getContrastingTextColor(message.color))


    Box {
        Text(message.emoji, Modifier.align(if (isUser) Alignment.TopEnd else Alignment.TopStart))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = if (!isUser) Arrangement.Start else Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topEnd = if (!isUser) 20.dp else 0.dp,
                            topStart = if (isUser) 20.dp else 0.dp,
                            bottomEnd = 20.dp,
                            bottomStart = 20.dp
                        )
                    )
                    .background(backgroundColor, RoundedCornerShape(8.dp))
                    .combinedClickable(
                        onClick = { },
                        onLongClick = { onLongPress(message) }
                    )
                    .padding(8.dp)

            ) {
                Text(
                    text = decryptedMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
        }
    }


}
