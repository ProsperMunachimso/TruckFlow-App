package com.griffith.truckflowapp.navigation

object NavRoutes {
    const val ROLE_SELECTION = "role_selection"
    const val CLIENT_TRUCKS = "client_trucks"
    const val CREATE_BOOKING = "create_booking"
    const val MY_BOOKINGS = "my_bookings"

    // Added a route with a booking ID path argument. The pattern is used by NavHost,
    const val TRACK_SHIPMENT_PATTERN = "track_shipment/{bookingId}"
    const val TRACK_SHIPMENT_ARG = "bookingId"
    // This the helper function builds a concrete route for navigate().
    fun trackShipmentRoute(bookingId: String) = "track_shipment/$bookingId"
}