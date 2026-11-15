
package com.griffith.truckflowapp.screens.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.griffith.truckflowapp.model.BookingStatus
import com.griffith.truckflowapp.ui.theme.SuccessGreen

// This function is for displaying the current booking status.
// I made it reusable so the dashboard and My Bookings screen
// can display the same status colours and design.
@Composable
fun BookingStatusChip(status: BookingStatus, modifier: Modifier = Modifier) {

    // I used different background colours for each booking status.
    val background = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
        BookingStatus.IN_TRANSIT -> MaterialTheme.colorScheme.tertiaryContainer
        BookingStatus.DELIVERED -> SuccessGreen.copy(alpha = 0.2f)
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
    }

    // These colours make the text readable against each background.
    val foreground = when (status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.onPrimaryContainer
        BookingStatus.IN_TRANSIT -> MaterialTheme.colorScheme.onTertiaryContainer
        BookingStatus.DELIVERED -> SuccessGreen
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.onErrorContainer
    }

    // I used a rounded Surface to display the status as a small badge.
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = background,
        modifier = modifier
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
