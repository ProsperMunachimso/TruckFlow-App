package com.griffith.truckflowapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.griffith.truckflowapp.data.MockTruckRepository
import com.griffith.truckflowapp.model.Truck

// These are the truck types the owner can select from the dropdown.
private val truckTypes = listOf(
    "1 Ton Bakkie",
    "3 Ton Truck",
    "5 Ton Truck",
    "8 Ton Truck",
    "10 Ton Truck"
)

// This function displays the form for adding a new truck.
// It validates the details and saves the truck in MockTruckRepository.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTruckScreen(onNavigateBack: () -> Unit) {

    // I use remember to store the values entered in the form.
    var selectedType by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }

    // These variables store validation errors for each field.
    var typeError by remember { mutableStateOf<String?>(null) }
    var capacityError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    // This controls whether the truck type dropdown is open.
    var dropdownExpanded by remember { mutableStateOf(false) }

    // I use this to store the truck after it has been added successfully.
    var addedTruck by remember { mutableStateOf<Truck?>(null) }

    // This function checks the form details before adding a truck.
    fun validateAndSubmit() {

        // Checks that the owner has selected a truck type.
        typeError = if (selectedType.isBlank()) "Truck type is required" else null

        // I check that the capacity is a valid number within the allowed range.
        capacityError = if (capacity.isBlank()) {
            "Capacity is required"
        } else {
            val c = capacity.toIntOrNull()
            when {
                c == null -> "Capacity must be a number"
                c <= 0 -> "Capacity must be positive"
                c < 100 -> "Capacity must be at least 100 kg"
                c > 50_000 -> "Capacity must be under 50,000 kg"
                else -> null
            }
        }

        // I also validate the daily rental price.
        priceError = if (price.isBlank()) {
            "Daily price is required"
        } else {
            val p = price.toDoubleOrNull()
            when {
                p == null -> "Price must be a number"
                p <= 0.0 -> "Price must be positive"
                p > 10_000.0 -> "Price must be under €10,000"
                else -> null
            }
        }

        // A truck can only be added when all fields are valid.
        val isValid = typeError == null && capacityError == null && priceError == null
        if (isValid) {

            // I create a Truck object using the details entered by the owner.
            val newTruck = Truck(
                id = MockTruckRepository.nextTruckId(),
                type = selectedType,
                capacityKg = capacity.toInt(),
                pricePerDay = price.toDouble(),
                isAvailable = isAvailable
            )

            // This saves the truck in the shared repository.
            MockTruckRepository.addTruck(newTruck)

            // I store the added truck so its details can be displayed below.
            addedTruck = newTruck
        }
    }

    // Scaffold provides the main layout and top navigation bar.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Truck") },

                // This button takes the owner back to the previous screen.
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        // I used a scrollable Column so the form fits on smaller screens.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // This section contains the truck's main details.
            Text(
                text = "Vehicle Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // I used a dropdown so the owner can select a predefined truck type.
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = { /* readOnly */ },
                    readOnly = true,
                    label = { Text("Truck Type *") },
                    isError = typeError != null,
                    supportingText = typeError?.let { { Text(it) } },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                // This displays the available truck types.
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    truckTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {

                                // I update the selected type and close the dropdown.
                                selectedType = type
                                typeError = null
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // This field allows the owner to enter the truck's capacity.
            OutlinedTextField(
                value = capacity,
                onValueChange = { capacity = it; capacityError = null },
                label = { Text("Capacity (kg) *") },
                isError = capacityError != null,
                supportingText = capacityError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // This field accepts the daily rental price in euros.
            OutlinedTextField(
                value = price,
                onValueChange = { price = it; priceError = null },
                label = { Text("Price per day (€) *") },
                isError = priceError != null,
                supportingText = priceError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // This section allows the owner to control truck availability.
            Text(
                text = "Availability",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Available for bookings",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Turn off to temporarily hide this truck from clients",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // I used a Switch to turn truck availability on or off.
                Switch(
                    checked = isAvailable,
                    onCheckedChange = { isAvailable = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // This button validates the form and adds the truck.
            // It becomes disabled after a successful submission.
            Button(
                onClick = { validateAndSubmit() },
                enabled = addedTruck == null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (addedTruck == null) "Add Truck" else "Truck Added")
            }

            // This button returns to the dashboard.
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (addedTruck == null) "Cancel" else "Back to Dashboard")
            }

            // I display a success card after the truck has been added.
            val truck = addedTruck
            if (truck != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Truck added!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Displays the details of the newly added truck.
                        Text(
                            text = "${truck.type} · ${truck.capacityKg} kg · " +
                                    "€${truck.pricePerDay.toInt()}/day",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Shows whether the new truck is available.
                        Text(
                            text = if (truck.isAvailable) "Available" else "Unavailable",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
