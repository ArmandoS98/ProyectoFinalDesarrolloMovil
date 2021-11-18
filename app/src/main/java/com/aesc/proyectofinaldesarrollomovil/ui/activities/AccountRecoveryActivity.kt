package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import android.view.View
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityAccountRecoveryBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.extension.toast
import com.aesc.proyectofinaldesarrollomovil.ui.base.BaseActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogError
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountRecoveryActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAccountRecoveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSenEmail.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        Utils.statusProgress(true, binding.fragmentProgressBar)
        val email = binding.emailEditText.text.toString()
        if (email.isNotEmpty()) {
            Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                Utils.statusProgress(false, binding.fragmentProgressBar)
                if (task.isSuccessful) {
                    toast(getString(R.string.msg_correo_de_verificacion))
                    goToActivityF<LoginActivity>()
                } else {
                    dialogError(this, "Ingrese un email de\nuna cuenta valido")
                }
            }
        } else {
            Utils.statusProgress(false, binding.fragmentProgressBar)
            dialogError(this, "Ingrese un email de\nuna cuenta valida")
        }
    }
}