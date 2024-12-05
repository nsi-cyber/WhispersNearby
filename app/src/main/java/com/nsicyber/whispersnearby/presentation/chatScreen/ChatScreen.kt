package com.nsicyber.whispersnearby.presentation.chatScreen


import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nsicyber.whispersnearby.data.remote.ChatMessage
import com.nsicyber.whispersnearby.presentation.components.BaseView
import com.nsicyber.whispersnearby.presentation.components.ChatBubble
import com.nsicyber.whispersnearby.presentation.components.ChatInput
import com.nsicyber.whispersnearby.utils.Constants
import com.nsicyber.whispersnearby.utils.DeviceColorProvider
import com.nsicyber.whispersnearby.utils.DeviceIdProvider


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    location: Location?,
    secretCode: String? = null,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val deviceId = DeviceIdProvider.getDeviceId(context)

    val dialogState = remember {
        mutableStateOf(false)
    }
    val dialogData = remember {
        mutableStateOf(ChatMessage())
    }


    LaunchedEffect(location) {
        location.let { location ->
            viewModel.loadNearbyMessages(
                latitude = location?.latitude ?: 0.0,
                longitude = location?.longitude ?: 0.0,
                radius = Constants.RADIUS_SIZE,
                secretCode = secretCode
            )
        }
    }


    BaseView(onBackClicked = { onBackClick() }, dialogMessage = dialogData.value,
        isDialogShow = dialogState.value,
        dialogNegative = {
            dialogState.value = false
        },
        dialogPositive = {
            dialogState.value = false

            viewModel.reportMessage(
                it,deviceId
            )
        },
        topBarEnable = true,
        topBarFilled = true,
        topBarText = if (secretCode.isNullOrEmpty()) "Global Chat" else secretCode,
        topBarBackButtonEnable = true,
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    reverseLayout = true
                ) {
                    items(uiState.messages ?: listOf()) { message ->
                        ChatBubble(
                            message = message,
                            isUser = message.deviceId == deviceId,
                            onLongPress = {
                                dialogData.value = it
                                dialogState.value = true
                            }
                        )
                    }
                }

                ChatInput(
                    maxChar = 150,
                    holder = "Type a message...",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onSend = { content ->
                        location.let { location ->
                            val deviceColor = DeviceColorProvider.getDeviceColor(context)

                            viewModel.sendMessage(
                                content = content,
                                latitude = location?.latitude ?: 0.0,
                                longitude = location?.longitude ?: 0.0,
                                deviceId = deviceId,
                                secretCode = secretCode, deviceColor = deviceColor
                            )
                        }
                    })
            }
        })
}







