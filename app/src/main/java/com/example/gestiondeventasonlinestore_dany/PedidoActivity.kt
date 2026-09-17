package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityPedidoBinding
import com.example.gestiondeventasonlinestore_dany.databinding.ItemLineaFacturaBinding
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

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
        val nombreCliente = intent.getStringExtra("nombre_cliente").orEmpty()
        val metodoPago = intent.getStringExtra("metodo_pago").orEmpty()
        val numeroCuenta = intent.getStringExtra("numero_cuenta").orEmpty()
        val idPedido = intent.getLongExtra("fecha_pedido", System.currentTimeMillis())
        val cedulaCliente = intent.getStringExtra("cliente_cedula").orEmpty()
        val telefonoCliente = intent.getStringExtra("cliente_telefono").orEmpty()
        val emailCliente = intent.getStringExtra("cliente_email").orEmpty()
        val direccionCliente = intent.getStringExtra("cliente_direccion").orEmpty()

        // 2. Renderizar la factura
        if (listaRecibida != null && listaRecibida.isNotEmpty()) {
            val total = if (totalPagar > 0.0) {
                totalPagar
            } else {
                listaRecibida.sumOf { it.precio * it.cantidad }
            }
            val totalFormateado = getString(
                R.string.moneda_formato,
                String.format("%,.0f", total)
            )

            binding.tvFacturaId.text = getString(R.string.factura_id, idPedido % 1000000)
            val fechaLegible = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                .format(Date(idPedido))
            binding.tvFacturaFecha.text = getString(R.string.factura_fecha, fechaLegible)

            binding.tvFacturaNombre.text = nombreCliente.ifBlank { "—" }
            binding.tvFacturaCedula.text = cedulaCliente.ifBlank { "—" }
            binding.tvFacturaCelular.text = telefonoCliente.ifBlank { "—" }
            binding.tvFacturaEmail.text = emailCliente.ifBlank { "—" }
            binding.tvFacturaDireccion.text = direccionCliente.ifBlank { "—" }

            val inflater = LayoutInflater.from(this)
            for (producto in listaRecibida) {
                val linea = ItemLineaFacturaBinding.inflate(
                    inflater,
                    binding.contenedorLineas,
                    false
                )
                linea.tvLineaFacturaProducto.text = producto.nomProducto
                linea.tvLineaFacturaDetalle.text = getString(
                    R.string.pedido_linea_detalle,
                    producto.marca,
                    producto.tallaSeleccionada,
                    producto.color
                )
                linea.tvLineaFacturaPrecio.text = getString(
                    R.string.moneda_formato,
                    String.format("%,.0f", producto.precio)
                )
                linea.tvLineaFacturaCantidad.text = getString(
                    R.string.pedido_linea_cantidad_x,
                    producto.cantidad
                )
                binding.contenedorLineas.addView(linea.root)
            }

            binding.tvFacturaTotal.text = totalFormateado

            if (metodoPago.isNotBlank()) {
                val cuentaExtra = if (numeroCuenta.isNotBlank()) " · N° $numeroCuenta" else ""
                binding.tvDetallePago.text = getString(
                    R.string.pedido_resumen_nota_pago,
                    metodoPago,
                    cuentaExtra,
                    totalFormateado
                )
                binding.tvDetallePago.visibility = View.VISIBLE
            }
        } else {
            binding.cardFactura.visibility = View.GONE
            binding.tvGracias.setText(R.string.pedido_mensaje)
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