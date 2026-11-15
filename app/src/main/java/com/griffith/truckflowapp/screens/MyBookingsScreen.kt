
package com.griffith.truckflowapp.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.griffith.truckflowapp.model.BookingStatus
import com.griffith.truckflowapp.model.RecentBooking
import com.griffith.truckflowapp.screens.components.BookingStatusChip

// This enum class is for defining the two tabs on the My Bookings screen.
enum class BookingTab(val title: String) {
    UPCOMING("Upcoming"),
    PAST("Past")
}

// I created sample bookings to test how upcoming and past bookings
// are displayed before connecting the app to the backend.
private val allSampleBookings = listOf(
    // Upcoming bookings
    RecentBooking("BK-ABC123", "Dublin", "Galway", BookingStatus.CONFIRMED, "15 Nov 2026"),
    RecentBooking("BK-DEF456", "Cork", "Blanchardstown", BookingStatus.PENDING, "20 Nov 2026"),
    RecentBooking("BK-GHI789", "Limerick", "Dun Laoghaire", BookingStatus.IN_TRANSIT, "3 Dec 2026"),

    // Past bookings
    RecentBooking("BK-JKL012", "Dublin", "Cork", BookingStatus.DELIVERED, "5 Nov 2026"),
    RecentBooking("BK-MNO345", "Galway", "Dublin", BookingStatus.DELIVERED, "1 Nov 2026"),
    RecentBooking("BK-PQR678", "Belfast", "Dublin", BookingStatus.CANCELLED, "28 Oct 2026")
)

// I used these statuses to identify which bookings are still active.
private val upcomingStatuses = setOf(
    BookingStatus.PENDING,
    BookingStatus.CONFIRMED,
    BookingStatus.IN_TRANSIT
)

// This screen is for displaying all the client's bookings.
// I used two tabs to separate upcoming bookings from past bookings.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTrackShipment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // I used remember to keep track of the selected tab.
    // The Upcoming tab is selected when the screen first opens.
    var selectedTab by remember { mutableStateOf(BookingTab.UPCOMING) }

    // Filter the bookings based on their current status.
    val upcoming = remember {
        allSampleBookings.filter { it.status in upcomingStatuses }
    }
    val past = remember {
        allSampleBookings.filter { it.status !in upcomingStatuses }
    }

    // Display bookings based on the tab selected by the user.
    val visibleBookings = if (selectedTab == BookingTab.UPCOMING) upcoming else past

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings") },

                // This button returns the user to the previous screen.
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

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // I used TabRow to let users switch between upcoming and past bookings.
            // Each tab also displays the number of bookings.
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                Tab(
                    selected = selectedTab == BookingTab.UPCOMING,
                    onClick = { selectedTab = BookingTab.UPCOMING },
                    text = {
                        Text("${BookingTab.UPCOMING.title} (${upcoming.size})")
                    }
                )

                Tab(
                    selected = selectedTab == BookingTab.PAST,
                    onClick = { selectedTab = BookingTab.PAST },
                    text = {
                        Text("${BookingTab.PAST.title} (${past.size})")
                    }
                )
            }

            // Show a message if there are no bookings in the selected tab.
            if (visibleBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(56.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Display a different message for each tab.
                        Text(
                            text = if (selectedTab == BookingTab.UPCOMING) {
                                "No upcoming bookings"
                            } else {
                                "No past bookings yet"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = if (selectedTab == BookingTab.UPCOMING) {
                                "Tap 'Book a Truck' on the dashboard to create one."
                            } else {
                                "Completed and cancelled bookings will appear here."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {

                // I used LazyColumn to display the bookings in a scrollable list.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Create a booking card for each booking in the selected tab.
                    items(visibleBookings, key = { it.id }) { booking ->
                        BookingHistoryCard(
                            booking = booking,

                            // Only allow tracking for bookings that are still active.
                            onTrack = if (booking.status in upcomingStatuses) {
                                { onNavigateToTrackShipment(booking.id) }
                            } else {
                                null
                            }
                        )
                    }
                }
            }
        }
    }
}

// This function is for displaying the details of an individual booking.
// I used a Card to group the booking ID, route, date and status together.
@Composable
private fun BookingHistoryCard(
    booking: RecentBooking,
    onTrack: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Display the booking ID and status side by side.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.id,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // I reused BookingStatusChip to keep the status design consistent.
                BookingStatusChip(status = booking.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show the pickup and delivery locations.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${booking.pickup} → ${booking.delivery}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Display the pickup date with a calendar icon.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = booking.pickupDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Only show the Track button when tracking is available.
            if (onTrack != null) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onTrack,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Track")
                }
            }
        }
    }
}
