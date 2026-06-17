package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Bundle
import android.support.v4.os.IResultReceiver
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val chkTerminos = findViewById<MaterialCheckBox>(R.id.chkTerminos)
        val btnComenzar = findViewById<MaterialButton>(R.id.btnComenzar)

        btnComenzar.isEnabled = false
        btnComenzar.alpha = 0.5f

        chkTerminos.setOnCheckedChangeListener { _, isChecked ->
        btnComenzar.isEnabled = isChecked
        btnComenzar.alpha = if (isChecked) 1.0f else 0.5f
        }

        btnComenzar.setOnClickListener {
            if (chkTerminos.isChecked) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val animacionError = AlphaAnimation(0.2f, 1.0f).apply {
                    duration = 250
                    repeatCount = 1
                }
                chkTerminos.startAnimation(animacionError)
            }
        }
    }
}