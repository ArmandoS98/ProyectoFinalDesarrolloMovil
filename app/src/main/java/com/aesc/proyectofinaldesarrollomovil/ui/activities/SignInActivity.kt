package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivitySignInBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.UserDao
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignInActivity : AppCompatActivity(), View.OnClickListener {
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

                    if (useremail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(useremail)
                            .matches()
                    ) {
                        Toast.makeText(
                            this, "Ingrese un email valido.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (userpassword.isEmpty() || !passwordRegex.matcher(userpassword)
                            .matches()
                    ) {
                        Toast.makeText(
                            this, "La contraseña es debil.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (userpassword != userconfirmpassword) {
                        Toast.makeText(
                            this, "Confirma la contraseña.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        createAccount(username, useremail, userpassword)
                    }
                } else
                    Toast.makeText(
                        baseContext,
                        "Todos los campos son obligatorios",
                        Toast.LENGTH_SHORT
                    ).show()
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
                    Toast.makeText(
                        this, "No se pudo crear la cuenta. Vuelva a intertarlo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}