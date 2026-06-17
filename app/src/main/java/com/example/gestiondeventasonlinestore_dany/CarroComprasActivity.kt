package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        verificarContenidoCarrito()

        binding.btnIrAPagar.setOnClickListener {
            if (carroCompras.isNotEmpty()) {
                val intent = Intent(this, DatosPersonalesActivity::class.java)

                val total = carroCompras.sumOf { it.precio }
                intent.putExtra("lista_final_pedido", carroCompras)
                intent.putExtra("total_pagar", total)

                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvListaCarro.layoutManager = LinearLayoutManager(this)
        // Asegúrate que tu AdaptadorCarroCompras reciba estos dos parámetros
        adapter = AdaptadorCarroCompras(binding.tvTotal, carroCompras)
        binding.rvListaCarro.adapter = adapter

        adapter.registerAdapterDataObserver(object : androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
            override fun onChanged() { super.onChanged(); verificarContenidoCarrito() }
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) { verificarContenidoCarrito() }
        })
    }
    private fun verificarContenidoCarrito() {
        val total = carroCompras.sumOf { it.precio }
        binding.tvTotal.text = "Total a Pagar: $ ${String.format("%,.0f", total)}"

        if (carroCompras.isEmpty()) {
            binding.btnIrAPagar.isEnabled = false
            binding.btnIrAPagar.alpha = 0.4f
            binding.btnIrAPagar.text = "CARRITO VACÍO"
        } else {
            binding.btnIrAPagar.isEnabled = true
            binding.btnIrAPagar.alpha = 1.0f
            binding.btnIrAPagar.text = "IR A PAGAR"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

