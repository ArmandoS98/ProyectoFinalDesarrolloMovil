package com.aesc.proyectofinaldesarrollomovil.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aesc.proyectofinaldesarrollomovil.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnVolver.setOnClickListener(this)
        binding.webview.loadUrl("https://github.com/ArmandoS98")
    }

    override fun onClick(p0: View?) {
        finish()
    }
}