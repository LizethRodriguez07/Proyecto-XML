package com.example.gestiondeventasonlinestore_dany

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityTerminosBinding

class TerminosCondicionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTerminosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTerminosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.ajustarBarrasSistema()

        binding.toolbarTerminos.setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.btnAceptarTerminos.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        binding.btnCancelarTerminos.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}