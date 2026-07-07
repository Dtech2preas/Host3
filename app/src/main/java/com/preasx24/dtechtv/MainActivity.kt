package com.preasx24.dtechtv

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.preasx24.dtechtv.core.preferences.AppPreferences
import com.preasx24.dtechtv.core.ui.theme.DTechTvTheme
import com.preasx24.dtechtv.mobileui.home.HomeScreen
import com.preasx24.dtechtv.mobileui.onboarding.OnboardingScreen
import com.preasx24.dtechtv.mobileui.player.PlayerScreen
import com.preasx24.dtechtv.mobileui.logs.LogConsoleScreen
import com.preasx24.dtechtv.tvui.TvHomeScreen
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DTechTvTheme {
                val context = LocalContext.current
                val isTv = isAndroidTv(context)
                val isOnboardingCompleted by appPreferences.isOnboardingCompleted.collectAsState(initial = null)

                if (isTv) {
                    TvAppNavigation()
                } else {
                    if (isOnboardingCompleted == null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        MobileAppNavigation(startDestination = if (isOnboardingCompleted == true) "home" else "onboarding")
                    }
                }
            }
        }
    }

    private fun isAndroidTv(context: Context): Boolean {
        val uiModeManager = context.getSystemService(UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }
}

@Composable
fun MobileAppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onChannelSelected = { channel ->
                    val encodedUrl = URLEncoder.encode(channel.streamUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate("player/$encodedUrl")
                },
                onNavigateToLogs = {
                    navController.navigate("logs")
                }
            )
        }
        composable("logs") {
            LogConsoleScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = "player/{streamUrl}",
            arguments = listOf(navArgument("streamUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val streamUrl = backStackEntry.arguments?.getString("streamUrl") ?: return@composable
            PlayerScreen(encodedStreamUrl = streamUrl)
        }
    }
}

@Composable
fun TvAppNavigation() {
    // For MVP, just show the TV Home Screen placeholder
    TvHomeScreen()
}
