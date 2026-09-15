package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox

class SplashActivity : AppCompatActivity() {

    private lateinit var chkTerminos: MaterialCheckBox
    private lateinit var btnComenzar: MaterialButton

    private val terminosLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // El usuario leyó y aceptó los términos: marcamos la casilla
            chkTerminos.isChecked = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        chkTerminos = findViewById(R.id.chkTerminos)
        btnComenzar = findViewById(R.id.btnComenzar)
        val tvVerTerminos = findViewById<TextView>(R.id.tvVerTerminos)

        // Animación de entrada del logo
        findViewById<View>(R.id.imgLogo).startAnimation(
            AlphaAnimation(0f, 1f).apply {
                duration = 700
                fillAfter = true
            }
        )

        chkTerminos.setOnCheckedChangeListener { _, _ ->
            actualizarEstadoBoton()
        }

        tvVerTerminos.setOnClickListener {
            val intent = Intent(this, TerminosCondicionesActivity::class.java)
            terminosLauncher.launch(intent)
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
                Toast.makeText(this, R.string.msg_acepta_terminos, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Garantiza que el estado del botón coincida con la casilla (al volver o rotar)
        actualizarEstadoBoton()
    }

    private fun actualizarEstadoBoton() {
        btnComenzar.isEnabled = chkTerminos.isChecked
        btnComenzar.alpha = if (chkTerminos.isChecked) 1.0f else 0.5f
    }
}