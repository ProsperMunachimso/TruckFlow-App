
package com.griffith.truckflowapp.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.griffith.truckflowapp.model.BookingStatus
import com.griffith.truckflowapp.model.ShipmentTracking
import com.griffith.truckflowapp.viewmodel.TrackingViewModel

// This screen is for displaying the tracking details of a shipment.
// I used the TrackingViewModel to receive location and status updates
// and show the truck's progress along its route.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackShipmentScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    viewModel: TrackingViewModel = viewModel()
) {
    // I used collectAsStateWithLifecycle so the screen automatically
    // updates when the repository sends new tracking information.
    val tracking by viewModel.track(bookingId)
        .collectAsStateWithLifecycle()

    // Scaffold provides the layout for the top bar and screen content.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Shipment") },

                // The back button returns the user to the previous screen.
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                // I used the app's primary colours to keep the design consistent.
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        // This Box holds either the loading indicator or tracking details.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (tracking == null) {

                // Show a loading indicator while waiting for the first update.
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                // Once tracking data is available, display the shipment details.
                // I made the content scrollable for smaller phone screens.
                TrackingContent(
                    tracking = tracking!!,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                )
            }
        }
    }
}

// This function displays the booking information, tracking progress
// and the truck's current coordinates.
@Composable
private fun TrackingContent(
    tracking: ShipmentTracking,
    modifier: Modifier = Modifier
) {
    // I used a Column to arrange the tracking cards vertically.
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // This card displays the booking ID, route and current status.
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // I used a Row to place the booking ID and status side by side.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Booking ${tracking.bookingId}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Displays the current booking status.
                    StatusChip(status = tracking.status)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Shows the pickup and delivery locations.
                Text(
                    text = "${tracking.pickupLocation} → ${tracking.deliveryLocation}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // This card shows the truck's progress from pickup to delivery.
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Live location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // The progress bar updates whenever the truck moves.
                RouteProgressBar(progress = tracking.progress)

                Spacer(modifier = Modifier.height(12.dp))

                // I placed the pickup and delivery names at opposite ends.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        tracking.pickupLocation,
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        tracking.deliveryLocation,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Convert the progress value from 0–1 into a percentage.
                Text(
                    text = "${(tracking.progress * 100).toInt()}% of the route complete",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // This card displays the truck's simulated GPS coordinates.
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Truck coordinates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // I used let to display the coordinates only when available.
                tracking.truckLocation?.let { loc ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        // Display latitude and longitude to four decimal places.
                        Text(
                            text = "Lat ${"%.4f".format(loc.latitude)}, " +
                                    "Lng ${"%.4f".format(loc.longitude)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

// This function displays an animated progress bar showing
// how far the truck has travelled along its route.
@Composable
private fun RouteProgressBar(progress: Float) {

    // I used animateFloatAsState to make the truck move smoothly
    // instead of jumping whenever a new location is received.
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "truck_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {

        // This is the background of the progress bar.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .align(Alignment.CenterStart)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(3.dp)
                )
        )

        // This section fills as the truck gets closer to its destination.
        // I used animatedProgress to control how much of the bar is filled.
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(6.dp)
                .align(Alignment.CenterStart)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(3.dp)
                )
        )

        // I used a truck icon to represent the current position.
        // It moves towards the end of the bar as progress increases.
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.CenterEnd
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = "Truck position",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// This function displays the shipment status using different
// colours depending on the current booking status.
@Composable
private fun StatusChip(status: BookingStatus) {

    // I used different background colours to distinguish each status.
    val background = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
        BookingStatus.IN_TRANSIT -> MaterialTheme.colorScheme.tertiaryContainer
        BookingStatus.DELIVERED -> MaterialTheme.colorScheme.primaryContainer
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
    }

    // These colours make sure the text is readable on each background.
    val foreground = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.onPrimaryContainer
        BookingStatus.IN_TRANSIT -> MaterialTheme.colorScheme.onTertiaryContainer
        BookingStatus.DELIVERED -> MaterialTheme.colorScheme.onPrimaryContainer
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.onErrorContainer
    }

    // I used a rounded Surface to display the status as a small badge.
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = background
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Show a checkmark when the shipment has been delivered.
            if (status == BookingStatus.DELIVERED) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = foreground,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.size(4.dp))
            }

            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = foreground
            )
        }
    }
}
