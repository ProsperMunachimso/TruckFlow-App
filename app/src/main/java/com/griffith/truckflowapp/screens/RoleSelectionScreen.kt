package com.griffith.truckflowapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class RoleSelectionScreen {
    
    @Composable
    fun RoleSelectionScreen(
        onRoleSelected: (UserRole) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. A title — Text composable
            Text(text = "TruckFlow")

            // 2. ONE button as an example
            Button(onClick = { onRoleSelected(UserRole.CLIENT) }) {
                Text("Continue as Client")
            }
        }
    }
}