package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiondeventasonlinestore_dany.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val terminosLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // El usuario leyó y aceptó los términos: marcamos la casilla
            binding.chkTerminos.isChecked = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.ajustarBarrasSistema()

        // Animación de entrada del logo
        binding.imgLogo.startAnimation(
            AlphaAnimation(0f, 1f).apply {
                duration = 700
                fillAfter = true
            }
        )

        binding.chkTerminos.setOnCheckedChangeListener { _, _ ->
            actualizarEstadoBoton()
        }

        binding.tvVerTerminos.setOnClickListener {
            val intent = Intent(this, TerminosCondicionesActivity::class.java)
            terminosLauncher.launch(intent)
        }

        binding.btnComenzar.setOnClickListener {
            if (binding.chkTerminos.isChecked) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val animacionError = AlphaAnimation(0.2f, 1.0f).apply {
                    duration = 250
                    repeatCount = 1
                }
                binding.chkTerminos.startAnimation(animacionError)
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
        binding.btnComenzar.isEnabled = binding.chkTerminos.isChecked
        binding.btnComenzar.alpha = if (binding.chkTerminos.isChecked) 1.0f else 0.5f
    }
}