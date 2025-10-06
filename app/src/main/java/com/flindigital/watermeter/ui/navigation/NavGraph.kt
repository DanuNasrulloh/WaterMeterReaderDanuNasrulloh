package com.flindigital.watermeter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flindigital.watermeter.pages.detail.DetailScreen
import com.flindigital.watermeter.pages.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onNavigate = { navController.navigate(Routes.DETAIL) })
        }
        composable(Routes.DETAIL) {
            DetailScreen()
        }
    }
}