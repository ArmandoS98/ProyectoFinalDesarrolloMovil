package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import android.view.View
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityDeleteAccountBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.ui.base.BaseActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogDeleteAccount
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogError
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DeleteAccountActivity : BaseActivity(), View.OnClickListener {
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
        if (password.isNotEmpty())
            deleteAccount(password)
        else
            dialogError(this, getString(R.string.password_incorrecta))
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
                                    dialogDeleteAccount(
                                        this,
                                        getString(R.string.se_elimino_la_cuenta)
                                    )
                                    signOut()
                                }
                            }
                    } else {
                        dialogError(this, getString(R.string.password_incorrecta))
                    }
                }
        }
    }

    private fun signOut() {
        auth.signOut()
        goToActivityF<LoginActivity>()
    }
}