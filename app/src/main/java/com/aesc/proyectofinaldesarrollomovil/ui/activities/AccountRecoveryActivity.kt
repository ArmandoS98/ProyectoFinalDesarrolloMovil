package com.aesc.proyectofinaldesarrollomovil.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityAccountRecoveryBinding
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivitySignInBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountRecoveryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAccountRecoveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSenEmail.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val email = binding.emailEditText.text.toString()
        Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this, "Se ha enviado un correo de verifiación.",
                    Toast.LENGTH_SHORT
                ).show()
                goToActivityF<LoginActivity>()
            } else {
                Toast.makeText(this, "Ingrese un email de una cuenta valido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}