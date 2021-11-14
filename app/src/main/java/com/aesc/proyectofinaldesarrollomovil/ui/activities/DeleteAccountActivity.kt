package com.aesc.proyectofinaldesarrollomovil.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityDeleteAccountBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DeleteAccountActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDeleteAccountBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.deleteAccountAppCompatButton.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        val password = binding.tiePassword.text.toString()
        deleteAccount(password)
    }

    private fun deleteAccount(password: String) {
        val user = auth.currentUser

        if (user != null) {
            val email = user.email
            val credential = EmailAuthProvider
                .getCredential(email!!, password)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.delete()
                            .addOnCompleteListener { taskDeleteAcount ->
                                if (taskDeleteAcount.isSuccessful) {
                                    Toast.makeText(
                                        this, "Se elminó tu cuenta.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    signOut()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this, "La contraseña ingresada es incorrecta.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun signOut() {
        auth.signOut()
        goToActivityF<LoginActivity>()
    }
}