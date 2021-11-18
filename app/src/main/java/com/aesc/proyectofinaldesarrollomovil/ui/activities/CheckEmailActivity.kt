package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import android.view.View
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityCheckEmailBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesKey
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesProvider
import com.aesc.proyectofinaldesarrollomovil.ui.base.BaseActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class CheckEmailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityCheckEmailBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        binding.veficateEmailAppCompatButton.setOnClickListener(this)
        binding.tvEnviarOtro.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.veficateEmailAppCompatButton -> {
                val user = auth.currentUser
                val profileUpdates = userProfileChangeRequest {}
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (user.isEmailVerified) {
                                PreferencesProvider.set(this, PreferencesKey.RECORDARME, true)
                                goToActivityF<MainActivity>()
                            } else {
                                dialogInfo(this, getString(R.string.msg_verifica_tu_correo))
                            }
                        }
                    }
            }
            R.id.tvEnviarOtro -> {
                sendEmailVerification()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                PreferencesProvider.set(this, PreferencesKey.RECORDARME, true)
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
                    dialogInfo(this, getString(R.string.msg_se_a_enviado_un_correo))
                }
            }
    }
}