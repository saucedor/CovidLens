package com.example.covidlens.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.covidlens.ui.screen.CompareScreen
import com.example.covidlens.ui.screen.CountryDetailScreen
import com.example.covidlens.ui.screen.SearchScreen
import com.example.covidlens.ui.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    vm: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        // SEARCH SCREEN
        composable("search") {
            SearchScreen(
                vm = vm,
                onCountryClick = { country ->
                    navController.navigate("detail/$country")
                },
                onCompareClick = {
                    navController.navigate("compare")
                }
            )
        }

        // DETAIL SCREEN (con parámetro de país)
        composable(
            route = "detail/{country}",
            arguments = listOf(
                navArgument("country") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val country = backStackEntry.arguments?.getString("country") ?: "Unknown"
            CountryDetailScreen(
                country = country,
                vm = vm,
                navigateBack = { navController.popBackStack() }
            )
        }

        // COMPARE SCREEN
        composable("compare") {
            CompareScreen(
                vm = vm,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}