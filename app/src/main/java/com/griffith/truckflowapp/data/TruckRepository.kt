
package com.griffith.truckflowapp.data

import com.griffith.truckflowapp.model.Truck
import kotlinx.coroutines.flow.Flow

// This interface defines the functions used to manage the owner's trucks.
// I use it to keep the data operations separate from the screens.
interface TruckRepository {

    // This provides the truck list and updates it whenever the data changes.
    fun observeTrucks(): Flow<List<Truck>>

    // This function adds a new truck to the list.
    fun addTruck(truck: Truck)

    // This function updates whether a particular truck is available.
    fun updateTruckAvailability(truckId: String, isAvailable: Boolean)

    // This generates an ID for each new truck.
    fun nextTruckId(): String
}
