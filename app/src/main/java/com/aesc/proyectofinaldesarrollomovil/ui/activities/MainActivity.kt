package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityMainBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesKey
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesProvider
import com.aesc.proyectofinaldesarrollomovil.ui.base.BaseActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogBienvenida
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firebase Auth
        auth = Firebase.auth

        val navView: BottomNavigationView = binding.navView
        supportActionBar!!.elevation = 0f

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_history, R.id.navigation_home, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        val recordarme = PreferencesProvider.bool(this, PreferencesKey.RECORDARME)

        if (currentUser == null || !recordarme)
            goToActivityF<LoginActivity>()
        else {
            if (!currentUser.isEmailVerified)
                goToActivityF<CheckEmailActivity>()
            else {
                //Bienvenida de paimon
                val isTheFirstTime = PreferencesProvider.bool(this, PreferencesKey.FIRST_TIME)
                if (!isTheFirstTime) {
                    dialogBienvenida(this)
                    PreferencesProvider.set(this, PreferencesKey.FIRST_TIME, true)
                }
            }
        }
    }
}