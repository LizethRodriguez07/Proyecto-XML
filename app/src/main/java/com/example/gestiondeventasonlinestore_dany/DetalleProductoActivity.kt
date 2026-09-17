package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityDetalleProductoBinding
import com.google.android.material.chip.Chip
import java.util.Locale

class DetalleProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleProductoBinding
    private lateinit var producto: Producto
    private var esFavorito = false

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

        esFavorito = RepositorioFavoritos.leer(this).contains(producto.nomProducto)
        renderizarFavorito()

        binding.cardFavoritoDetalle.setOnClickListener {
            esFavorito = !esFavorito
            val set = RepositorioFavoritos.leer(this)
            if (esFavorito) {
                set.add(producto.nomProducto)
                Toast.makeText(
                    this,
                    getString(R.string.toast_favorito_anadido, producto.nomProducto),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                set.remove(producto.nomProducto)
                Toast.makeText(
                    this,
                    getString(R.string.toast_favorito_quitado, producto.nomProducto),
                    Toast.LENGTH_SHORT
                ).show()
            }
            RepositorioFavoritos.guardar(this, set)
            renderizarFavorito()
        }

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

        configurarChipsColor()

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

    private fun renderizarFavorito() {
        binding.imgFavoritoDetalle.imageTintList = ColorStateList.valueOf(
            if (esFavorito) getColor(R.color.oro) else getColor(R.color.texto_secundario)
        )
        binding.imgFavoritoDetalle.contentDescription = getString(
            R.string.content_desc_corazon,
            producto.nomProducto
        )
    }

    private fun configurarChipsColor() {
        val colores = producto.color.split("/")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (colores.isEmpty()) {
            binding.tvDetalleColorLabel.visibility = View.GONE
            binding.chipGroupColores.visibility = View.GONE
            return
        }

        val densidad = resources.displayMetrics.density
        for (color in colores) {
            val chip = Chip(this)
            chip.text = color
            chip.isCheckable = false
            chip.isClickable = false
            chip.isFocusable = false
            chip.chipBackgroundColor =
                ColorStateList.valueOf(getColor(R.color.surface_alta))
            chip.setTextColor(getColor(R.color.texto_secundario))
            chip.chipStrokeColor = ColorStateList.valueOf(getColor(R.color.oro))
            chip.chipStrokeWidth = 1f * densidad
            chip.chipCornerRadius = 24f * densidad
            chip.textSize = 13f
            chip.minHeight = (36 * densidad).toInt()
            chip.chipIcon = dibujarPuntoColor(color)
            chip.isChipIconVisible = true
            chip.iconStartPadding = 6f * densidad
            chip.iconEndPadding = 6f * densidad
            binding.chipGroupColores.addView(chip)
        }
    }

    private fun dibujarPuntoColor(nombre: String): Drawable {
        val diametro = (14 * resources.displayMetrics.density).toInt()
        val punto = GradientDrawable()
        punto.shape = GradientDrawable.OVAL
        punto.setColor(colorDeNombre(nombre))
        punto.setSize(diametro, diametro)
        return punto
    }

    private fun colorDeNombre(nombre: String): Int {
        val n = nombre.lowercase(Locale.getDefault())
        return when {
            n.contains("blanco") -> android.graphics.Color.WHITE
            n.contains("negro") -> android.graphics.Color.BLACK
            n.contains("gris") -> android.graphics.Color.rgb(158, 158, 158)
            n.contains("verde") -> android.graphics.Color.rgb(76, 175, 80)
            n.contains("azul") -> android.graphics.Color.rgb(33, 150, 243)
            n.contains("café") || n.contains("cafe") || n.contains("marrón") || n.contains("marron") ->
                android.graphics.Color.rgb(141, 110, 99)
            n.contains("rojo") -> android.graphics.Color.rgb(255, 82, 82)
            else -> getColor(R.color.oro)
        }
    }
}