package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityPedidoBinding
import java.util.ArrayList

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.ajustarBarrasSistema()

        // 1. Leer los datos del pedido enviados desde DatosPersonalesActivity
        val listaRecibida = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                "lista_final_pedido",
                ArrayList::class.java
            ) as? ArrayList<Producto>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("lista_final_pedido") as? ArrayList<Producto>
        }
        val totalPagar = intent.getDoubleExtra("total_pagar", 0.0)
        val nombreCliente = intent.getStringExtra("nombre_cliente")

        // 2. Mostrar el resumen de la compra
        if (listaRecibida != null && listaRecibida.isNotEmpty()) {
            val cantidad = listaRecibida.sumOf { it.cantidad }
            val total = if (totalPagar > 0.0) {
                totalPagar
            } else {
                listaRecibida.sumOf { it.precio * it.cantidad }
            }
            val totalFormateado = getString(
                R.string.moneda_formato,
                String.format("%,.0f", total)
            )

            binding.tvSubtitulo.text = if (nombreCliente.isNullOrBlank()) {
                getString(R.string.pedido_resumen_sin_nombre, cantidad, totalFormateado)
            } else {
                getString(R.string.pedido_resumen, cantidad, totalFormateado, nombreCliente)
            }
        } else {
            binding.tvSubtitulo.setText(R.string.pedido_mensaje)
        }

        binding.btnVolverTienda.setOnClickListener {
            volverAlInicio()
        }
    }

    override fun onBackPressed() {
        volverAlInicio()
    }

    // 3. Redirige al catálogo cerrando toda la pila de pantallas de la compra
    private fun volverAlInicio() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}