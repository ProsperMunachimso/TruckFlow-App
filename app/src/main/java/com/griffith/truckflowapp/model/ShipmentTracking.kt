package com.griffith.truckflowapp.model

// I used a separate data class for geographical coordinates so that
// the same structure can represent a simulated location now and
// a real GPS location received from the backend in the future.
data class TruckLocation(
    val latitude: Double,
    val longitude: Double
)

// This model contains the information needed by the tracking screen.
// I keep the tracking data separate from the UI so that changing
// the source of the information will not require rewriting the screen.
// The status uses BookingStatus — the same enum the dashboard uses —
// so there is no translation step between the two screens.
data class ShipmentTracking(
    val bookingId: String,
    val pickupLocation: String,
    val deliveryLocation: String,
    val status: BookingStatus,
    val truckLocation: TruckLocation?
)