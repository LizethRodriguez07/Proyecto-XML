package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityCarroComprasBinding
import java.util.ArrayList

class CarroComprasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarroComprasBinding
    private lateinit var adapter: AdaptadorCarroCompras
    private var carroCompras = ArrayList<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarroComprasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recepción segura de datos según la versión de Android
        val listaRecibida = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                "lista_carrito",
                ArrayList::class.java
            ) as? ArrayList<Producto>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("lista_carrito") as? ArrayList<Producto>
        }

        if (listaRecibida != null) {
            carroCompras = listaRecibida
        }

        setupRecyclerView()
        actualizarTotalInicial()

        binding.btnIrAPagar.setOnClickListener {
            if (carroCompras.isNotEmpty()) {
                val intent = Intent(this, DatosPersonalesActivity::class.java)
                // Opcional: Podrías pasar el total a la siguiente pantalla si lo necesitas
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvListaCarro.layoutManager = LinearLayoutManager(this)
        // Asegúrate que tu AdaptadorCarroCompras reciba estos dos parámetros
        adapter = AdaptadorCarroCompras(binding.tvTotal, carroCompras)
        binding.rvListaCarro.adapter = adapter
    }

    private fun actualizarTotalInicial() {
        val total = carroCompras.sumOf { it.precio }
        binding.tvTotal.text = "Total a Pagar: $ ${String.format("%,.0f", total)}"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
