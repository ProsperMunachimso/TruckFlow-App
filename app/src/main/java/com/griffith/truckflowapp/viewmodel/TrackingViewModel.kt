package com.griffith.truckflowapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffith.truckflowapp.data.MockTrackingRepository
import com.griffith.truckflowapp.data.TrackingRepository
import com.griffith.truckflowapp.model.ShipmentTracking
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// This class is for managing the shipment tracking data that is displayed
// on the Track Shipment screen. I used a ViewModel to separate the tracking
// logic from the user interface, so the screen only focuses on displaying
// the information rather than managing where the data comes from.
class TrackingViewModel(
    private val repository: TrackingRepository = MockTrackingRepository()
) : ViewModel() {

    // This function is for tracking a specific booking using its booking ID.
    // It gets the shipment updates from the repository and converts the
    // Flow into a StateFlow so the UI can observe the latest information.
    fun track(bookingId: String): StateFlow<ShipmentTracking?> =
        repository.observeShipment(bookingId).stateIn(
            scope = viewModelScope,
            // I used WhileSubscribed(5000) to keep the tracking flow active
            // for 5 seconds after the screen stops observing it. This helps
            // prevent the simulation from restarting during short interruptions,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null // The initial value is null because the shipment data has notbeen received yet.
        )
}