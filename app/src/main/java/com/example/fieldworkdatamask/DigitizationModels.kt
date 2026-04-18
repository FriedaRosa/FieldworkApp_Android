package com.example.fieldworkdatamask

import kotlinx.serialization.Serializable

/**
 * High-level container for a note being digitized.
 */
data class DigitizationDraft(
    val sourceImagePath: String,
    val rawExtractedText: String,
    val parsedRecord: ParsedFieldRecord
)

@Serializable
data class ParsedFieldRecord(
    val recordId: String? = null,
    val recordedBy: String? = null,
    val recordNumber: String? = null,
    val eventDate: String? = null,
    val eventTime: String? = null,
    val scientificName: String? = null,
    val family: String? = null,
    val locality: String? = null,
    val verbatimLocality: String? = null,
    val decimalLatitude: String? = null,
    val decimalLongitude: String? = null,
    val verbatimCoordinates: String? = null,
    val minimumElevationInMeters: String? = null,
    val habitat: String? = null,
    val biome: String? = null,
    val lifeForm: String? = null,
    val organismQuantity: String? = null,
    val reproductiveCondition: String? = null,
    val flowerPresent: Boolean? = null,
    val fruitPresent: Boolean? = null,
    val occurrenceRemarks: String? = null,
    val associatedTaxa: String? = null,
    val notes: String? = null
)
