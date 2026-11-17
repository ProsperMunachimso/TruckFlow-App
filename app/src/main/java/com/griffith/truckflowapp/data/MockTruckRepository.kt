
package com.griffith.truckflowapp.data

import com.griffith.truckflowapp.model.Truck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// This object stores the owner's trucks temporarily in memory.
// I made it a singleton so different screens can access the same truck list.
object MockTruckRepository : TruckRepository {

    // I used MutableStateFlow to hold the trucks and notify the UI
    // whenever a truck is added or its availability changes.
    private val _trucks = MutableStateFlow<List<Truck>>(
        listOf(
            Truck("TRK-001", "3 Ton Truck", 1500, 450.0, isAvailable = true),
            Truck("TRK-002", "5 Ton Truck", 2500, 650.0, isAvailable = true),
            Truck("TRK-003", "8 Ton Truck", 4000, 850.0, isAvailable = false)
        )
    )

    // This exposes the truck list as a read-only Flow.
    override fun observeTrucks(): Flow<List<Truck>> = _trucks.asStateFlow()

    // I add the new truck to the existing list using update.
    override fun addTruck(truck: Truck) {
        _trucks.update { current -> current + truck }
    }

    // This finds the truck by its ID and changes its availability.
    override fun updateTruckAvailability(truckId: String, isAvailable: Boolean) {
        _trucks.update { current ->

            // I use map to update the matching truck while keeping the others unchanged.
            current.map { truck ->
                if (truck.id == truckId) truck.copy(isAvailable = isAvailable) else truck
            }
        }
    }

    // This function generates the next truck ID.
    // I use the highest existing number instead of the list size.
    override fun nextTruckId(): String {

        // This extracts the numeric part of each ID and finds the highest value.
        val maxNumber = _trucks.value
            .mapNotNull { it.id.removePrefix("TRK-").toIntOrNull() }
            .maxOrNull() ?: 0

        // I added one to the highest number and format it with leading zeros.
        return "TRK-${(maxNumber + 1).toString().padStart(3, '0')}"
    }
}
