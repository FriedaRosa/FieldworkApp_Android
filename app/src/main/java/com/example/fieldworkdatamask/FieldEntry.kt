package com.example.fieldworkdatamask

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class FieldEntry(
    val date: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val time: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
    val locationName: String,
    val gpsLatitude: String,
    val gpsLongitude: String,
    val altitude: String,
    val habitatDescription: String,
    val biomeDescription: String,
    val collectors: String,
    val speciesName: String,
    val lifeForm: String,
    val height: String,
    val collectedVoucher: Boolean,
    val voucherId: String,
    val fruit: Boolean,
    val flower: Boolean,
    val notes: String,
    val photoPath: String = ""
) {
    fun toCsvRow(): String {
        return "\"$date\",\"$time\",\"$locationName\",\"$gpsLatitude\",\"$gpsLongitude\",\"$altitude\",\"$habitatDescription\",\"$biomeDescription\",\"$collectors\",\"$speciesName\",\"$lifeForm\",\"$height\",\"$collectedVoucher\",\"$voucherId\",\"$fruit\",\"$flower\",\"$notes\",\"$photoPath\"\n"
    }

    companion object {
        fun getCsvHeader(): String {
            return "Date,Time,Location Name,Latitude,Longitude,Altitude,Habitat,Biome,Collectors,Species,Life Form,Height,Collected Voucher,Voucher ID,Fruit,Flower,Notes,Photo Path\n"
        }

        fun fromCsvRow(row: String): FieldEntry {
            val parts = row.split("\",\"").map { it.trim('\"', '\n') }
            return FieldEntry(
                date = parts[0],
                time = parts[1],
                locationName = parts[2],
                gpsLatitude = parts[3],
                gpsLongitude = parts[4],
                altitude = parts[5],
                habitatDescription = parts[6],
                biomeDescription = parts[7],
                collectors = parts[8],
                speciesName = parts[9],
                lifeForm = parts[10],
                height = parts[11],
                collectedVoucher = parts[12].toBoolean(),
                voucherId = parts[13],
                fruit = parts[14].toBoolean(),
                flower = parts[15].toBoolean(),
                notes = parts[16],
                photoPath = if (parts.size > 17) parts[17] else ""
            )
        }
    }
}
