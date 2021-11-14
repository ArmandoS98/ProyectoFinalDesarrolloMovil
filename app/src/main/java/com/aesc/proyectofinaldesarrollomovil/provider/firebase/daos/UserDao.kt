package com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos

import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDao {
    private val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection("users")

    fun addUser(user: User?) {
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                userCollection.document(user.uid).set(it)
            }
        }
    }

    fun getUserByid(uId: String): Task<DocumentSnapshot> {
        return userCollection.document(uId).get()
    }

    fun updateUserInfo(user: User?) {
        GlobalScope.launch(Dispatchers.IO) {
            userCollection.document(user!!.uid).set(user)
        }
    }
}