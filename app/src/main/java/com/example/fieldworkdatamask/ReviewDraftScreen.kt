package com.example.fieldworkdatamask

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDraftScreen(
    draft: DigitizationDraft,
    onSave: (FieldEntry) -> Unit,
    onCancel: () -> Unit
) {
    var scientificName by remember { mutableStateOf(draft.parsedRecord.scientificName ?: "") }
    var family by remember { mutableStateOf(draft.parsedRecord.family ?: "") }
    var recordedBy by remember { mutableStateOf(draft.parsedRecord.recordedBy ?: "") }
    var recordNumber by remember { mutableStateOf(draft.parsedRecord.recordNumber ?: "") }
    var locality by remember { mutableStateOf(draft.parsedRecord.locality ?: "") }
    var eventDate by remember { mutableStateOf(draft.parsedRecord.eventDate ?: "") }
    var habitat by remember { mutableStateOf(draft.parsedRecord.habitat ?: "") }
    var decimalLatitude by remember { mutableStateOf(draft.parsedRecord.decimalLatitude ?: "") }
    var decimalLongitude by remember { mutableStateOf(draft.parsedRecord.decimalLongitude ?: "") }
    var minimumElevationInMeters by remember { mutableStateOf(draft.parsedRecord.minimumElevationInMeters ?: "") }
    var lifeForm by remember { mutableStateOf(draft.parsedRecord.lifeForm ?: "Tree") }
    var organismQuantity by remember { mutableStateOf(draft.parsedRecord.organismQuantity ?: "") }
    var flowerPresent by remember { mutableStateOf(draft.parsedRecord.flowerPresent ?: false) }
    var fruitPresent by remember { mutableStateOf(draft.parsedRecord.fruitPresent ?: false) }
    var occurrenceRemarks by remember { mutableStateOf(draft.parsedRecord.occurrenceRemarks ?: draft.parsedRecord.notes ?: draft.rawExtractedText) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Review Digitized Note", style = MaterialTheme.typography.headlineMedium)

        AsyncImage(
            model = File(draft.sourceImagePath),
            contentDescription = "Source Note",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit
        )

        OutlinedTextField(
            value = scientificName,
            onValueChange = { scientificName = it },
            label = { Text("Scientific Name (DwC)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = family,
            onValueChange = { family = it },
            label = { Text("Family (DwC)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = recordedBy,
            onValueChange = { recordedBy = it },
            label = { Text("Recorded By (DwC)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = recordNumber,
            onValueChange = { recordNumber = it },
            label = { Text("Record Number (DwC)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = locality,
            onValueChange = { locality = it },
            label = { Text("Locality (DwC)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = eventDate,
            onValueChange = { eventDate = it },
            label = { Text("Event Date (DwC)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = habitat,
            onValueChange = { habitat = it },
            label = { Text("Habitat (DwC)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = decimalLatitude,
                onValueChange = { decimalLatitude = it },
                label = { Text("Latitude (DwC)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = decimalLongitude,
                onValueChange = { decimalLongitude = it },
                label = { Text("Longitude (DwC)") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = minimumElevationInMeters,
            onValueChange = { minimumElevationInMeters = it },
            label = { Text("Elevation (m) (DwC)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lifeForm,
            onValueChange = { lifeForm = it },
            label = { Text("Life Form") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = organismQuantity,
            onValueChange = { organismQuantity = it },
            label = { Text("Organism Quantity / Height") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = flowerPresent, onCheckedChange = { flowerPresent = it })
                Text("Flower")
            }
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = fruitPresent, onCheckedChange = { fruitPresent = it })
                Text("Fruit")
            }
        }

        OutlinedTextField(
            value = occurrenceRemarks,
            onValueChange = { occurrenceRemarks = it },
            label = { Text("Occurrence Remarks (DwC)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Discard")
            }
            Button(
                onClick = {
                    val entry = FieldEntry(
                        scientificName = scientificName,
                        family = family,
                        recordedBy = recordedBy,
                        recordNumber = recordNumber,
                        locality = locality,
                        eventDate = eventDate,
                        habitat = habitat,
                        occurrenceRemarks = occurrenceRemarks,
                        sourceNoteImagePath = draft.sourceImagePath,
                        rawExtractedText = draft.rawExtractedText,
                        extractionMethod = "gemini",
                        reviewStatus = "reviewed",
                        decimalLatitude = decimalLatitude,
                        decimalLongitude = decimalLongitude,
                        minimumElevationInMeters = minimumElevationInMeters,
                        lifeForm = lifeForm,
                        organismQuantity = organismQuantity,
                        collectedVoucher = recordNumber.isNotBlank(),
                        catalogNumber = recordNumber,
                        fruitPresent = fruitPresent,
                        flowerPresent = flowerPresent,
                        verbatimLocality = draft.parsedRecord.verbatimLocality ?: "",
                        verbatimCoordinates = draft.parsedRecord.verbatimCoordinates ?: "",
                        reproductiveCondition = draft.parsedRecord.reproductiveCondition ?: "",
                        associatedTaxa = draft.parsedRecord.associatedTaxa ?: ""
                    )
                    onSave(entry)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Record")
            }
        }
    }
}
