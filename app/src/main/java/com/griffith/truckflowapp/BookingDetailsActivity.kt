package com.griffith.truckflowapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.griffith.truckflowapp.ui.theme.TruckFlowAppTheme

// This is the BookingDetails class. It displays booking information in a separate Activity.
// This Activity is launched from the Client Dashboard using an explicit Intent.
class BookingDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruckFlowAppTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Booking Details",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = "Truck: 3 Ton Truck\nPickup: Dublin City Centre\nPrice: R450/day",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )

                    // This button closes this activity and takes the user back to the Client screen.
                    Button(
                        onClick = { finish() }, // finish() closes the current activity
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Dashboard")
                    }
                }
            }
        }
    }
}