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

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TruckFlow",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Trucks. Labour. Moving Forward.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onRoleSelected(UserRole.CLIENT) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue as Client")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onRoleSelected(UserRole.OWNER) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue as Truck Owner")
        }

        Spacer(modifier = Modifier.height(12.dp))

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