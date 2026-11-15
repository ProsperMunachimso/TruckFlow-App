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

// I adapted this booking form from the CreateBooking page in my TruckFlow web app.
// Instead of opening a separate Activity, I made it a Compose navigation destination.
// The onNavigateBack callback lets MainActivity control navigation without passing
// the NavController directly into this screen.
// For now the form validates input and displays a confirmation; it does not save
// a booking to the backend or add it to the dashboard's sample bookings.
@OptIn(ExperimentalMaterial3Api::class) // I used @OptIn because this screen uses Material 3 components
// that may be marked as experimental in the library version.

@Composable
fun CreateBookingScreen(onNavigateBack: () -> Unit) {

    // I used mutableStateOf for each of the fields below so Compose can update the displayed value as the user types.
    // I used remember because it retains the value during normal recomposition.
    // Keeping separate variables makes it easier to validate each field individually.
    var pickup by remember { mutableStateOf("") }
    var delivery by remember { mutableStateOf("") }
    var cargoType by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var pickupDate by remember { mutableStateOf("") }

    // The two checkboxes represent optional labour assistance requested by the client.
    // Their Boolean values change independently when the user ticks each checkbox.
    var needLoading by remember { mutableStateOf(false) }
    var needUnloading by remember { mutableStateOf(false) }
    var instructions by remember { mutableStateOf("") }

    // I store validation errors separately from the form values. A null error means
    // the field has no message to show, otherwise its error text appears below it.
    // The errors are cleared when the user edits the corresponding input.
    var pickupError by remember { mutableStateOf<String?>(null) }
    var deliveryError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }
    var dimensionsError by remember { mutableStateOf<String?>(null) }
    // The visible date is stored as text, but I also keep its timestamp so I can
    // compare calendar dates without trying to parse the displayed string.
    var pickupDateMillis by remember { mutableStateOf<Long?>(null) }

    // This flag controls whether the confirmation card is visible after validation.
    // It is only a local UI result, no booking is sent to the server yet.
    var showSuccess by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // I used Android's DatePickerDialog so users can select a date rather than typing one manually.
    fun showDatePicker() {
        val cal = Calendar.getInstance()

        // I ensured that today for the user is selectable by resetting the current time to midnight.
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // The callback runs when the user confirms a date. Calendar months start
        // at zero, so I add 1 when formatting the date for display.
        val dialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                pickupDate = "${day}/${month + 1}/$year"

                // I save the selected day at midnight for a date-only comparison.
                pickupDateMillis = Calendar.getInstance().apply {
                    set(year, month, day, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                dateError = null
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        // Setting minDate prevents the picker from selecting earlier dates.
        // I also check the date during submission in case the saved value is stale.
        dialog.datePicker.minDate = startOfToday.timeInMillis
        dialog.show()
    }

    // I run validation when Submit Booking is pressed, rather than showing errors
    // before the user has had a chance to fill out the form.
    fun validateAndSubmit() {
        // Pickup, delivery and pickup date are required. isBlank also catches
        // strings made entirely of spaces, not just completely empty strings.
        pickupError = if (pickup.isBlank()) "Pickup is required" else null
        deliveryError = if (delivery.isBlank()) "Delivery is required" else null
        dateError = if (pickupDate.isBlank()) "Pickup date is required" else null

        // I trim spaces and ignore capitalisation when comparing the locations,
        // so "Dublin" and " dublin " are treated as the same entered location.
        if (!pickup.isBlank() && !delivery.isBlank() &&
            pickup.trim().equals(delivery.trim(), ignoreCase = true)
        ) {
            deliveryError = "Delivery must be different from pickup"
        }

        // I repeat the date check on submission as a safeguard if the date
        // becomes outdated after it was selected, such as when the day changes.
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        if (pickupDateMillis != null && pickupDateMillis!! < todayStart) {
            dateError = "Pickup date cannot be in the past"
        }

        // Weight is optional here, but a supplied value must be a whole number
        // between 1 and 50,000 kg. toIntOrNull avoids crashing on invalid text.
        weightError = if (weight.isNotBlank()) {
            val w = weight.toIntOrNull()
            if (w == null || w <= 0 || w > 50_000) "Weight must be 1-50,000 kg" else null
        } else null

        // I validate the three cargo dimensions together to avoid repeating the same check for length, width and height.
        // This implementation requires all three to be valid whole numbers
        // between 1 and 1000 cm and I made sure partly completed set shows an error.
        val dims = listOf(length, width, height)
        dimensionsError = if (dims.any { it.isNotBlank() }) {
            val parsed = dims.map { it.trim().toIntOrNull() }
            when {
                parsed.any { it == null } ->
                    "Dimensions must be numbers"
                parsed.any { it!! <= 0 || it > 1000 } ->
                    "Dimensions must be 1-1000 cm"
                else -> null
            }
        } else null

        // The form is considered valid only if none of the checks produced an error.
        // Setting showSuccess triggers Compose to display the confirmation card.
        val isValid = pickupError == null &&
                deliveryError == null &&
                dateError == null &&
                weightError == null &&
                dimensionsError == null

        if (isValid) showSuccess = true
    }

    // Scaffold gives the booking screen a top app bar and a content area.
    // The back arrow uses the callback supplied by the NavHost destination.
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
        // The booking form is taller than a typical phone screen, so I used
        // verticalScroll to keep every input and the submit button reachable.
        // Applying the Scaffold padding prevents content overlapping the app bar.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Route details come first because the pickup and destination are
            // essential for describing the transport request.
            SectionHeader("Route")
            OutlinedTextField(
                value = pickup,
                // The field updates its state on each edit and clears an old error.
                onValueChange = { pickup = it; pickupError = null },
                label = { Text("Pickup Location *") },
                // isError changes the field styling, while supportingText displays
                // the actual validation message when there is one.
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

            // Cargo information is grouped separately from the route.
            // The type, weight and dimensions are currently optional, but supplied numbers are checked before the confirmation is displayed.
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
            // I placed the three dimensions in one Row to save vertical space.
            // weight(1f) gives each text field an equal share of the row width.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = length,
                    onValueChange = { length = it; dimensionsError = null },
                    label = { Text("Length") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = width,
                    onValueChange = { width = it; dimensionsError = null },
                    label = { Text("Width") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it; dimensionsError = null },
                    label = { Text("Height") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // One shared error message appears below the dimensions row instead
            // of showing the same message under all three small text fields.
            if (dimensionsError != null) {
                Text(
                    text = dimensionsError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            HorizontalDivider()

            // The date field is read-only because I want users to choose a date
            // through the picker, avoiding inconsistent date formats from typing.
            SectionHeader("Schedule")
            OutlinedTextField(
                value = pickupDate,
                onValueChange = { /* read-only, populated by showDatePicker */ },
                label = { Text("Pickup Date *") },
                isError = dateError != null,
                supportingText = dateError?.let { { Text(it) } },
                readOnly = true,
                trailingIcon = {
                    // The calendar icon opens the platform date picker.
                    IconButton(onClick = { showDatePicker() }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            // Clients can request help with loading, unloading, or both.
            // These selections are currently kept in local state only.
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

            // I used a multi-line field for special instructions so clients
            // can provide extra details about the cargo or collection.
            SectionHeader("Notes")
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Special Instructions") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // The primary button runs the validation function.
             Button(onClick = { validateAndSubmit() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Booking")
            }
            OutlinedButton(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel") // The Cancel button uses the same back-navigation callback as the toolbar arrow.
            }

            // This card only confirms that the entered values passed local validation.
            // It does not create a booking record or call an API.
            // I display the route and date so the user can review their input.
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
// I made the section heading reusable so Route, Cargo, Schedule, Assistance
// and Notes use the same typography without duplicating the Text styling.
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}
