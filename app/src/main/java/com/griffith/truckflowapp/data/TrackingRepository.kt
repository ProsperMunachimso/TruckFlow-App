package com.griffith.truckflowapp.data

import com.griffith.truckflowapp.model.ShipmentTracking
import kotlinx.coroutines.flow.Flow

// I use a repository interface to separate the tracking screen from the source of the shipment information.
// For this milestone, the implementation will provide simulated locations.
interface TrackingRepository {
    fun observeShipment(
        bookingId: String
    ): Flow<ShipmentTracking?>
}
