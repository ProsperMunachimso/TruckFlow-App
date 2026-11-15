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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.griffith.truckflowapp.model.BookingStatus
import com.griffith.truckflowapp.model.ShipmentTracking
import com.griffith.truckflowapp.viewmodel.TrackingViewModel

// Live tracking screen. Subscribes to the TrackingViewModel which exposes
// a StateFlow of ShipmentTracking. As the mock repository emits a new
// position every 1.5 seconds, this screen redraws with the truck icon
// moving further along the route.
//
// The route visualisation is deliberately abstract (a horizontal line
// with two city labels) rather than a real map, because drawing an
// actual map would require the Google Maps SDK — an unnecessary
// dependency for a mock track. In M3 with the real API, we would swap
// this Box for a GoogleMap composable.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackShipmentScreen(
    bookingId: String,
    onNavigateBack: () -> Unit,
    viewModel: TrackingViewModel = viewModel()
) {
    // collectAsStateWithLifecycle is the lifecycle-aware way to consume
    // a StateFlow from Compose. It stops collection when the screen is
    // in the background and resumes when it returns — no manual
    // DisposableEffect needed.
    val tracking by viewModel.track(bookingId)
        .collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Shipment") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (tracking == null) {
                // First frame before the StateFlow has emitted anything.
                // Shows a spinner so the user knows the app is working.
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
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

@Composable
private fun TrackingContent(tracking: ShipmentTracking, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ── Header card: booking ID + status chip ─────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                    StatusChip(status = tracking.status)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${tracking.pickupLocation} → ${tracking.deliveryLocation}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── Route visualisation ───────────────────────────
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Live location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(24.dp))
                RouteProgressBar(progress = tracking.progress)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(tracking.pickupLocation, style = MaterialTheme.typography.labelMedium)
                    Text(tracking.deliveryLocation, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${(tracking.progress * 100).toInt()}% of the route complete",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ── Coordinate readout ────────────────────────────
        // Shows the raw data driving the animation. In M3 this whole
        // card would be replaced by a real map centred on the truck.
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Truck coordinates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                tracking.truckLocation?.let { loc ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.size(8.dp))
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

// Horizontal progress bar with a truck icon that slides along it.
// animateFloatAsState smooths the 1.5-second jumps into continuous
// movement — without it the truck would teleport.
@Composable
private fun RouteProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "truck_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        // The grey track sits at the vertical centre of the box.
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
        // The filled portion grows to match progress.
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
        // The truck icon sits at the leading edge of the filled bar.
        // fillMaxWidth(animatedProgress) with CenterEnd aligns the icon
        // at the right edge of that filled region.
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

@Composable
private fun StatusChip(status: BookingStatus) {
    val background = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
        BookingStatus.IN_TRANSIT -> MaterialTheme.colorScheme.tertiaryContainer
        BookingStatus.DELIVERED -> MaterialTheme.colorScheme.primaryContainer
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
    }
    val foreground = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.onPrimaryContainer
        BookingStatus.IN_TRANSIT -> MaterialTheme.colorScheme.onTertiaryContainer
        BookingStatus.DELIVERED -> MaterialTheme.colorScheme.onPrimaryContainer
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.onErrorContainer
    }
    Surface(shape = RoundedCornerShape(12.dp), color = background) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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