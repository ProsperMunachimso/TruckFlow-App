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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.griffith.truckflowapp.model.UserRole
import com.griffith.truckflowapp.navigation.NavRoutes
import com.griffith.truckflowapp.screens.ClientTruckListScreen
import com.griffith.truckflowapp.screens.CreateBookingScreen
import com.griffith.truckflowapp.screens.MyBookingsScreen
import com.griffith.truckflowapp.screens.RoleSelectionScreen
import com.griffith.truckflowapp.screens.TrackShipmentScreen
import com.griffith.truckflowapp.ui.theme.TruckFlowAppTheme

// This class is for starting the application and setting up the main layout.
// I used a single Activity with Compose Navigation to manage the different screens.
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // I used enableEdgeToEdge to allow the app to draw behind the system bars.
        enableEdgeToEdge()

        // setContent allows me to build the app's interface using Jetpack Compose.
        setContent {

            // I used TruckFlowAppTheme to keep the colours consistent across screens.
            TruckFlowAppTheme {

                // Scaffold provides the main layout for the application.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TruckFlowApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// This function is for managing navigation between the different screens.
// I separated it from MainActivity to keep the navigation code organised.
@Composable
fun TruckFlowApp(modifier: Modifier = Modifier) {

    // I used rememberNavController to manage navigation and the back stack.
    val navController = rememberNavController()

    // NavHost displays the current screen based on its route.
    // The role selection screen is the first screen shown when the app opens.
    NavHost(
        navController = navController,
        startDestination = NavRoutes.ROLE_SELECTION,
        modifier = modifier
    ) {

        // This route displays the role selection screen.
        composable(NavRoutes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onRoleSelected = { role ->

                    // For now, only the Client role navigates to its dashboard.
                    if (role == UserRole.CLIENT) {
                        navController.navigate(NavRoutes.CLIENT_TRUCKS)
                    }

                    // Truck Owner and Labourer navigation will be added later.
                }
            )
        }

        // This route displays the client dashboard.
        composable(NavRoutes.CLIENT_TRUCKS) {
            ClientTruckListScreen(

                // Navigate to the booking form when the user selects Book a Truck.
                onNavigateToCreateBooking = {
                    navController.navigate(NavRoutes.CREATE_BOOKING)
                },

                // I used the booking ID to open the tracking screen
                // for the booking selected by the user.
                onNavigateToTrackShipment = { bookingId ->
                    navController.navigate(
                        NavRoutes.trackShipmentRoute(bookingId)
                    )
                },

                // Navigate to My Bookings to view all bookings.
                onNavigateToMyBookings = {
                    navController.navigate(NavRoutes.MY_BOOKINGS)
                }
            )
        }

        // This route displays the form for creating a booking.
        composable(NavRoutes.CREATE_BOOKING) {
            CreateBookingScreen(

                // Return to the previous screen when the back button is pressed.
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // This route displays the tracking screen for a specific booking.
        composable(
            route = NavRoutes.TRACK_SHIPMENT_PATTERN,
            arguments = listOf(

                // I used a String argument to pass the booking ID between screens.
                navArgument(NavRoutes.TRACK_SHIPMENT_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            // Get the booking ID from the route.
            // If it is missing, an empty string is used instead.
            val bookingId = backStackEntry.arguments
                ?.getString(NavRoutes.TRACK_SHIPMENT_ARG)
                ?: ""

            TrackShipmentScreen(
                bookingId = bookingId,

                // Return to the previous screen.
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // This route displays the client's upcoming and past bookings.
        composable(NavRoutes.MY_BOOKINGS) {
            MyBookingsScreen(

                // Return to the client dashboard.
                onNavigateBack = {
                    navController.popBackStack()
                },

                // Open the tracking screen for the selected booking.
                onNavigateToTrackShipment = { bookingId ->
                    navController.navigate(
                        NavRoutes.trackShipmentRoute(bookingId)
                    )
                }
            )
        }
    }
}
