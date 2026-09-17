package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

        binding.root.ajustarBarrasSistema()

        binding.toolbarCarrito.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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

        // Conservar el carrito (cambios de cantidades/tallas) al rotar la pantalla
        savedInstanceState?.let { estado ->
            val carroGuardado = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                estado.getSerializable(
                    "lista_carrito_rotacion",
                    ArrayList::class.java
                ) as? ArrayList<Producto>
            } else {
                @Suppress("DEPRECATION")
                estado.getSerializable("lista_carrito_rotacion") as? ArrayList<Producto>
            }
            if (carroGuardado != null) {
                carroCompras = carroGuardado
            }
        }

        setupRecyclerView()
        verificarContenidoCarrito()
        configurarMetodoPago()

        binding.btnIrAPagar.setOnClickListener {
            if (carroCompras.isNotEmpty() && validarMetodoPago()) {
                val intent = Intent(this, DatosPersonalesActivity::class.java)

                val total = carroCompras.sumOf { it.precio * it.cantidad }
                intent.putExtra("lista_final_pedido", carroCompras)
                intent.putExtra("total_pagar", total)
                intent.putExtra("metodo_pago", metodoPagoSeleccionado()?.first ?: "")
                intent.putExtra("numero_cuenta", numeroCuentaActual())

                startActivity(intent)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) {
            // Devuelve el carrito actualizado al catálogo (MainActivity)
            val resultIntent = Intent().apply {
                putExtra("lista_carrito", carroCompras)
                putExtra("total_pagar", carroCompras.sumOf { it.precio * it.cantidad })
            }
            setResult(RESULT_OK, resultIntent)
        }
    }

    private fun setupRecyclerView() {
        binding.rvListaCarro.layoutManager = LinearLayoutManager(this)
        adapter = AdaptadorCarroCompras(carroCompras) { verificarContenidoCarrito() }
        binding.rvListaCarro.adapter = adapter
    }

    private fun verificarContenidoCarrito() {
        val total = carroCompras.sumOf { it.precio * it.cantidad }
        binding.tvTotal.text = getString(
            R.string.moneda_formato,
            String.format("%,.0f", total)
        )
        binding.tvCantidadArticulos.text = getString(
            R.string.carro_articulos_conteo,
            carroCompras.sumOf { it.cantidad }
        )

        if (carroCompras.isEmpty()) {
            binding.btnIrAPagar.isEnabled = false
            binding.btnIrAPagar.alpha = 0.4f
            binding.btnIrAPagar.text = getString(R.string.carro_boton_vacio)
        } else {
            binding.btnIrAPagar.isEnabled = true
            binding.btnIrAPagar.alpha = 1.0f
            binding.btnIrAPagar.text = getString(R.string.carro_ir_pagar)
        }
    }

    private fun configurarMetodoPago() {
        binding.chipGroupPago.setOnCheckedStateChangeListener { _, _ ->
            binding.tilNumeroCuenta.error = null
            actualizarVisibilidadCampoNumero()
        }
        actualizarVisibilidadCampoNumero()
    }

    private fun metodoPagoSeleccionado(): Pair<String, Boolean>? {
        return when (binding.chipGroupPago.checkedChipId) {
            R.id.chipNequi -> Pair(getString(R.string.pago_nequi), true)
            R.id.chipDaviplata -> Pair(getString(R.string.pago_daviplata), true)
            R.id.chipEfectivo -> Pair(getString(R.string.pago_efectivo), false)
            else -> null
        }
    }

    private fun actualizarVisibilidadCampoNumero() {
        val usaNumero = metodoPagoSeleccionado()?.second == true
        binding.tilNumeroCuenta.visibility = if (usaNumero) View.VISIBLE else View.GONE
        binding.tvNotaEfectivo.visibility = if (usaNumero) View.GONE else View.VISIBLE
        if (!usaNumero) {
            binding.etNumeroCuenta.setText("")
            binding.tilNumeroCuenta.error = null
        }
    }

    private fun numeroCuentaActual(): String = binding.etNumeroCuenta.text.toString().trim()

    private fun validarMetodoPago(): Boolean {
        val metodo = metodoPagoSeleccionado()
        if (metodo == null) {
            Toast.makeText(this, R.string.pago_error_metodo, Toast.LENGTH_SHORT).show()
            return false
        }
        if (metodo.second) {
            val numero = numeroCuentaActual()
            if (numero.length != 10 || !numero.all { it in '0'..'9' }) {
                binding.tilNumeroCuenta.error = getString(R.string.pago_error_numero)
                return false
            }
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("lista_carrito_rotacion", carroCompras)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}