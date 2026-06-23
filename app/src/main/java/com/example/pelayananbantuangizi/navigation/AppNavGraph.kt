package com.example.pelayananbantuangizi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pelayananbantuangizi.data.api.ApiClient
import com.example.pelayananbantuangizi.data.repository.AuthRepository
import com.example.pelayananbantuangizi.data.repository.LansiaRepository
import com.example.pelayananbantuangizi.data.repository.PemeriksaanRepository
import com.example.pelayananbantuangizi.local.TokenDataStore
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val tokenDataStore = TokenDataStore(context)
    ApiClient.init(tokenDataStore)

    val authRepo = AuthRepository(ApiClient.apiService, tokenDataStore)
    val lansiaRepo = LansiaRepository(ApiClient.apiService)
    val pemeriksaanRepo = PemeriksaanRepository(ApiClient.apiService)

    val token by tokenDataStore.getToken().collectAsState(initial = null)
    val startDestination = if (token != null) Screen.LansiaList.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            com.example.pelayananbantuangizi.ui.auth.LoginScreen(
                authRepository = authRepo,
                onLoginSuccess = {
                    navController.navigate(Screen.LansiaList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.LansiaList.route) {
            com.example.pelayananbantuangizi.ui.lansia.LansiaListScreen(
                lansiaRepository = lansiaRepo,
                authRepository = authRepo,
                onNavigateToDetail = { id -> navController.navigate(Screen.LansiaDetail.createRoute(id)) },
                onNavigateToForm = { id -> navController.navigate(Screen.LansiaForm.createRoute(id)) },
                onLogout = {
                    // logout handled here — clear token and go back to Login
                    kotlinx.coroutines.MainScope().launch {
                        authRepo.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.LansiaDetail.route,
            arguments = listOf(navArgument("lansiaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lansiaId = backStackEntry.arguments?.getInt("lansiaId") ?: return@composable
            com.example.pelayananbantuangizi.ui.lansia.LansiaDetailScreen(
                lansiaRepository = lansiaRepo,
                lansiaId = lansiaId,
                onNavigateToEdit = { id -> navController.navigate(Screen.LansiaForm.createRoute(id)) },
                onNavigateToPemeriksaan = { id -> navController.navigate(Screen.PemeriksaanList.createRoute(id)) },
                onNavigateToMonitoring = { id, nama -> navController.navigate(Screen.Monitoring.createRoute(id, nama)) },
                onDeleted = {
                    navController.popBackStack(Screen.LansiaList.route, inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.LansiaForm.route,
            arguments = listOf(navArgument("lansiaId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val lansiaId = backStackEntry.arguments?.getInt("lansiaId")?.takeIf { it != -1 }
            com.example.pelayananbantuangizi.ui.lansia.LansiaFormScreen(
                lansiaRepository = lansiaRepo,
                lansiaId = lansiaId,
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.PemeriksaanList.route,
            arguments = listOf(navArgument("lansiaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lansiaId = backStackEntry.arguments?.getInt("lansiaId") ?: return@composable
            com.example.pelayananbantuangizi.ui.pemeriksaan.PemeriksaanListScreen(
                pemeriksaanRepository = pemeriksaanRepo,
                lansiaId = lansiaId,
                onNavigateToTambah = { id -> navController.navigate(Screen.TambahPemeriksaan.createRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.TambahPemeriksaan.route,
            arguments = listOf(navArgument("lansiaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lansiaId = backStackEntry.arguments?.getInt("lansiaId") ?: return@composable
            com.example.pelayananbantuangizi.ui.pemeriksaan.TambahPemeriksaanScreen(
                pemeriksaanRepository = pemeriksaanRepo,
                lansiaId = lansiaId,
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Monitoring.route,
            arguments = listOf(
                navArgument("lansiaId") { type = NavType.IntType },
                navArgument("namaLansia") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lansiaId = backStackEntry.arguments?.getInt("lansiaId") ?: return@composable
            val namaLansia = backStackEntry.arguments?.getString("namaLansia") ?: ""
            com.example.pelayananbantuangizi.ui.pemeriksaan.MonitoringScreen(
                pemeriksaanRepository = pemeriksaanRepo,
                lansiaId = lansiaId,
                namaLansia = java.net.URLDecoder.decode(namaLansia, "UTF-8"),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
