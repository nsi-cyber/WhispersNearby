package com.nsicyber.whispersnearby.presentation.mainScreen

import android.location.Location
import android.widget.Toast
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nsicyber.whispersnearby.presentation.components.BaseView
import com.nsicyber.whispersnearby.presentation.components.ChatInput
import com.nsicyber.whispersnearby.presentation.components.FilledButton
import com.nsicyber.whispersnearby.presentation.components.NormalButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    location: Location?,
    onSecretRoom: (secretCode: String) -> Unit,
    onGlobalRoom: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val bottomSheetState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        ),
    )

    BaseView(
        bottomSheetDismiss = {
            scope.launch {
                bottomSheetState.bottomSheetState.hide()
            }
        }, bottomSheetState = bottomSheetState, bottomSheetContent =
        {

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
                                onSecretRoom(content)

                        })

                }
            }
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FilledButton(text = "Enter Global Chat", onClick = {
                    if (location == null)
                        Toast.makeText(context, "Check your GPS connection!", Toast.LENGTH_SHORT)
                            .show()
                    else
                        onGlobalRoom()
                })



                Spacer(modifier = Modifier.height(16.dp))
                NormalButton(text = "Enter Secret Room", onClick = {
                    if (location == null)
                        Toast.makeText(context, "Check your GPS connection!", Toast.LENGTH_SHORT)
                            .show()
                    else
                        scope.launch {
                            bottomSheetState.bottomSheetState.expand()
                        }
                })

            }
        }, topBarEnable = false
    )

}

