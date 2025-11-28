package com.example.covidlens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.covidlens.data.prefs.UserPrefsDataStore
import com.example.covidlens.data.remote.ApiKeyProvider
import com.example.covidlens.data.remote.CovidApi
import com.example.covidlens.data.repo.CovidRepositoryImpl
import com.example.covidlens.data.repo.UserPreferencesRepositoryImpl
import com.example.covidlens.ui.navigation.AppNavigation
import com.example.covidlens.ui.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Creación de dependencias ---

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Api-Key", ApiKeyProvider().getKey())
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val covidApi = retrofit.create(CovidApi::class.java)
        val covidRepo = CovidRepositoryImpl(covidApi)
        val prefsDataStore = UserPrefsDataStore(applicationContext)
        val prefsRepo = UserPreferencesRepositoryImpl(prefsDataStore)

        // --- Fin de la creación de dependencias ---

        setContent {
            MaterialTheme {
                // ⭐ Crear el NavController
                val navController = rememberNavController()

                val vm: MainViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return MainViewModel(covidRepo, prefsRepo) as T
                        }
                    }
                )

                // ⭐ Pasar ambos parámetros a AppNavigation
                AppNavigation(
                    navController = navController,
                    vm = vm
                )
            }
        }
    }
}