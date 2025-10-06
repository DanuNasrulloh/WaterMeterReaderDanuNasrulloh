package com.flindigital.watermeter.data.model

data class Customer(
    val userId: String,
    val userName: String,
    val date: String, // e.g., "Senin, 06 Okt 2025"
    val time: String, // e.g., "09:41"
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double,
    val isRecorded: Boolean
)
