package com.aesc.proyectofinaldesarrollomovil.provider.firebase.models

data class Locations(
    val latitude: Double = 0.00,
    val longitude: Double = 0.00,
    val createdAt: Long = 0L,
    val userText : String = "",
    val createdBy: User = User()
)