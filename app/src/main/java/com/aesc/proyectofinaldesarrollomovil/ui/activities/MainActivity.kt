package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityMainBinding
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesKey
import com.aesc.proyectofinaldesarrollomovil.provider.preferences.PreferencesProvider
import com.aesc.proyectofinaldesarrollomovil.ui.base.BaseActivity
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogBienvenida
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.elevation = 0f
        //AESC 2021-11-26 Initialize Logger
        Logger.addLogAdapter(AndroidLogAdapter())

        //Firebase Auth
        auth = Firebase.auth

        val navView: CurvedBottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val menuItems = arrayOf(
            CbnMenuItem(
                R.drawable.ic_map_24,
                R.drawable.avd_map,
                R.id.navigation_home
            ),
            CbnMenuItem(
                R.drawable.ic_history_24,
                R.drawable.avd_history,
                R.id.navigation_history
            ),
            CbnMenuItem(
                R.drawable.ice_person_24,
                R.drawable.avd_person,
                R.id.navigation_profile
            )
        )
        val activeIndex = savedInstanceState?.getInt("activeIndex") ?: 1
        navView.setMenuItems(menuItems, 1/*,activeIndex*/)
        navView.setupWithNavController(navController)
        navView.onMenuItemClick(1)
//        navView.onMenuItemClick(1)


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