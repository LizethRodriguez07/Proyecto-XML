package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityAyudaBinding
import java.util.Calendar

class AyudaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAyudaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAyudaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.ajustarBarrasSistema()

        binding.toolbarAyuda.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val telefono = "573123555400"
        binding.lnLlamar.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+$telefono")))
        }
        binding.lnWhatsApp.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$telefono")))
        }

        actualizarEstado()
    }

    private fun actualizarEstado() {
        val ahora = Calendar.getInstance()
        val dia = ahora.get(Calendar.DAY_OF_WEEK)
        val hora = ahora.get(Calendar.HOUR_OF_DAY) + ahora.get(Calendar.MINUTE) / 60.0
        val abierto = dia in Calendar.MONDAY..Calendar.FRIDAY && hora in 8.0..19.0
        val color = if (abierto) R.color.exito else R.color.texto_terciario
        binding.tvEstadoAyuda.setText(if (abierto) R.string.ayuda_abierto else R.string.ayuda_cerrado)
        binding.tvEstadoAyuda.setTextColor(ContextCompat.getColor(this, color))
        binding.dotEstadoAyuda.backgroundTintList = ContextCompat.getColorStateList(this, color)
    }
}