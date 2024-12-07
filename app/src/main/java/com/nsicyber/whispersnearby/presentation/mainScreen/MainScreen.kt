package com.nsicyber.whispersnearby.presentation.mainScreen

import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nsicyber.whispersnearby.R
import com.nsicyber.whispersnearby.presentation.components.BaseView
import com.nsicyber.whispersnearby.presentation.components.ChatInput
import com.nsicyber.whispersnearby.presentation.components.FilledButton
import com.nsicyber.whispersnearby.presentation.components.NormalButton
import kotlinx.coroutines.launch


enum class BottomSheetType {
    LOCATION_PERMISSION_GLOBAL,
    CAMERA_PERMISSION_GLOBAL,
    LOCATION_PERMISSION_SECRET,
    CAMERA_PERMISSION_SECRET,
    SECRET_CODE,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    hasLocationPermission: Boolean, hasCameraPermission: Boolean,
    location: Location?,
    onSecretRoom: (secretCode: String, hasCamera: Boolean) -> Unit,
    onGlobalRoom: (hasCamera: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val bottomSheetState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        ),
    )

    val bottomSheetType = remember { mutableStateOf(BottomSheetType.SECRET_CODE) }


    BaseView(
        bottomSheetDismiss = {
            scope.launch {
                bottomSheetState.bottomSheetState.hide()
            }
        }, bottomSheetState = bottomSheetState, bottomSheetContent =
        {
            when (bottomSheetType.value) {
                BottomSheetType.SECRET_CODE -> {
                    Column {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            text = "Enter you secret code",
                            style = TextStyle().copy(
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                fontSize = 22.sp
                            )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ChatInput(
                                maxChar = 10,
                                holder = "Enter secret code",
                                onSend = { content ->

                                    scope.launch {
                                        bottomSheetState.bottomSheetState.hide()
                                    }
                                    if (content.isBlank())
                                        Toast.makeText(
                                            context,
                                            "You need to enter Secret Code",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    else
                                        onSecretRoom(content, hasCameraPermission)

                                })

                        }
                    }

                }


                BottomSheetType.LOCATION_PERMISSION_GLOBAL -> Column {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = "You cant enter any room without active GPS connection, Please check your GPS permission and be sure GPS is open on your device.",
                        style = TextStyle().copy(
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontSize = 22.sp
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NormalButton(text = "I understand") {
                            scope.launch { bottomSheetState.bottomSheetState.hide() }
                        }

                    }
                }

                BottomSheetType.CAMERA_PERMISSION_GLOBAL -> Column {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = "To fully use features, please give camera permission.",
                        style = TextStyle().copy(
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontSize = 22.sp
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NormalButton(text = "Continue without Camera") {
                            scope.launch { bottomSheetState.bottomSheetState.hide() }
                            onGlobalRoom(hasCameraPermission)

                        }

                    }
                }

                BottomSheetType.LOCATION_PERMISSION_SECRET -> Column {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = "You cant enter any room without active GPS connection, Please check your GPS permission and be sure GPS is open on your device.",
                        style = TextStyle().copy(
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontSize = 22.sp
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NormalButton(text = "I understand") {
                            scope.launch { bottomSheetState.bottomSheetState.hide() }
                        }

                    }
                }

                BottomSheetType.CAMERA_PERMISSION_SECRET -> Column {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = "To fully use features, please give camera permission.",
                        style = TextStyle().copy(
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontSize = 22.sp
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NormalButton(text = "Continue without Camera") {
                            bottomSheetType.value = BottomSheetType.SECRET_CODE
                        }

                    }
                }
            }

        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                Column {
                    Text(
                        text = "There are whispers everywhere...",
                        style = TextStyle().copy(fontSize = 32.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Why dont you join to them?",
                        style = TextStyle().copy(fontSize = 22.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    FilledButton(text = "Enter Global Chat", onClick = {
                        if (hasLocationPermission == false) {
                            bottomSheetType.value = BottomSheetType.LOCATION_PERMISSION_GLOBAL
                            scope.launch {
                                bottomSheetState.bottomSheetState.expand()
                            }
                        } else if (hasCameraPermission == false) {
                            bottomSheetType.value = BottomSheetType.CAMERA_PERMISSION_GLOBAL
                            scope.launch {
                                bottomSheetState.bottomSheetState.expand()
                            }
                        } else if (location == null) {
                            bottomSheetType.value = BottomSheetType.LOCATION_PERMISSION_GLOBAL
                            scope.launch {
                                bottomSheetState.bottomSheetState.expand()
                            }
                        } else
                            onGlobalRoom(hasCameraPermission)
                    })



                    Spacer(modifier = Modifier.height(16.dp))
                    NormalButton(text = "Enter Secret Room", onClick = {
                        if (hasLocationPermission == false) {
                            bottomSheetType.value = BottomSheetType.LOCATION_PERMISSION_SECRET
                            scope.launch {
                                bottomSheetState.bottomSheetState.expand()
                            }
                        } else if (hasCameraPermission == false) {
                            bottomSheetType.value = BottomSheetType.CAMERA_PERMISSION_SECRET
                            scope.launch {
                                bottomSheetState.bottomSheetState.expand()
                            }
                        } else if (location == null) {
                            bottomSheetType.value = BottomSheetType.LOCATION_PERMISSION_SECRET
                            scope.launch {
                                bottomSheetState.bottomSheetState.expand()
                            }
                        } else {
                            bottomSheetType.value = BottomSheetType.SECRET_CODE

                            scope.launch {
                                bottomSheetState.bottomSheetState.expand()
                            }
                        }

                    })
                }


                Image(
                    painter = painterResource(R.drawable.ic_logo_main),
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }, topBarEnable = false
    )

}

