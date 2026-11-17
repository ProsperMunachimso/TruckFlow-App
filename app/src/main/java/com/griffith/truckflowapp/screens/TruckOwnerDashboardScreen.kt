
package com.griffith.truckflowapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.griffith.truckflowapp.model.OwnerMetrics
import com.griffith.truckflowapp.model.Truck
import com.griffith.truckflowapp.screens.components.MetricCard
import com.griffith.truckflowapp.ui.theme.SuccessGreen
import com.griffith.truckflowapp.ui.theme.TruckFlowAppTheme

// I created sample statistics to display on the Truck Owner dashboard.
// These will be replaced with actual data when the features are connected.
private val sampleOwnerMetrics = OwnerMetrics(
    myTrucks = 3,
    bookingRequests = 5,
    activeBookings = 8,
    monthlyRevenue = 8500.0
)

// These sample trucks are used to populate the dashboard for now.
private val sampleTrucks = listOf(
    Truck("TRK-001", "3 Ton Truck", 1500, 450.0, isAvailable = true),
    Truck("TRK-002", "5 Ton Truck", 2500, 650.0, isAvailable = true),
    Truck("TRK-003", "8 Ton Truck", 4000, 850.0, isAvailable = false)
)

// This function displays the Truck Owner dashboard.
// It shows the owner's statistics, quick actions and truck listings.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TruckOwnerDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddTruck: () -> Unit,
    onNavigateToMyTrucks: () -> Unit,
    onNavigateToBookingRequests: () -> Unit,
    modifier: Modifier = Modifier
) {
    // I use remember to keep the sample data available during recomposition.
    val metrics = remember { sampleOwnerMetrics }
    val trucks = remember { sampleTrucks }

    // Scaffold provides the main layout and the top navigation bar.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Truck Owner") },

                // This button takes the user back to the previous screen.
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                // I used the app's primary colours for the top bar.
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        // I used LazyColumn to make the dashboard scrollable.
        // The padding keeps the content away from the edges and top bar.
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Displays the dashboard heading and welcome message.
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "My Fleet",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Welcome back, Prosper! Here's how your trucks are doing.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // I used the reusable MetricCard to display four statistics.
            // The cards are arranged in two rows with equal spacing.
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // The first row displays the truck count and booking requests.
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            icon = Icons.Filled.LocalShipping,
                            value = metrics.myTrucks.toString(),
                            label = "My Trucks",
                            iconTint = MaterialTheme.colorScheme.primary,
                            caption = "All listings",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            icon = Icons.Filled.Assignment,
                            value = metrics.bookingRequests.toString(),
                            label = "Requests",
                            iconTint = MaterialTheme.colorScheme.secondary,
                            caption = "Awaiting reply",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // The second row displays active bookings and monthly revenue.
                    // weight(1f) gives both cards equal space in the row.
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard(
                            icon = Icons.Filled.CheckCircle,
                            value = metrics.activeBookings.toString(),
                            label = "Active",
                            iconTint = SuccessGreen,
                            caption = "In progress",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            icon = Icons.Filled.AttachMoney,
                            value = "€${metrics.monthlyRevenue.toInt()}",
                            label = "Revenue",
                            iconTint = MaterialTheme.colorScheme.tertiary,
                            caption = "This month",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // I used a divider to separate the statistics from the actions.
            item { HorizontalDivider() }

            // These buttons allow the owner to add trucks, view listings
            // and manage incoming booking requests.
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // This is the main action for adding a new truck.
                    Button(
                        onClick = onNavigateToAddTruck,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Filled.AddCircle,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add New Truck")
                    }

                    // Opens the screen showing all the owner's trucks.
                    OutlinedButton(
                        onClick = onNavigateToMyTrucks,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Filled.LocalShipping,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View My Trucks")
                    }

                    // Opens the booking requests screen.
                    OutlinedButton(
                        onClick = onNavigateToBookingRequests,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Filled.Assignment,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Booking Requests")
                    }
                }
            }

            item { HorizontalDivider() }

            // Displays the heading for the truck listings section.
            item {
                Column {
                    Text(
                        text = "My Trucks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Your current listings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // I used items() to create a preview card for each truck.
            // LazyColumn handles displaying the cards as the user scrolls.
            items(trucks) { truck ->
                TruckPreviewCard(truck = truck)
            }

            // This button opens the full truck management screen.
            item {
                OutlinedButton(
                    onClick = onNavigateToMyTrucks,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text("Manage All Trucks")
                }
            }
        }
    }
}

// This function displays a summary of an individual truck.
// It shows the truck type, capacity, daily price and availability.
@Composable
private fun TruckPreviewCard(truck: Truck) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        // I used a Column to arrange the truck information vertically.
        Column(modifier = Modifier.padding(14.dp)) {

            // This Row places the truck details and availability status
            // on opposite sides of the card.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Displays the truck icon beside its type.
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = truck.type,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Displays whether the truck is available for booking.
                TruckAvailabilityChip(isAvailable = truck.isAvailable)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Displays the truck's capacity in kilograms.
            Text(
                text = "${truck.capacityKg} kg capacity",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Displays the truck's daily rental price.
            Text(
                text = "€${truck.pricePerDay.toInt()}/day",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// This function displays the availability status of a truck.
// I used different colours to distinguish available and unavailable trucks.
@Composable
private fun TruckAvailabilityChip(isAvailable: Boolean) {

    // I change the background colour depending on availability.
    val background = if (isAvailable)
        SuccessGreen.copy(alpha = 0.15f)
    else
        MaterialTheme.colorScheme.errorContainer

    // I also change the text colour to match the availability status.
    val foreground = if (isAvailable)
        SuccessGreen
    else
        MaterialTheme.colorScheme.onErrorContainer

    // The label changes based on whether the truck is available.
    val label = if (isAvailable) "Available" else "Unavailable"

    // Surface gives the availability label its rounded background.
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = background
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = foreground,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
        )
    }
}

// This preview allows me to check the dashboard design in Android Studio.
@Preview(showBackground = true)
@Composable
private fun TruckOwnerDashboardPreview() {
    TruckFlowAppTheme {
        TruckOwnerDashboardScreen(
            onNavigateBack = {},
            onNavigateToAddTruck = {},
            onNavigateToMyTrucks = {},
            onNavigateToBookingRequests = {}
        )
    }
}
