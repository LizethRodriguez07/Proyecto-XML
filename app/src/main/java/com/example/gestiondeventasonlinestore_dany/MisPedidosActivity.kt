package com.example.gestiondeventasonlinestore_dany

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityMisPedidosBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
        binding.rvPedidos.adapter = AdaptadorPedidos(
            this,
            pedidos,
            onAvanzar = ::avanzarEstado,
            onEliminar = ::preguntarEliminar
        )
    }

    private fun avanzarEstado(pedido: Pedido) {
        val nuevo = when (pedido.estado) {
            "PENDIENTE" -> "ENVIADO"
            else -> "ENTREGADO"
        }
        RepositorioPedidos.actualizarEstado(this, pedido.id, nuevo)
        mostrarPedidos()
    }

    private fun preguntarEliminar(pedido: Pedido) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.pedido_eliminar_titulo)
            .setMessage(R.string.pedido_eliminar_mensaje)
            .setPositiveButton(R.string.pedido_eliminar_btn) { _, _ ->
                RepositorioPedidos.eliminar(this, pedido.id)
                mostrarPedidos()
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}