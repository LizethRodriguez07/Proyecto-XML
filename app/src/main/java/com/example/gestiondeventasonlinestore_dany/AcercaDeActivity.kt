package com.example.gestiondeventasonlinestore_dany

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityAcercaBinding

class AcercaDeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAcercaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcercaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.ajustarBarrasSistema()

        binding.toolbarAcerca.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}