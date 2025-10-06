package com.flindigital.watermeter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flindigital.watermeter.pages.customers.CustomerListScreen
import com.flindigital.watermeter.pages.detail.DetailScreen
import com.flindigital.watermeter.pages.camera.CameraScreen
import com.flindigital.watermeter.pages.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val CAMERA = "camera"
    const val DETAIL = "detail"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onNavigate = { customer ->
                navController.navigate("${Routes.CAMERA}/${customer.userId}")
            })
        }

        composable(
            route = "${Routes.CAMERA}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            CameraScreen(
                userId = userId,
                onCaptured = { full, crop ->
                    navController.navigate(
                        "${Routes.DETAIL}/$userId?full=${full.encode()}&crop=${crop.encode()}"
                    ) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.DETAIL}/{userId}?full={full}&crop={crop}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("full") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("crop") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val full = backStackEntry.arguments?.getString("full")
            val crop = backStackEntry.arguments?.getString("crop")
            DetailScreen(userId = userId, fullPath = full?.decode(), cropPath = crop?.decode())
        }
    }
}

private fun String.encode(): String = java.net.URLEncoder.encode(this, Charsets.UTF_8.name())
private fun String.decode(): String = java.net.URLDecoder.decode(this, Charsets.UTF_8.name())