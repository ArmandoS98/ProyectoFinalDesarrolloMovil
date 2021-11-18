package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityLoginBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivity
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.UserDao
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.models.User
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesKey
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesProvider
import com.aesc.proyectofinaldesarrollomovil.ui.base.BaseActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogError
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.statusProgress
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : BaseActivity(), View.OnClickListener {
    private val TAG: String = "LoginActivity"
    private lateinit var googleConf: GoogleSignInOptions
    private val GOOGLE_SIGN_IN = 1998
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.signInAppCompatButton.setOnClickListener(this)
        binding.sibFirebaseGoogle.setOnClickListener(this)
        binding.btnCreateNewAccount.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.signInAppCompatButton -> {
                statusProgress(true, binding.fragmentProgressBar)
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty())
                    signIn(email, password)
                else {
                    statusProgress(false, binding.fragmentProgressBar)
                    dialogError(this, "Login Error")
                }
            }
            R.id.sibFirebaseGoogle -> {
                //progress bar
                statusProgress(true, binding.fragmentProgressBar)

                //Configuracion
                googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                signIn()
            }
            R.id.btnCreateNewAccount -> {
                goToActivity<SignInActivity>()
            }
            R.id.tvForgotPassword -> {
                goToActivity<AccountRecoveryActivity>()
            }
        }
    }

    private fun signIn() {
        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                statusProgress(false, binding.fragmentProgressBar)
                dialogError(this, "Google sign in failed")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    PreferencesProvider.set(this, PreferencesKey.RECORDARME, true)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    statusProgress(false, binding.fragmentProgressBar)
                    dialogError(this, "Login Error")
                    updateUI(null)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MainActivity", "signInWithEmail:success")
                    val user = auth.currentUser
                    val rememberMe = binding.rememberMe.isChecked
                    if (rememberMe) {
                        PreferencesProvider.set(this, PreferencesKey.RECORDARME, rememberMe)
                    } else {
                        PreferencesProvider.set(this, PreferencesKey.RECORDARME, rememberMe)
                    }
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MainActivity", "signInWithEmail:failure", task.exception)
                    statusProgress(false, binding.fragmentProgressBar)
                    dialogError(this, "Login Error")
                    updateUI(null)
                }
            }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            val usersDao = UserDao()
            usersDao.userCollection.whereEqualTo("uid", firebaseUser.uid)
                .limit(1).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reslut = task.result.isEmpty
                        statusProgress(false, binding.fragmentProgressBar)
                        if (reslut) {
                            //Add new user
                            val user = User(
                                firebaseUser.uid,
                                firebaseUser.displayName!!,
                                firebaseUser.photoUrl.toString(),
                                firebaseUser.email!!
                            )
                            usersDao.addUser(user)
                            goToActivityF<MainActivity>()
                        } else {
                            //User Exists Previously
                            goToActivityF<MainActivity>()
                        }
                    }
                }
        }

    }
}
