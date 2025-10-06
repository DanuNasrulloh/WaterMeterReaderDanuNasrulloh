package com.flindigital.watermeter

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.flindigital.watermeter.ui.navigation.NavGraph

@Composable
fun MyApp() {
    val navController = rememberNavController()
    Surface { NavGraph(navController) }
}