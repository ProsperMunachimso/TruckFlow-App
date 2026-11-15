package com.griffith.truckflowapp.screens

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Calendar

// Booking form. This is a NavHost destination (not a separate Activity),
// so navigation back to the dashboard goes through onNavigateBack rather
// than finish(). The form mirrors CreateBooking.jsx from the web app:
// route, cargo, schedule, assistance, notes. On M2 the submit button
// validates and shows a success card inline; M3 will POST to
// /api/bookings and navigate to BookingDetailsActivity with the new ID.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingScreen(onNavigateBack: () -> Unit) {

    // ── Form state ────────────────────────────────────────
    var pickup by remember { mutableStateOf("") }
    var delivery by remember { mutableStateOf("") }
    var cargoType by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var pickupDate by remember { mutableStateOf("") }
    var needLoading by remember { mutableStateOf(false) }
    var needUnloading by remember { mutableStateOf(false) }
    var instructions by remember { mutableStateOf("") }

    // ── Per-field error state ─────────────────────────────
    // null = no error; non-null = message shown under the field.
    // Errors are cleared as the user types so red fields disappear once
    // they fix the problem.
    var pickupError by remember { mutableStateOf<String?>(null) }
    var deliveryError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }

    // ── Submit outcome ────────────────────────────────────
    var showSuccess by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Opens the platform date picker. Using the platform dialog avoids
    // adding the Material DatePicker dependency for one field.
    fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                pickupDate = "${day}/${month + 1}/$year"
                dateError = null
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Validation runs only on submit — not on every keystroke — so the
    // user is not shown red fields before they have typed anything.
    fun validateAndSubmit() {
        pickupError = if (pickup.isBlank()) "Pickup is required" else null
        deliveryError = if (delivery.isBlank()) "Delivery is required" else null
        dateError = if (pickupDate.isBlank()) "Pickup date is required" else null
        weightError = if (weight.isNotBlank()) {
            val w = weight.toIntOrNull()
            if (w == null || w <= 0 || w > 50_000) "Weight must be 1-50,000 kg" else null
        } else null

        val isValid = pickupError == null && deliveryError == null &&
                dateError == null && weightError == null
        if (isValid) showSuccess = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book a Truck") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        // The form is taller than one screen; verticalScroll makes the
        // whole column scroll so the submit button stays reachable.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Route ─────────────────────────────────────
            SectionHeader("Route")
            OutlinedTextField(
                value = pickup,
                onValueChange = { pickup = it; pickupError = null },
                label = { Text("Pickup Location *") },
                isError = pickupError != null,
                supportingText = pickupError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = delivery,
                onValueChange = { delivery = it; deliveryError = null },
                label = { Text("Delivery Location *") },
                isError = deliveryError != null,
                supportingText = deliveryError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // ── Cargo ─────────────────────────────────────
            SectionHeader("Cargo")
            OutlinedTextField(
                value = cargoType,
                onValueChange = { cargoType = it },
                label = { Text("Cargo Type") },
                placeholder = { Text("e.g. Palletized") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it; weightError = null },
                label = { Text("Weight (kg)") },
                isError = weightError != null,
                supportingText = weightError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Dimensions (cm)", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = length,
                    onValueChange = { length = it },
                    label = { Text("Length") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = width,
                    onValueChange = { width = it },
                    label = { Text("Width") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            // ── Schedule ──────────────────────────────────
            SectionHeader("Schedule")
            OutlinedTextField(
                value = pickupDate,
                onValueChange = { /* read-only; populated by showDatePicker */ },
                label = { Text("Pickup Date *") },
                isError = dateError != null,
                supportingText = dateError?.let { { Text(it) } },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker() }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // ── Assistance ────────────────────────────────
            SectionHeader("Assistance")
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = needLoading, onCheckedChange = { needLoading = it })
                Text("Need loading assistance?")
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = needUnloading, onCheckedChange = { needUnloading = it })
                Text("Need unloading assistance?")
            }

            HorizontalDivider()

            // ── Notes ─────────────────────────────────────
            SectionHeader("Notes")
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Special Instructions") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Actions ───────────────────────────────────
            Button(onClick = { validateAndSubmit() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Booking")
            }
            OutlinedButton(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }

            // ── Success card ──────────────────────────────
            // Replaces the API call for M2. In M3 this will POST to
            // /api/bookings and navigate to BookingDetailsActivity with
            // the returned booking ID.
            if (showSuccess) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Booking submitted!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$pickup → $delivery\n$pickupDate",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                            Text("Back to Dashboard")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}