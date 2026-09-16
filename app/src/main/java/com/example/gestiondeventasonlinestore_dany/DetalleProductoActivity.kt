package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityDetalleProductoBinding

class DetalleProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoBinding
    private lateinit var producto: Producto

    private val tallas = arrayOf("37", "38", "39", "40", "41", "42")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.ajustarBarrasSistema()

        binding.toolbarDetalle.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val recibido = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("producto", Producto::class.java) as? Producto
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("producto") as? Producto
        }
        if (recibido == null) {
            finish()
            return
        }
        producto = recibido

        binding.imagenDetalle.setImageResource(producto.imagen)
        binding.imagenDetalle.contentDescription =
            getString(R.string.content_desc_producto, producto.nomProducto)
        binding.tvDetalleMarca.text = producto.marca
        binding.tvDetalleNombre.text = producto.nomProducto
        binding.tvDetallePrecio.text = getString(
            R.string.moneda_formato,
            String.format("%,.0f", producto.precio)
        )
        binding.tvDetalleDescripcion.text =
            getString(R.string.detalle_descripcion, producto.nomProducto) + "\n\n" +
                getString(R.string.detalle_global_descripcion)

        val adapterTallas = ArrayAdapter(this, android.R.layout.simple_spinner_item, tallas)
        adapterTallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTallasDetalle.adapter = adapterTallas
        val indice = tallas.indexOf(producto.tallaSeleccionada)
        if (indice >= 0) binding.spTallasDetalle.setSelection(indice)

        binding.btnAnadirDetalle.setOnClickListener {
            val tallaSel = tallas[binding.spTallasDetalle.selectedItemPosition]
            val resultado = producto.copy(tallaSeleccionada = tallaSel, cantidad = 1)
            setResult(RESULT_OK, Intent().putExtra("producto_detalle", resultado))
            Toast.makeText(
                this,
                getString(R.string.toast_producto_anadido, producto.nomProducto, tallaSel),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
}