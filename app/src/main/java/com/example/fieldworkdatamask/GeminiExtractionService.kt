package com.example.fieldworkdatamask

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class GeminiExtractionResult(
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

class GeminiExtractionService(apiKey: String) {
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun extractStructuredData(rawText: String): ParsedFieldRecord {
        val prompt = """
            Extract botanical field note data from the following raw OCR text into a structured JSON format following Darwin Core terms where possible.
            Raw Text:
            ${rawText}
            
            Return a JSON object with the following fields:
            recordedBy, recordNumber, eventDate (YYYY-MM-DD), eventTime (HH:mm), scientificName, family, 
            locality, verbatimLocality, decimalLatitude, decimalLongitude, verbatimCoordinates, minimumElevationInMeters, 
            habitat, biome, lifeForm, organismQuantity (e.g. height), reproductiveCondition, flowerPresent (boolean), 
            fruitPresent (boolean), occurrenceRemarks, associatedTaxa, notes.
            
            If a field is not found, return null for that field.
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            val responseText = response.text ?: return ParsedFieldRecord()
            val result = json.decodeFromString<GeminiExtractionResult>(responseText)
            
            ParsedFieldRecord(
                recordedBy = result.recordedBy,
                recordNumber = result.recordNumber,
                eventDate = result.eventDate,
                eventTime = result.eventTime,
                scientificName = result.scientificName,
                family = result.family,
                locality = result.locality,
                verbatimLocality = result.verbatimLocality,
                decimalLatitude = result.decimalLatitude,
                decimalLongitude = result.decimalLongitude,
                verbatimCoordinates = result.verbatimCoordinates,
                minimumElevationInMeters = result.minimumElevationInMeters,
                habitat = result.habitat,
                biome = result.biome,
                lifeForm = result.lifeForm,
                organismQuantity = result.organismQuantity,
                reproductiveCondition = result.reproductiveCondition,
                flowerPresent = result.flowerPresent,
                fruitPresent = result.fruitPresent,
                occurrenceRemarks = result.occurrenceRemarks,
                associatedTaxa = result.associatedTaxa,
                notes = result.notes
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ParsedFieldRecord()
        }
    }
}
