package com.example.covidlens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.covidlens.ui.screen.CompareScreen
import com.example.covidlens.ui.screen.CountryDetailScreen
import com.example.covidlens.ui.screen.SearchScreen
import com.example.covidlens.ui.theme.CovidLensTheme
import com.example.covidlens.ui.viewmodel.MainViewModel
import com.example.covidlens.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CovidLensTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(
                vm = viewModel,
                navigateToDetail = { navController.navigate("detail") },
                navigateToCompare = { navController.navigate("compare") }
            )
        }
        composable("detail") {
            CountryDetailScreen(vm = viewModel)
        }
        composable("compare") {
            CompareScreen(vm = viewModel)
        }
    }
}
