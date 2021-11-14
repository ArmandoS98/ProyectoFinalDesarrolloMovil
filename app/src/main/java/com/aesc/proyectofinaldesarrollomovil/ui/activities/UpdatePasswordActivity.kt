package com.aesc.proyectofinaldesarrollomovil.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityUpdatePasswordBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class UpdatePasswordActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUpdatePasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.changePasswordAppCompatButton.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        val passwordRegex = Pattern.compile(
            "^" +
                    "(?=.*[-@#$%^&+=])" +     // Al menos 1 carácter especial
                    ".{6,}" +                // Al menos 4 caracteres
                    "$"
        )

        val currentPassword = binding.tieCurrentPassword.text.toString()
        val newPassword = binding.tieNewPassword.text.toString()
        val repeatPassword = binding.tieRepeatPassword.text.toString()

        if (newPassword.isEmpty() || !passwordRegex.matcher(newPassword).matches()) {
            Toast.makeText(
                this, "La contraseña es debil.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (newPassword != repeatPassword) {
            Toast.makeText(
                this, "Confirma la contraseña.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            chagePassword(currentPassword, newPassword)
        }
    }

    private fun chagePassword(current: String, password: String) {
        val user = auth.currentUser

        if (user != null) {
            val email = user.email
            val credential = EmailAuthProvider
                .getCredential(email!!, current)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        user.updatePassword(password)
                            .addOnCompleteListener { taskUpdatePassword ->
                                if (taskUpdatePassword.isSuccessful) {
                                    Toast.makeText(
                                        this, "Se cambio la contraseña.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            }

                    } else {
                        Toast.makeText(
                            this, "La contraseña actual es incorrecta.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}