package com.example.gestiondeventasonlinestore_dany

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityMisPedidosBinding

class MisPedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisPedidosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.ajustarBarrasSistema()

        binding.toolbarPedidos.setNavigationOnClickListener {
            finish()
        }

        binding.btnVolverPedidos.setOnClickListener {
            finish()
        }
    }
}