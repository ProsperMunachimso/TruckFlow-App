
package com.griffith.truckflowapp.model

// This data class is for storing the summary information displayed
// on the client dashboard, such as active shipments and total spending.
data class DashboardMetrics(
    val activeShipments: Int,
    val completedDeliveries: Int,
    val labourRequestsPending: Int,
    val totalSpend: Double
)

// This data class is for storing the details of a booking.
// I used it to display recent bookings on the client dashboard.
data class RecentBooking(
    val id: String,
    val pickup: String,
    val delivery: String,
    val status: BookingStatus,
    val pickupDate: String
)

// This enum class is for defining the different statuses a booking can have.
// I kept it in the model package so both the dashboard and tracking screen
// can use the same booking statuses.
enum class BookingStatus(val label: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    IN_TRANSIT("In Transit"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}
