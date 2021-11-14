package com.aesc.proyectofinaldesarrollomovil.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityCheckEmailBinding
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivitySignInBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class CheckEmailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityCheckEmailBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        binding.veficateEmailAppCompatButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {}
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (user.isEmailVerified) {
                        goToActivityF<MainActivity>()
                    } else {
                        Toast.makeText(
                            this, "Por favor verifica tu correo.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                goToActivityF<MainActivity>()
            } else {
                sendEmailVerification()
            }
        }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this, "Se ha enviado un correo de verifiación.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}