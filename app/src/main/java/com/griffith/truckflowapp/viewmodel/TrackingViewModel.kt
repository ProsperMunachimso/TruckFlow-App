package com.griffith.truckflowapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffith.truckflowapp.data.MockTrackingRepository
import com.griffith.truckflowapp.data.TrackingRepository
import com.griffith.truckflowapp.model.ShipmentTracking
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// Exposes the repository's Flow as a StateFlow so the Compose screen
// can collect it with collectAsStateWithLifecycle and get a stable
// current value plus automatic lifecycle handling.
//
// The default constructor takes MockTrackingRepository so ViewModel can
// be instantiated without a factory (required for Compose's viewModel()).
// Tests can pass a fake repository by hand.
class TrackingViewModel(
    private val repository: TrackingRepository = MockTrackingRepository()
) : ViewModel() {

    fun track(bookingId: String): StateFlow<ShipmentTracking?> =
        repository.observeShipment(bookingId).stateIn(
            scope = viewModelScope,
            // WhileSubscribed(5000) keeps the flow alive for 5 seconds
            // after the last collector leaves — so a rotation does not
            // restart the simulation from step 0.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}