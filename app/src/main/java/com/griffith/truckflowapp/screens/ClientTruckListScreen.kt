package com.griffith.truckflowapp.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.griffith.truckflowapp.BookingDetailsActivity
import com.griffith.truckflowapp.ui.theme.SuccessGreen
import com.griffith.truckflowapp.ui.theme.TruckFlowAppTheme
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.griffith.truckflowapp.model.BookingStatus
import com.griffith.truckflowapp.model.DashboardMetrics
import com.griffith.truckflowapp.model.RecentBooking

// Temporary dashboard figures used to test the interface without a server connection.
// These are sample values, not totals calculated from the bookings shown below.
private val sampleMetrics = DashboardMetrics(
    activeShipments = 3,
    completedDeliveries = 12,
    labourRequestsPending = 4,
    totalSpend = 15400.0
)

// The sample bookings also provide examples of different status colours and routes.
// Replace them with backend results when API integration is implemented.
private val sampleBookings = listOf(
    RecentBooking("BK-ABC123", "Dublin", "Galway", BookingStatus.CONFIRMED, "15 Nov 2026"),
    RecentBooking("BK-DEF456", "Cork", "Blanchardstown", BookingStatus.PENDING, "20 Nov 2026"),
    RecentBooking("BK-GHI789", "Limerick", "Dun Laoghaire", BookingStatus.IN_TRANSIT, "3 Dec 2026")
)

// Main client dashboard adapted from the existing TruckFlow React dashboard.
// It keeps the same general information hierarchy: greeting, summary metrics,
// common actions, and recent bookings. The desktop booking table becomes a vertical
// list of cards because a full-width table is difficult to read on a phone.
// This screen currently demonstrates the interface using sample data.
@Composable
fun ClientTruckListScreen(
    onNavigateToCreateBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The Android Context is required to launch external apps and Activities from Compose button click handlers.
    val context = LocalContext.current

    // I used the fixed sample data while the mobile app is not connected to the API.
    // I used remember because it retains the same references across recompositions.
    val metrics = remember { sampleMetrics }
    val bookings = remember { sampleBookings }

    var selectedFilter by remember { mutableStateOf<BookingStatus?>(null) } // This tracks the currently selected status filter. null = "All" (no filter).

    val visibleBookings = if (selectedFilter == null) {
        bookings
    } else {
        bookings.filter { it.status == selectedFilter }
    }

    // I used LazyColumn to build a vertically scrolling screen and composes list content as needed.
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // Horizontal padding and spacedBy to keep content away from screen edges
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // First list item: screen title and explanatory subtitle.
        // MaterialTheme typography and colours keep this header consistent
        // with the shared styles defined in the theme package.
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Welcome back, Prosper! Here's an overview of your operations.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Dashboard statistics are displayed in a 2-by-2 arrangement instead of the wider desktop layout.
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { // Each Row contains two reusable MetricCards.
                    MetricCard(
                        icon = Icons.Filled.LocalShipping,
                        value = metrics.activeShipments.toString(),
                        label = "Active Shipments",
                        iconTint = MaterialTheme.colorScheme.primary,
                        caption = "+3 from last week",
                        modifier = Modifier.weight(1f) // I used Modifier.weight(1f) to give both cards an equal share of available width.
                        // while the spacing prevents the cards from touching.
                    )
                    MetricCard(
                        icon = Icons.Filled.CheckCircle,
                        value = metrics.completedDeliveries.toString(),
                        label = "Completed",
                        iconTint = SuccessGreen,
                        caption = "+12 this month",
                        modifier = Modifier.weight(1f) // I used Modifier.weight(1f) to give both cards an equal share of available width.
                        // while the spacing prevents the cards from touching.
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { // Each Row contains two reusable MetricCards.
                    MetricCard(
                        icon = Icons.Filled.PersonAdd,
                        value = metrics.labourRequestsPending.toString(),
                        label = "Labour Requests",
                        iconTint = MaterialTheme.colorScheme.secondary,
                        caption = "4 pending",
                        modifier = Modifier.weight(1f)// I used Modifier.weight(1f) to give both cards an equal share of available width.
                        // while the spacing prevents the cards from touching.
                    )
                    MetricCard(
                        icon = Icons.Filled.AttachMoney,
                        value = "€${metrics.totalSpend.toInt()}",
                        label = "Total Spend",
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        caption = "This month",
                        modifier = Modifier.weight(1f)// I used Modifier.weight(1f) to give both cards an equal share of available width.
                        // while the spacing prevents the cards from touching.
                    )
                }
            }
        }

        // Separate dashboard sections visually without adding another large heading.
        item { HorizontalDivider() }

        // Quick actions correspond to the main workflows in the React dashboard.
        // The first three buttons show the intended navigation options,
        // The two M1 demonstration actions below remain functional so both kinds
        // of Android Intent can still be demonstrated.
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                // Primary action visually emphasised using a filled Button.
                Button(
                    onClick = onNavigateToCreateBooking,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.AddCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Book a Truck")
                }
                // This button is for the client to track shipments.
                OutlinedButton(
                    onClick = { /* M3: navigate to MyBookings */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.TrackChanges, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Track Shipment")
                }
                // This button is for the labour assistance becase client's can request for laboour.
                OutlinedButton(
                    onClick = {  },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Assignment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Request Labour")
                }
                // Implicit Intent, ACTION_VIEW asks Android to open a map-capable app.
                // For now the geo URI uses a fixed example pickup point in Dublin.
                // If no compatible app is installed, show a Toast instead of crashing.
                // This is not yet connected to a booking-specific pickup location.
                OutlinedButton(
                    onClick = {
                        val uri = Uri.parse("geo:53.3453,-6.2646?q=53.3453,-6.2646(Truck+pickup)")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "No maps app found", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Pickup in Maps (M1 demo)")
                }
                // Explicit intent, open the app's Booking Details activity.
                // Explicit Intent, names BookingDetailsActivity directly, so Android opens a specific screen within TruckFlow.
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(context, BookingDetailsActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Assignment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Booking Details (M1 demo)")
                }
            }
        }

        // Separate dashboard sections visually without adding another large heading.
        item { HorizontalDivider() }

        // Recent bookings heading uses a title style for hierarchy and a smaller
        // subtitle to explain what information the list contains.
        item {
            Column {
                Text(
                    text = "Recent Bookings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Your latest shipment activities",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("All") }
                        )
                    }
                    items(BookingStatus.entries) { status ->
                        FilterChip(
                            selected = selectedFilter == status,
                            onClick = { selectedFilter = status },
                            label = { Text(status.label) }
                        )
                    }
                }
            }
        }

        // Display an empty-state message if there are no bookings.
        // The current list is fixed sample data, so no live updates occur yet.
        if (visibleBookings.isEmpty()) {
            item {
                Text(
                    text = if (selectedFilter == null) { //Checks to see if there have been bookings made in the past
                        "No bookings yet. Tap 'Book a Truck' to create one."
                    } else {
                        "No ${selectedFilter?.label} bookings."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(visibleBookings) { booking -> RecentBookingCard(booking = booking) // This creates a reusable RecentBookingCard for each entry.
            }
        }

        // Footer action for the full booking history; navigation is not implemented.
        item {
            OutlinedButton(
                onClick = {  },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text("View All Bookings")
            }
        }
    }
}

// Reusable dashboard metric component. Parameters allow the same layout to
// display shipments, deliveries, labour requests, or spending without repeating
// the Card implementation. The caller controls width using the modifier parameter.
// The card uses theme surface colours so it adapts to light and dark modes.
@Composable
private fun MetricCard(
    icon: ImageVector,
    value: String,
    label: String,
    iconTint: Color,
    caption: String,
    modifier: Modifier = Modifier
) {
    // Card provides a Material container, its background uses surfaceVariant
    // to distinguish the statistic from the surrounding dashboard.
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Place the icon and numeric value on opposite sides of the card.
            // SpaceBetween uses the available width rather than fixed offsets.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Reusable recent-booking card, adapted from one row of the React bookings table.
// The first row shows the booking reference and a coloured status indicator.
// Route and pickup date are stacked underneath for easier reading on mobile.
// The Track action is visible but currently has no implemented navigation.
@Composable
private fun RecentBookingCard(booking: RecentBooking) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Place the icon and numeric value on opposite sides of the card.
            // SpaceBetween uses the available width rather than fixed offsets.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.id,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Render the booking status with a consistent reusable chip.
                StatusChip(status = booking.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${booking.pickup} → ${booking.delivery}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = booking.pickupDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Track")
            }
        }
    }
}

