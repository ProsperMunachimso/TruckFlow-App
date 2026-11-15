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
import com.griffith.truckflowapp.screens.CreateBookingScreen

 /* MainActivity is the only Activity in the app apart from BookingDetailsActivity.
 I used a single-Activity architecture because Compose Navigation is designed
 around it,nstead of creatingg a new Activity for every screen, the NavHost
 swaps composables in and out inside this one Acctivity. This avoids the cost
 ofspinning up new Activities for every navigation and keeps the back stack
 managed in one place.*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // I wrapped the whole app in TruckFlowAppTheme so colours especiall stay the same through every screen.
            TruckFlowAppTheme {
                // Scaffold gives the app a standard layout skeleton with slots for a top bar, bottom bar, floating action button and content.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TruckFlowApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// TruckFlowApp holds the navigation setup. I separated it from MainActivity so the navigation logic lives in its own composable
@Composable
fun TruckFlowApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // NavHost is the container that displays whichever screen matches the current route. startDestination tells it which screen to show first when the app opens
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
        // Client dashboard I edited this to now take a navigation callback so the "Book a
        // Truck" button can route to the form.
        composable(NavRoutes.CLIENT_TRUCKS) {
            ClientTruckListScreen(
                onNavigateToCreateBooking = { //Kept the callback as a parameter (rather than passing navController) to preserve the hoisting pattern made with RoleSelectionScreen.
                    navController.navigate(NavRoutes.CREATE_BOOKING)
                }
            )
        }

        // Booking form. popBackStack() returns to whichever screen was on top before navigation
        composable(NavRoutes.CREATE_BOOKING) {
            CreateBookingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}