package com.example.gestiondeventasonlinestore_dany

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.util.ArrayList

class AdaptadorProducto(
    private val context: Context,
    private val listaProducto: ArrayList<Producto>,
    private val carroCompras: ArrayList<Producto>,
    private val favoritos: HashSet<String>,
    private val onCartUpdated: (Int) -> Unit,
    private val onItemClick: (Producto) -> Unit,
    private val onToggleFavorito: (Producto) -> Unit
): RecyclerView.Adapter<AdaptadorProducto.ViewHolder>() {

    private val tallas = arrayOf("37", "38", "39", "40", "41", "42")

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nomproducto: TextView = itemView.findViewById(R.id.nomproducto)
        val nomdescripcion: TextView = itemView.findViewById(R.id.nomdescripcion)
        val tvMarca: TextView = itemView.findViewById(R.id.tvMarca)
        val nomprecio: TextView = itemView.findViewById(R.id.nomprecio)
        val imagen: ImageView = itemView.findViewById(R.id.imageView3)
        val btnAdd: MaterialButton = itemView.findViewById(R.id.btn_add_item)
        val spTallas: Spinner = itemView.findViewById(R.id.spTallas)
        val cardFavorito: com.google.android.material.card.MaterialCardView =
            itemView.findViewById(R.id.cardFavorito)
        val imgFavorito: ImageView = itemView.findViewById(R.id.imgFavorito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_productos, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaProducto[position]

        holder.nomproducto.text = producto.nomProducto
        holder.tvMarca.text = producto.marca
        holder.nomdescripcion.text = producto.descripcion
        holder.nomprecio.text = context.getString(
            R.string.moneda_formato,
            String.format("%,.0f", producto.precio)
        )
        holder.imagen.setImageResource(producto.imagen)
        holder.imagen.contentDescription = context.getString(
            R.string.content_desc_producto,
            producto.nomProducto
        )

        // Configuración del Spinner de Tallas
        val adapterTallas = ArrayAdapter(context, android.R.layout.simple_spinner_item, tallas)
        adapterTallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spTallas.adapter = adapterTallas

        holder.spTallas.onItemSelectedListener = null

        // Restaurar la talla seleccionada del producto
        val selectedIndex = tallas.indexOf(producto.tallaSeleccionada)
        if (selectedIndex >= 0) holder.spTallas.setSelection(selectedIndex)

        holder.spTallas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                producto.tallaSeleccionada = tallas[pos]
                actualizarEstadoBoton(holder, producto)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        holder.btnAdd.setOnClickListener {
            anadirOQuitar(holder, producto)
        }

        holder.imgFavorito.imageTintList = ColorStateList.valueOf(
            if (favoritos.contains(producto.nomProducto)) context.getColor(R.color.oro)
            else context.getColor(R.color.texto_secundario)
        )
        holder.imgFavorito.contentDescription = context.getString(
            R.string.content_desc_corazon,
            producto.nomProducto
        )
        holder.cardFavorito.setOnClickListener {
            onToggleFavorito(producto)
        }

        holder.itemView.setOnClickListener {
            onItemClick(producto)
        }

        actualizarEstadoBoton(holder, producto)
    }

    private fun anadirOQuitar(holder: ViewHolder, producto: Producto) {
        val indiceExistente = carroCompras.indexOfFirst {
            it.nomProducto == producto.nomProducto && it.tallaSeleccionada == producto.tallaSeleccionada
        }

        if (indiceExistente != -1) {
            // Línea existente: disminuimos la cantidad o la eliminamos
            val item = carroCompras[indiceExistente]
            item.cantidad--
            if (item.cantidad <= 0) {
                carroCompras.removeAt(indiceExistente)
            }
            Toast.makeText(
                context,
                context.getString(R.string.toast_producto_quitado, producto.nomProducto),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Línea nueva: añadimos una copia con la talla seleccionada
            carroCompras.add(
                producto.copy(
                    tallaSeleccionada = producto.tallaSeleccionada,
                    cantidad = 1
                )
            )
            Toast.makeText(
                context,
                context.getString(
                    R.string.toast_producto_anadido,
                    producto.nomProducto,
                    producto.tallaSeleccionada
                ),
                Toast.LENGTH_SHORT
            ).show()
        }

        actualizarEstadoBoton(holder, producto)
        onCartUpdated(carroCompras.sumOf { it.cantidad })
    }

    private fun actualizarEstadoBoton(holder: ViewHolder, producto: Producto) {
        val existe = carroCompras.any {
            it.nomProducto == producto.nomProducto && it.tallaSeleccionada == producto.tallaSeleccionada
        }
        if (existe) {
            configurarBotonQuitar(holder.btnAdd)
        } else {
            configurarBotonAnadir(holder.btnAdd)
        }
    }

    override fun getItemCount(): Int = listaProducto.size

    private fun configurarBotonAnadir(button: MaterialButton) {
        button.text = context.getString(R.string.btn_anadir)
        button.setTextColor(context.getColor(R.color.black))
        button.setIconResource(android.R.drawable.ic_input_add)
        button.iconTint = ColorStateList.valueOf(context.getColor(R.color.black))
        button.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.oro)))
        button.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.oro)))
        button.strokeWidth = 0
    }

    private fun configurarBotonQuitar(button: MaterialButton) {
        button.text = context.getString(R.string.btn_quitar)
        button.setTextColor(context.getColor(R.color.error))
        button.setIconResource(android.R.drawable.ic_delete)
        button.iconTint = ColorStateList.valueOf(context.getColor(R.color.error))
        button.setBackgroundTintList(
            ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
        )
        button.setStrokeColor(ColorStateList.valueOf(context.getColor(R.color.error)))
        button.strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
    }
}