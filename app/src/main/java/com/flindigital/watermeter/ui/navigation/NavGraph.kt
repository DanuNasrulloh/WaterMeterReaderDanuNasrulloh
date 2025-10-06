package com.flindigital.watermeter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flindigital.watermeter.pages.detail.DetailScreen
import com.flindigital.watermeter.pages.customers.CustomerListScreen
import com.flindigital.watermeter.pages.camera.CameraScreen

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail"
    const val CAMERA = "camera"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            CustomerListScreen(onNavigateCamera = { navController.navigate(Routes.CAMERA) })
        }
        composable(Routes.DETAIL) {
            DetailScreen()
        }
        composable(Routes.CAMERA) {
            CameraScreen()
        }
    }
}