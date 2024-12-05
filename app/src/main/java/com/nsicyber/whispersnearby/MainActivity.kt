package com.nsicyber.whispersnearby

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nsicyber.whispersnearby.presentation.chatScreen.ChatScreen
import com.nsicyber.whispersnearby.presentation.mainScreen.MainScreen
import com.nsicyber.whispersnearby.utils.Constants
import com.nsicyber.whispersnearby.utils.LocationTracker
import com.nsicyber.whispersnearby.utils.checkAndRequestLocationPermission
import dagger.hilt.android.AndroidEntryPoint


@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Constants.MAIN_SCREEN,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    },
) {
    val locationTracker = remember { LocationTracker(context) }
    val location = remember { mutableStateOf<Location?>(null) }


    DisposableEffect(Unit) {
        locationTracker.startLocationUpdates(
            onLocationUpdated = { newLocation ->
                location.value = newLocation
            },
            onGpsSignalLost = {
                location.value = null
            }
        )
        onDispose {
            locationTracker.stopLocationUpdates()
        }
    }

    Scaffold() { innerPadding ->
        if (location.value == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "GPS signal lost. Please ensure your location services are enabled.",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

        }
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(innerPadding),
        ) {


            composable(route = Constants.MAIN_SCREEN) {
                MainScreen(location = location.value,
                    onSecretRoom = {
                        navActions.navigateToSecretChat(it)
                    },
                    onGlobalRoom = { navActions.navigateToGlobalChat() }
                )
            }

            composable(route = Constants.GLOBAL_CHAT) {
                ChatScreen(
                    location = location.value, onBackClick = { navActions.popBackStack() }
                )
            }

            composable(
                route = "${Constants.SECRET_CHAT}/{secretCode}",
                arguments = listOf(navArgument("secretCode") {
                    type = NavType.StringType
                })
            ) {
                ChatScreen(
                    location = location.value,
                    secretCode = it.arguments?.getString("secretCode"),
                    onBackClick = { navActions.popBackStack() }
                )

            }


        }
    }


}

fun NavOptionsBuilder.popUpToTop(navController: NavController, inclusive: Boolean = false) {
    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
        this.inclusive = inclusive
    }
}

class NavigationActions(private val navController: NavHostController) {

    fun navigateToGlobalChat() {
        navController.navigate(Constants.GLOBAL_CHAT) {
            popUpToTop(navController)
        }
    }


    fun navigateToSecretChat(secretCode: String) {
        navController.navigate(
            "${Constants.SECRET_CHAT}/${secretCode}"
        ) {
            popUpToTop(navController)
        }
    }


    fun popBackStack() {
        navController.popBackStack()
    }


}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val context = this
            var hasPermission by remember { mutableStateOf(false) }
            val navController = rememberNavController()
            checkAndRequestLocationPermission(
                activity = context,
                onPermissionGranted = { hasPermission = true },
                onPermissionDenied = { /* Permission denied */ }
            )
            NavigationGraph()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}

