package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivitySignInBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.UserDao
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.aesc.proyectofinaldesarrollomovil.ui.base.BaseActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogError
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignInActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.btnCreateNewAccount.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnCancel -> {
                goToActivityF<LoginActivity>()
            }
            R.id.btnCreateNewAccount -> {
                Utils.statusProgress(true, binding.fragmentProgressBar)

                val username = binding.tieUsername.text.toString()
                val useremail = binding.tieEmail.text.toString()
                val userpassword = binding.tiePassword.text.toString()
                val userconfirmpassword = binding.tieConfirmPassword.text.toString()

                if (username.isNotEmpty() && useremail.isNotEmpty() && userpassword.isNotEmpty() && userconfirmpassword.isNotEmpty()) {
                    val passwordRegex = Pattern.compile(
                        "^" +
                                "(?=.*[-@#$%^&+=])" +     // Al menos 1 carácter especial
                                ".{6,}" +                // Al menos 4 caracteres
                                "$"
                    )

                    if (useremail.isEmpty()
                        || !Patterns.EMAIL_ADDRESS.matcher(useremail).matches()
                    ) {
                        Utils.statusProgress(false, binding.fragmentProgressBar)
                        dialogInfo(this, getString(R.string.insertar_un_email_valido))
                    } else if (userpassword.isEmpty()
                        || !passwordRegex.matcher(userpassword).matches()
                    ) {
                        Utils.statusProgress(false, binding.fragmentProgressBar)
                        dialogInfo(
                            this,
                            getString(R.string.contrasenia_no_es_valida)
                        )
                    } else if (userpassword != userconfirmpassword) {
                        Utils.statusProgress(false, binding.fragmentProgressBar)
                        dialogInfo(this, getString(R.string.contrasenia_no_coincide))
                    } else {
                        createAccount(username, useremail, userpassword)
                    }
                } else {
                    Utils.statusProgress(false, binding.fragmentProgressBar)
                    dialogInfo(this, getString(R.string.todos_los_campos_requeridos))
                }

            }
        }
    }

    private fun createAccount(
        username: String,
        useremail: String,
        userpassword: String
    ) {
        auth.createUserWithEmailAndPassword(useremail, userpassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val usersDao = UserDao()
                    val userT = auth.currentUser

                    usersDao.userCollection.whereEqualTo("uid", userT!!.uid).limit(1).get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val reslut = task.result.isEmpty
                                Utils.statusProgress(false, binding.fragmentProgressBar)
                                if (reslut) {
                                    //Add new user
                                    val user = User(
                                        userT.uid,
                                        username,
                                        "https://icon-library.com/images/2018/374217_anonymous-user-png-download.png",
                                        useremail
                                    )
                                    usersDao.addUser(user)
                                    goToActivityF<CheckEmailActivity>()
                                } else {
                                    //User Exists Previously
                                    goToActivityF<MainActivity>()
                                }
                            }
                        }
                } else {
                    Utils.statusProgress(false, binding.fragmentProgressBar)
                    dialogError(this, getString(R.string.cuenta_no_creada_intentar_de_nuevo))
                }
            }
    }
}