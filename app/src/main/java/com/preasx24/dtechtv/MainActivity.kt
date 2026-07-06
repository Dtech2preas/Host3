package com.preasx24.dtechtv

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.preasx24.dtechtv.core.ui.theme.DTechTvTheme
import com.preasx24.dtechtv.mobileui.home.HomeScreen
import com.preasx24.dtechtv.mobileui.player.PlayerScreen
import com.preasx24.dtechtv.tvui.TvHomeScreen
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DTechTvTheme {
                val context = LocalContext.current
                val isTv = isAndroidTv(context)

                if (isTv) {
                    TvAppNavigation()
                } else {
                    MobileAppNavigation()
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
fun MobileAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onChannelSelected = { channel ->
                    val encodedUrl = URLEncoder.encode(channel.streamUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate("player/$encodedUrl")

                }
            )
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
