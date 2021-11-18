package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.extension.goToActivityF
import com.aesc.proyectofinaldesarrollomovil.ui.base.BaseActivity

class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            goToActivityF<MainActivity>()
        }, 3000)
    }
}