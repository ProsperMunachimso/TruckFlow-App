package com.griffith.truckflowapp.screens

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.griffith.truckflowapp.model.UserRole
import com.griffith.truckflowapp.ui.theme.TruckFlowAppTheme

// This is the first screen the user sees. It lets them pick which role they want to use the app as: Client, Truck Owner or Labourer.
@Composable
fun RoleSelectionScreen(
    // Instead of passing a NavController into this screen, I pass a callback.
    // The screen just reports which role was tapped and lets the caller decide
    // where to navigate. This keeps the screen reusable and easier to test.
    onRoleSelected: (UserRole) -> Unit,

    modifier: Modifier = Modifier
) {

    // Column stacks everything vertically. I centred the content both vertically and horizontally so the three buttons sit neatly in the middle.
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // App name at the top using the theme's headline style.
        Text(
            text = "TruckFlow",
            style = MaterialTheme.typography.headlineLarge
        )

        // Slogan under the title, using a smaller body style.
        Text(
            text = "Trucks. Labour. Moving Forward.",
            style = MaterialTheme.typography.bodyMedium
        )

        // Space between the header and the buttons.
        Spacer(modifier = Modifier.height(32.dp))

        // Client button. Tapping it fires the callback with CLIENT.
        // The screen itself does not navigate, I let MainActivity decide what happens.
        Button(
            onClick = { onRoleSelected(UserRole.CLIENT) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue as Client")
        }

        // Small gap between buttons so they do not touch.
        Spacer(modifier = Modifier.height(12.dp))

        // Truck Owner button. Same pattern as the Client button.
        Button(
            onClick = { onRoleSelected(UserRole.OWNER) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue as Truck Owner")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Labourer button. Same pattern again.
        Button(
            onClick = { onRoleSelected(UserRole.LABOURER) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue as Labourer")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoleSelectionScreenPreview() {
    TruckFlowAppTheme {
        RoleSelectionScreen(onRoleSelected = {})
    }
}