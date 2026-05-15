package com.example.pelayananbantuangizi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.pelayananbantuangizi.navigation.AppNavGraph
import com.example.pelayananbantuangizi.ui.theme.PelayananBantuanGiziTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PelayananBantuanGiziTheme(darkTheme = false) {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}
