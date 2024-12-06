package com.nsicyber.whispersnearby.presentation.chatScreen


import android.app.Activity
import android.location.Location
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    activity: Activity,
    location: Location?,
    secretCode: String? = null,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val deviceId = DeviceIdProvider.getDeviceId(activity.applicationContext)
    val deviceColor = DeviceColorProvider.getDeviceColor(activity.applicationContext)
    val lifecycleOwner = LocalLifecycleOwner.current


    val controller = remember {
        LifecycleCameraController(
            activity.applicationContext

        ).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            bindToLifecycle(lifecycleOwner)
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.IMAGE_ANALYSIS,
            )
        }
    }


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
                it, deviceId
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


                    item {
                        AndroidView(
                            modifier = Modifier.alpha(0f).size(1.dp),
                            factory = {
                                PreviewView(it).apply {
                                    this.controller = controller
                                    controller.bindToLifecycle(lifecycleOwner)
                                }
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
                            viewModel.sendMessageWithImage(
                                controller = controller,
                                content = content,
                                latitude = location?.latitude ?: 0.0,
                                longitude = location?.longitude ?: 0.0,
                                deviceId = deviceId,
                                secretCode = secretCode,
                                deviceColor = deviceColor

                            )
                            /*


                                                                                   viewModel.sendMessage(
                                                                                       content = content,
                                                                                       latitude = location?.latitude ?: 0.0,
                                                                                       longitude = location?.longitude ?: 0.0,
                                                                                       deviceId = deviceId,
                                                                                       secretCode = secretCode, deviceColor = deviceColor
                                                                                   )

                                                        */
                        }
                    })
            }
        })
}