// StatusChip maps each BookingStatus enum value to a background and text colour.
// Separate foreground/background colours help the label remain readable.
// MaterialTheme container colours follow the active theme; delivered uses a
// translucent success green. Text is retained so meaning is not colour-only.
@Composable
private fun StatusChip(status: BookingStatus) {
    // Pick the chip background based on the current booking state.
    val background = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
        BookingStatus.IN_TRANSIT -> MaterialTheme.colorScheme.tertiaryContainer
        BookingStatus.DELIVERED -> SuccessGreen.copy(alpha = 0.2f)
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
    }
    // Choose a corresponding text colour for sufficient contrast.
    val foreground = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.onPrimaryContainer
        BookingStatus.IN_TRANSIT -> MaterialTheme.colorScheme.onTertiaryContainer
        BookingStatus.DELIVERED -> SuccessGreen
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.onErrorContainer
    }
    // I used a rounded Surface because it acts as a compact, non-interactive status label.
    // Padding around the text gives it the appearance of a small pill.
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = background
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = foreground,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// Android Studio preview renders the dashboard with the shared TruckFlow theme.
// This helps inspect spacing and colours without launching the full application.
// It does not verify navigation, Intents, or runtime interaction behaviour.
@Preview(showBackground = true)
@Composable
private fun ClientTruckListScreenPreview() {
    TruckFlowAppTheme {
        ClientTruckListScreen(onNavigateToCreateBooking = {})
    }
}