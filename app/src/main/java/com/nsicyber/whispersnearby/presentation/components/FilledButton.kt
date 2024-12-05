package com.nsicyber.whispersnearby.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nsicyber.whispersnearby.utils.DeviceColorProvider
import com.nsicyber.whispersnearby.utils.getContrastingTextColor

@Preview
@Composable
fun FilledButton(text: String = "Button", onClick: () -> Unit = {}) {
    val context = LocalContext.current
    val deviceColor = DeviceColorProvider.getDeviceColor(context)
    val textColor = Color(getContrastingTextColor(deviceColor))



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .vibrateClickable { onClick() }
            .background(color = Color(deviceColor))
            .border(BorderStroke(1.dp, textColor), RoundedCornerShape(12.dp))
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, style = TextStyle(fontSize = 18.sp))
    }


}
