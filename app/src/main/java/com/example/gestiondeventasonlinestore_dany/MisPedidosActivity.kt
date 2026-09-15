package com.example.gestiondeventasonlinestore_dany

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

        mostrarPedidos()
    }

    private fun mostrarPedidos() {
        val pedidos = RepositorioPedidos.leer(this)

        if (pedidos.isEmpty()) {
            binding.scrollPedidos.visibility = android.view.View.VISIBLE
            binding.rvPedidos.visibility = android.view.View.GONE
            return
        }

        binding.scrollPedidos.visibility = android.view.View.GONE
        binding.rvPedidos.visibility = android.view.View.VISIBLE
        binding.rvPedidos.layoutManager = LinearLayoutManager(this)
        binding.rvPedidos.adapter = AdaptadorPedidos(this, pedidos)
    }
}