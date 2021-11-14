package com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos

import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.Locations
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LocationDao {
    private val db = FirebaseFirestore.getInstance()
    val locationCollection = db.collection("locations")
    private val auth = Firebase.auth

    fun addLocation(
        latitude: Double = 0.00,
        longitude: Double = 0.00,
        nameLocation: String
    ) {
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch {
            val userDao = UserDao()
            val user = userDao.getUserByid(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            val location = Locations(latitude, longitude, currentTime, nameLocation, user)
            locationCollection.document().set(location)
        }
    }

    fun deleteLocation(idLocation: String) {
        locationCollection.document(idLocation).delete()
    }
}