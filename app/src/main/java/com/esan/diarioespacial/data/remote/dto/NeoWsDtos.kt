package com.esan.diarioespacial.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTOs del endpoint NeoWs Feed: reflejan el JSON tal como lo envía la NASA.
// Todos los campos son nullables a propósito: Gson no respeta la nulabilidad de
// Kotlin y, si un campo no llega, deja null aunque el tipo diga String. Con un
// tipo no nulo la app se caería recién al usar el valor, lejos del origen del error.

data class NeoFeedResponse(
    // La API agrupa los asteroides por fecha: las claves del objeto son fechas
    // ("2026-11-14") y no nombres fijos, por eso se modela como Map y no como clase.
    @SerializedName("near_earth_objects")
    val nearEarthObjects: Map<String, List<NeoDto>?>?
)

data class NeoDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("is_potentially_hazardous_asteroid")
    val isPotentiallyHazardousAsteroid: Boolean?,
    @SerializedName("estimated_diameter")
    val estimatedDiameter: EstimatedDiameterDto?,
    @SerializedName("close_approach_data")
    val closeApproachData: List<CloseApproachDto>?
)

// El diámetro viene en varias unidades (kilometers, meters, miles, feet);
// solo se declara "meters" porque Gson ignora los campos que no están en la clase.
data class EstimatedDiameterDto(
    @SerializedName("meters") val meters: DiameterRangeDto?
)

data class DiameterRangeDto(
    @SerializedName("estimated_diameter_min") val estimatedDiameterMin: Double?,
    @SerializedName("estimated_diameter_max") val estimatedDiameterMax: Double?
)

data class CloseApproachDto(
    @SerializedName("close_approach_date") val closeApproachDate: String?,
    @SerializedName("epoch_date_close_approach") val epochDateCloseApproach: Long?,
    @SerializedName("relative_velocity") val relativeVelocity: RelativeVelocityDto?,
    @SerializedName("miss_distance") val missDistance: MissDistanceDto?
)

// Velocidad y distancia llegan como texto ("45123.4567"), no como número,
// por eso son String aquí y se convierten a Double en el mapper.
data class RelativeVelocityDto(
    @SerializedName("kilometers_per_hour") val kilometersPerHour: String?
)

data class MissDistanceDto(
    @SerializedName("kilometers") val kilometers: String?
)
