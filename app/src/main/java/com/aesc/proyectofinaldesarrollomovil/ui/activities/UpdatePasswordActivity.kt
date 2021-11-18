package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import android.view.View
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityUpdatePasswordBinding
import com.aesc.proyectofinaldesarrollomovil.extension.toast
import com.aesc.proyectofinaldesarrollomovil.ui.base.BaseActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogError
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogInfo
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class UpdatePasswordActivity : BaseActivity(), View.OnClickListener {
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
        Utils.statusProgress(true, binding.fragmentProgressBar)

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
            Utils.statusProgress(false, binding.fragmentProgressBar)
            dialogInfo(this, getString(R.string.contrasenia_no_es_valida))
        } else if (newPassword != repeatPassword) {
            Utils.statusProgress(false, binding.fragmentProgressBar)
            dialogInfo(this, getString(R.string.contrasenia_no_coincide))
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
                    Utils.statusProgress(false, binding.fragmentProgressBar)
                    if (task.isSuccessful) {
                        user.updatePassword(password)
                            .addOnCompleteListener { taskUpdatePassword ->
                                if (taskUpdatePassword.isSuccessful) {
                                    toast(getString(R.string.contrasenia_cambiada))
                                    finish()
                                }
                            }

                    } else {
                        dialogError(this, getString(R.string.contrasenia_actual_incorrecta))
                    }
                }
        }
    }
}