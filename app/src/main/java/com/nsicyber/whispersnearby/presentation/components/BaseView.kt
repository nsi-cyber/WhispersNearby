package com.nsicyber.whispersnearby.presentation.components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.ViewModel
import com.nsicyber.whispersnearby.data.remote.ChatMessage
import com.nsicyber.whispersnearby.ui.theme.AlphaBlack
import com.nsicyber.whispersnearby.utils.MessageEncryptor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class PopupEvent(
    val message: String,
    val action: PopupAction? = null
)

data class PopupAction(
    val name: String,
    val action: suspend () -> Unit
)

object PopupController {

    private val _events = Channel<PopupEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: PopupEvent) {
        _events.send(event)
    }
}


open class BaseViewModel : ViewModel() {

    private val _popupResultState = MutableStateFlow<PopupResult?>(null)
    val popupResultState: StateFlow<PopupResult?> = _popupResultState.asStateFlow()

    fun showPopup(result: PopupResult) {
        _popupResultState.value = result
    }

    fun resetPopupResult() {
        _popupResultState.value = null
    }

    suspend fun sendPopupEvent(event: PopupEvent) {
        PopupController.sendEvent(event)
    }
}

data class PopupResult(val message: String, val actionTaken: Boolean)


@Composable
fun Modifier.vibrateClickable(
    isEnable: Boolean = true,
    onClickLabel: String? = null,
    onClick: () -> Unit
): Modifier {
    val context = LocalContext.current
    val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    return this
        .clickable(enabled = isEnable, onClickLabel = onClickLabel) {

            vibrator.vibrate(VibrationEffect.createOneShot(20L, VibrationEffect.DEFAULT_AMPLITUDE))
            onClick()
        }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseView(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
    isPageLoading: Boolean = false,
    isDialogShow: Boolean = false,
    viewModel: BaseViewModel = BaseViewModel(),
    topBarEnable: Boolean = false,
    topBarText: String = "",
    dialogMessage: ChatMessage? = null,
    topBarFilled: Boolean = false,
    topBarBackButtonEnable: Boolean = false,
    onBackClicked: () -> Unit = {},
    dialogPositive: (id: String) -> Unit = {},
    dialogNegative: () -> Unit = {},
    bottomSheetContent: @Composable () -> Unit = {},
    bottomSheetState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        ),
    ),
    bottomSheetDismiss: () -> Unit = {},

    ) {
    val popupResult by viewModel.popupResultState.collectAsState()
    var currentPopupEvent by remember { mutableStateOf<PopupEvent?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(bottomSheetState.bottomSheetState.currentValue) {
        if (bottomSheetState.bottomSheetState.targetValue == SheetValue.Expanded) {
            keyboardController?.hide()
        }
    }

    BottomSheetScaffold(
        modifier = Modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent()
                    if (bottomSheetState.bottomSheetState.targetValue == SheetValue.Expanded) {
                        bottomSheetDismiss()
                    }
                }
            }


        },

        scaffoldState = bottomSheetState,
        sheetContent = {
            bottomSheetContent()
        }, sheetPeekHeight = 0.dp, content = {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp)) {
                Box(modifier = modifier) {
                    content()
                    if (topBarEnable) {
                        Row(
                            Modifier
                                .background(AlphaBlack)
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (topBarBackButtonEnable) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(99.dp))
                                        .vibrateClickable { onBackClicked() }
                                ) {
                                    Image(
                                        modifier = Modifier.align(Alignment.Center),
                                        painter = rememberVectorPainter(Icons.Default.ArrowBack),
                                        contentDescription = "",
                                        colorFilter = ColorFilter.tint(Color.Black)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(color = Color.Black)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = topBarText,
                                    style = TextStyle().copy(
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                )
                            }
                            Spacer(
                                modifier = Modifier
                                    .size(28.dp)
                            )

                        }
                    }
                    ReportDialog(
                        message = dialogMessage,
                        showDialog = isDialogShow,
                        onConfirm = { dialogPositive(it) },
                        onDismiss = dialogNegative,
                    )
                    PopupHost(
                        popupEvent = currentPopupEvent,
                        onDismiss = { currentPopupEvent = null },
                        viewModel = viewModel
                    )
                    popupResult?.let { result ->
                        if (result.actionTaken) {
                            println("Popup tamamlandı: ${result.message}")
                            viewModel.resetPopupResult() // Popup sonucunu sıfırla
                        }
                    }
                    if (isPageLoading) {
                        //   DinoLoadingScreen()
                    }

                }


                Box(
                    Modifier
                        .fillMaxSize()
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 200,
                                easing = FastOutSlowInEasing
                            )
                        )
                ) {

                    if (bottomSheetState.bottomSheetState.targetValue == SheetValue.Expanded) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Gray)
                        )
                    }
                }

            }

        })
}


@Composable
fun ReportDialog(
    message: ChatMessage?,
    showDialog: Boolean = true,
    onConfirm: (id: String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = "Report this message?")
            },
            text = {
                Text(
                    text = MessageEncryptor.decryptMessage(message?.content ?: "")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(message?.id ?: "")
                    }
                ) {
                    Text("Report")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun PopupHost(
    modifier: Modifier = Modifier,
    popupEvent: PopupEvent?,
    onDismiss: () -> Unit,
    viewModel: BaseViewModel
) {
    val scope = rememberCoroutineScope()
    if (popupEvent != null) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = { onDismiss() }
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = popupEvent.message, color = Color.Black)

                    popupEvent.action?.let { action ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            scope.launch {
                                viewModel.showPopup(PopupResult(popupEvent.message, true))
                                action.action()
                            }
                        }) {
                            Text(text = action.name)
                        }
                    }
                }
            }
        }
    }
}