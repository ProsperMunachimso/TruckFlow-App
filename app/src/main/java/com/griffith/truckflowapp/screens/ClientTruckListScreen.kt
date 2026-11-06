package com.griffith.truckflowapp.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.griffith.truckflowapp.BookingDetailsActivity
import com.griffith.truckflowapp.ui.theme.TruckFlowAppTheme

// Placeholder for the Client dashboard. For Milestone 1 it only needs to
// demonstrate the implicit intent requirement, so I added a single button
// that opens a hardcoded pickup location in Google Maps. The real truck
// list will replace this once the repository and ViewModel exist in M2/M3.
@Composable
fun ClientTruckListScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // I used Column here because there are now two elements to stack (title + button). Box is fine for one child but Column is
    // clearer once you have more than one.
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Client Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Button which I made to connect to whatever app can handle geo for the user.
        Button(
            onClick = {
                // I used a random Dublin pickup location for the demo. The "?q=" part tells Maps to drop a pin at that coordinate with a label.
                val uri = Uri.parse(
                    "geo:53.3453,-6.2646?q=53.3453,-6.2646(Truck+pickup)"
                )

                val intent = Intent(Intent.ACTION_VIEW, uri)

                // Used a try/catch in case no maps app is installed. On a
                // stock emulator Maps is present, but on a stripped device the
                // system would throw ActivityNotFoundException instead of
                // doing nothing, so this is the defensive way to handle it.
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "No maps app found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Pickup Location in Maps")
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Explicit intent demo. I target a specific Activity in my app.
        Button(
            onClick = {
                // An explicit intent names the exact class to launch.
                val intent = Intent(context, BookingDetailsActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Booking Details")
        }
    }
}

// Preview wrapped in the app theme so it matches the running app.
@Preview(showBackground = true)
@Composable
private fun ClientTruckListScreenPreview() {
    TruckFlowAppTheme {
        ClientTruckListScreen()
    }
}