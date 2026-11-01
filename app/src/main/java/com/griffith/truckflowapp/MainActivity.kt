package com.griffith.truckflowapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.griffith.truckflowapp.model.UserRole
import com.griffith.truckflowapp.navigation.NavRoutes
import com.griffith.truckflowapp.screens.ClientTruckListScreen
import com.griffith.truckflowapp.screens.RoleSelectionScreen
import com.griffith.truckflowapp.ui.theme.TruckFlowAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TruckFlowAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TruckFlowApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TruckFlowApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.ROLE_SELECTION,
        modifier = modifier
    ) {
        composable(NavRoutes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    if (role == UserRole.CLIENT) {
                        navController.navigate(NavRoutes.CLIENT_TRUCKS)
                    }
                    // Owner and Labourer routing deferred to later tasks
                }
            )
        }

        composable(NavRoutes.CLIENT_TRUCKS) {
            ClientTruckListScreen()
        }
    }
}