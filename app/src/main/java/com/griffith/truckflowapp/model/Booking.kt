package com.griffith.truckflowapp.model

// DashboardMetrics groups the four summary values displayed at the top
// of the client screen. Keeping these values in one data class makes the
// metric-card section easier to update when data is eventually loaded
// from the existing TruckFlow web backend.
data class DashboardMetrics(
    val activeShipments: Int,
    val completedDeliveries: Int,
    val labourRequestsPending: Int,
    val totalSpend: Double
)

// One row from the bookings list. Used by the dashboard's Recent Bookings
// section and by the tracking feature, so both screens share a single
// definition of what a booking looks like.
data class RecentBooking(
    val id: String,
    val pickup: String,
    val delivery: String,
    val status: BookingStatus,
    val pickupDate: String
)

// One status per booking. Kept in the model package because both the
// dashboard and the tracking screen reference it — a duplicate enum in
// each screen file would require translating between the two.
enum class BookingStatus(val label: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    IN_TRANSIT("In Transit"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}