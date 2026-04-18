package com.example.fieldworkdatamask

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.fieldworkdatamask.ui.theme.FieldworkDataMaskTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.File
import java.io.FileOutputStream
import java.io.BufferedReader
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FieldworkDataMaskTheme {
                var currentScreen by remember { mutableStateOf("entry") }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                                label = { Text("Entry") },
                                selected = currentScreen == "entry",
                                onClick = { currentScreen = "entry" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.List, contentDescription = null) },
                                label = { Text("Data") },
                                selected = currentScreen == "data",
                                onClick = { currentScreen = "data" }
                            )
                        }
                    }
                ) { innerPadding ->
                    if (currentScreen == "entry") {
                        DataEntryScreen(modifier = Modifier.padding(innerPadding))
                    } else {
                        DataTableScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun DataTableScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val fileName = "field_data.csv"
    val file = File(context.getExternalFilesDir(null), fileName)
    
    val entries = remember(file) {
        if (file.exists()) {
            try {
                BufferedReader(FileReader(file)).useLines { lines ->
                    lines.drop(1).map { FieldEntry.fromCsvRow(it) }.toList()
                }
            } catch (e: Exception) {
                emptyList<FieldEntry>()
            }
        } else {
            emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Previous Entries", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        if (entries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No entries yet")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                entries.reversed().forEach { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (entry.photoPath.isNotBlank()) {
                                AsyncImage(
                                    model = File(entry.photoPath),
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp).padding(end = 12.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = entry.speciesName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = entry.date, style = MaterialTheme.typography.bodySmall)
                                }
                                Text(text = "Location: ${entry.locationName}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "GPS: ${entry.gpsLatitude}, ${entry.gpsLongitude}", style = MaterialTheme.typography.bodySmall)
                                if (entry.notes.isNotBlank()) {
                                    Text(text = "Notes: ${entry.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share CSV"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export All to CSV")
                }
                
                OutlinedButton(
                    onClick = {
                        if (file.delete()) {
                            Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All Data")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DataEntryScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var locationName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var altitude by remember { mutableStateOf("") }
    var habitatDescription by remember { mutableStateOf("") }
    var biomeDescription by remember { mutableStateOf("") }
    var collectors by remember { mutableStateOf("") }
    var speciesName by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    var lifeForm by remember { mutableStateOf("Tree") }
    val lifeFormOptions = listOf("Tree", "Shrub", "Herb", "Other")
    var lifeFormExpanded by remember { mutableStateOf(false) }

    var collectedVoucher by remember { mutableStateOf(false) }
    var voucherId by remember { mutableStateOf("") }
    var fruit by remember { mutableStateOf(false) }
    var flower by remember { mutableStateOf(false) }

    var photoPath by remember { mutableStateOf("") }
    var capturedImageFile by remember { mutableStateOf<File?>(null) }
    
    val fileName = "field_data.csv"
    val file = File(context.getExternalFilesDir(null), fileName)

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoPath = capturedImageFile?.absolutePath ?: ""
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val photoFile = File(
                context.getExternalFilesDir("photos"),
                "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
            )
            capturedImageFile = photoFile
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
            // Success
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Field Data Entry", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = locationName,
            onValueChange = { locationName = it },
            label = { Text("Location Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Photo Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (photoPath.isNotEmpty()) {
                    AsyncImage(
                        model = File(photoPath),
                        contentDescription = "Captured Photo",
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Button(
                    onClick = {
                        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            val photoFile = File(
                                context.getExternalFilesDir("photos"),
                                "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
                            )
                            capturedImageFile = photoFile
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (photoPath.isEmpty()) "Take Photo" else "Retake Photo")
                }
            }
        }

        // GPS and Altitude Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Lat") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Lon") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                OutlinedTextField(
                    value = altitude,
                    onValueChange = { altitude = it },
                    label = { Text("Altitude (m)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                Button(
                    onClick = {
                        val fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                                .addOnSuccessListener { location ->
                                    if (location != null) {
                                        latitude = location.latitude.toString()
                                        longitude = location.longitude.toString()
                                        altitude = if (location.hasAltitude()) location.altitude.toInt().toString() else "N/A"
                                    } else {
                                        Toast.makeText(context, "Could not get location.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Get Current GPS & Altitude")
                }
            }
        }

        OutlinedTextField(
            value = habitatDescription,
            onValueChange = { habitatDescription = it },
            label = { Text("Habitat Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = biomeDescription,
            onValueChange = { biomeDescription = it },
            label = { Text("Biome Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = collectors,
            onValueChange = { collectors = it },
            label = { Text("Collectors") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = speciesName,
            onValueChange = { speciesName = it },
            label = { Text("Species Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Life Form Dropdown
        ExposedDropdownMenuBox(
            expanded = lifeFormExpanded,
            onExpandedChange = { lifeFormExpanded = !lifeFormExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = lifeForm,
                onValueChange = {},
                readOnly = true,
                label = { Text("Life Form") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lifeFormExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = lifeFormExpanded,
                onDismissRequest = { lifeFormExpanded = false }
            ) {
                lifeFormOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            lifeForm = selectionOption
                            lifeFormExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (m)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        // Voucher, Fruit, Flower Switches
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Collected Voucher", modifier = Modifier.weight(1f))
                Switch(checked = collectedVoucher, onCheckedChange = { collectedVoucher = it })
            }
            if (collectedVoucher) {
                OutlinedTextField(
                    value = voucherId,
                    onValueChange = { voucherId = it },
                    label = { Text("Voucher ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Fruit Present", modifier = Modifier.weight(1f))
                Switch(checked = fruit, onCheckedChange = { fruit = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Flower Present", modifier = Modifier.weight(1f))
                Switch(checked = flower, onCheckedChange = { flower = it })
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Button(
            onClick = {
                if (locationName.isBlank() || speciesName.isBlank()) {
                    Toast.makeText(context, "Location and Species are required", Toast.LENGTH_SHORT).show()
                } else {
                    val entry = FieldEntry(
                        locationName = locationName,
                        gpsLatitude = latitude,
                        gpsLongitude = longitude,
                        altitude = altitude,
                        habitatDescription = habitatDescription,
                        collectors = collectors,
                        speciesName = speciesName,
                        lifeForm = lifeForm,
                        height = height,
                        collectedVoucher = collectedVoucher,
                        voucherId = if (collectedVoucher && voucherId.isBlank()) "V-" + System.currentTimeMillis() else voucherId,
                        fruit = fruit,
                        flower = flower,
                        notes = notes,
                        biomeDescription = biomeDescription,
                        photoPath = photoPath
                    )
                    saveToCsv(file, entry)
                    Toast.makeText(context, "Saved to ${file.name}", Toast.LENGTH_SHORT).show()
                    
                    // Reset fields
                    speciesName = ""
                    height = ""
                    notes = ""
                    collectedVoucher = false
                    voucherId = ""
                    fruit = false
                    flower = false
                    photoPath = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Entry")
        }

        HorizontalDivider()

        Button(
            onClick = {
                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share CSV"))
                } else {
                    Toast.makeText(context, "No data to share", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Share/Export CSV")
        }
        
        if (file.exists()) {
            Text(
                text = "File: ${file.name}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun saveToCsv(file: File, entry: FieldEntry) {
    val isNewFile = !file.exists()
    FileOutputStream(file, true).use { output ->
        if (isNewFile) {
            output.write(FieldEntry.getCsvHeader().toByteArray())
        }
        output.write(entry.toCsvRow().toByteArray())
    }
}
