package com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos

import android.util.Log
import com.aesc.proyectofinaldesarrollomovil.extension.toast
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesKey
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesProvider
import com.aesc.proyectofinaldesarrollomovil.utils.Utils
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

    fun getCurrentUser(
        uId: String,
        success: (response: User) -> Unit,
        failure: (message: String) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            userCollection.document(uId).get().addOnSuccessListener { task ->
                val user: User? = task.toObject(User::class.java)
                success(user!!)
            }.addOnFailureListener {
                failure(it.message.toString())
            }
        }
    }

    fun updateUserInfo(
        user: User?,
        success: (response: User) -> Unit,
        failure: (message: String) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            userCollection.document(user!!.uid).set(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getUserByid(user.uid).addOnCompleteListener {
                        if (task.isSuccessful) {
                            success(user)
                        } else {
                            failure(task.exception.toString())
                        }
                    }.addOnFailureListener {
                        failure(it.message.toString())
                    }
                } else {
                    failure(task.exception.toString())
                }
            }.addOnFailureListener {
                failure(it.message.toString())
            }
        }
    }
}