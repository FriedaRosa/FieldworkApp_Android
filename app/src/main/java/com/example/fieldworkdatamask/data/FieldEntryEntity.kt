package com.example.fieldworkdatamask.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fieldworkdatamask.FieldEntry

@Entity(tableName = "field_entries")
data class FieldEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventDate: String,
    val eventTime: String,
    val scientificName: String,
    val family: String,
    val recordedBy: String,
    val recordNumber: String,
    val locality: String,
    val verbatimLocality: String,
    val decimalLatitude: String,
    val decimalLongitude: String,
    val verbatimCoordinates: String,
    val minimumElevationInMeters: String,
    val habitat: String,
    val reproductiveCondition: String,
    val occurrenceRemarks: String,
    val associatedTaxa: String,
    val lifeForm: String,
    val organismQuantity: String,
    val photoPath: String,
    val sourceNoteImagePath: String,
    val rawExtractedText: String,
    val extractionMethod: String,
    val reviewStatus: String,
    val parseWarnings: String,
    val flowerPresent: Boolean,
    val fruitPresent: Boolean,
    val collectedVoucher: Boolean,
    val catalogNumber: String
) {
    fun toDomain(): FieldEntry = FieldEntry(
        eventDate = eventDate,
        eventTime = eventTime,
        scientificName = scientificName,
        family = family,
        recordedBy = recordedBy,
        recordNumber = recordNumber,
        locality = locality,
        verbatimLocality = verbatimLocality,
        decimalLatitude = decimalLatitude,
        decimalLongitude = decimalLongitude,
        verbatimCoordinates = verbatimCoordinates,
        minimumElevationInMeters = minimumElevationInMeters,
        habitat = habitat,
        reproductiveCondition = reproductiveCondition,
        occurrenceRemarks = occurrenceRemarks,
        associatedTaxa = associatedTaxa,
        lifeForm = lifeForm,
        organismQuantity = organismQuantity,
        photoPath = photoPath,
        sourceNoteImagePath = sourceNoteImagePath,
        rawExtractedText = rawExtractedText,
        extractionMethod = extractionMethod,
        reviewStatus = reviewStatus,
        parseWarnings = parseWarnings,
        flowerPresent = flowerPresent,
        fruitPresent = fruitPresent,
        collectedVoucher = collectedVoucher,
        catalogNumber = catalogNumber
    )

    companion object {
        fun fromDomain(entry: FieldEntry): FieldEntryEntity = FieldEntryEntity(
            eventDate = entry.eventDate,
            eventTime = entry.eventTime,
            scientificName = entry.scientificName,
            family = entry.family,
            recordedBy = entry.recordedBy,
            recordNumber = entry.recordNumber,
            locality = entry.locality,
            verbatimLocality = entry.verbatimLocality,
            decimalLatitude = entry.decimalLatitude,
            decimalLongitude = entry.decimalLongitude,
            verbatimCoordinates = entry.verbatimCoordinates,
            minimumElevationInMeters = entry.minimumElevationInMeters,
            habitat = entry.habitat,
            reproductiveCondition = entry.reproductiveCondition,
            occurrenceRemarks = entry.occurrenceRemarks,
            associatedTaxa = entry.associatedTaxa,
            lifeForm = entry.lifeForm,
            organismQuantity = entry.organismQuantity,
            photoPath = entry.photoPath,
            sourceNoteImagePath = entry.sourceNoteImagePath,
            rawExtractedText = entry.rawExtractedText,
            extractionMethod = entry.extractionMethod,
            reviewStatus = entry.reviewStatus,
            parseWarnings = entry.parseWarnings,
            flowerPresent = entry.flowerPresent,
            fruitPresent = entry.fruitPresent,
            collectedVoucher = entry.collectedVoucher,
            catalogNumber = entry.catalogNumber
        )
    }
}
