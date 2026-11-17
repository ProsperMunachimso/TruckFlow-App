
package com.griffith.truckflowapp.model

// This data class stores the details of a truck added by a Truck Owner.
// It includes the truck type, capacity, daily price and availability.
data class Truck(
    val id: String,
    val type: String,            // The type of truck
    val capacityKg: Int,         // The truck's capacity in kilograms
    val pricePerDay: Double,     // The daily rental price
    val isAvailable: Boolean,    // Whether the truck is available for booking
    val imageUrl: String? = null // Optional image of the truck
)

// This data class stores the summary statistics for the Truck Owner dashboard.
// I use it to display the owner's trucks, booking requests,
// active bookings and monthly revenue.
data class OwnerMetrics(
    val myTrucks: Int,
    val bookingRequests: Int,
    val activeBookings: Int,
    val monthlyRevenue: Double
)
