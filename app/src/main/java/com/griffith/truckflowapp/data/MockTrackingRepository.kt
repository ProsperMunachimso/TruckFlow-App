package com.griffith.truckflowapp.data

import com.griffith.truckflowapp.model.BookingStatus
import com.griffith.truckflowapp.model.ShipmentTracking
import com.griffith.truckflowapp.model.TruckLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

// Simulates a truck travelling from Dublin to Galway. Emits 21 updates
// (one every 1.5s) interpolating between the two coordinates, so the UI
// can show the truck moving across Ireland over ~30 seconds and then
// arriving. In M3 the real repository will replace this with a call to
// GET /api/bookings/{id}/tracking or a Firebase realtime stream.
class MockTrackingRepository : TrackingRepository {

    override fun observeShipment(bookingId: String): Flow<ShipmentTracking?> = flow {
        val dublin = TruckLocation(53.3498, -6.2603)
        val galway = TruckLocation(53.2707, -9.0568)
        val steps = 20

        // Emit an initial "not yet started" state so the screen has
        // something to show immediately instead of a blank 1.5s delay.
        emit(
            ShipmentTracking(
                bookingId = bookingId,
                pickupLocation = "Dublin",
                deliveryLocation = "Galway",
                status = BookingStatus.CONFIRMED,
                truckLocation = dublin,
                progress = 0f
            )
        )
        delay(1500)

        for (i in 1..steps) {
            val fraction = i.toDouble() / steps
            val lat = dublin.latitude + (galway.latitude - dublin.latitude) * fraction
            val lng = dublin.longitude + (galway.longitude - dublin.longitude) * fraction

            emit(
                ShipmentTracking(
                    bookingId = bookingId,
                    pickupLocation = "Dublin",
                    deliveryLocation = "Galway",
                    status = if (i < steps) BookingStatus.IN_TRANSIT else BookingStatus.DELIVERED,
                    truckLocation = TruckLocation(lat, lng),
                    progress = fraction.toFloat()
                )
            )
            delay(1500)
        }
    }.flowOn(Dispatchers.Default)
}