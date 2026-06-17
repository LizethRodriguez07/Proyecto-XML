package com.example.gestiondeventasonlinestore_dany

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.util.ArrayList

class AdaptadorProducto(
    private val context: Context,
    private val listaProducto: ArrayList<Producto>,
    private val carroCompras: ArrayList<Producto>,
    private val onCartUpdated: (Int) -> Unit
): RecyclerView.Adapter<AdaptadorProducto.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nomproducto: TextView = itemView.findViewById(R.id.nomproducto)
        val nomdescripcion: TextView = itemView.findViewById(R.id.nomdescripcion)
        val nomprecio: TextView = itemView.findViewById(R.id.nomprecio)
        val imagen: ImageView = itemView.findViewById(R.id.imageView3)
        val btnAdd: MaterialButton = itemView.findViewById(R.id.btn_add_item)
        // 1. Agregamos el Spinner al ViewHolder
        val spTallas: Spinner = itemView.findViewById(R.id.spTallas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_productos, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaProducto[position]

        holder.nomproducto.text = producto.nomProducto
        holder.nomdescripcion.text = producto.descripcion
        holder.nomprecio.text = "$${String.format("%,.0f", producto.precio)}"
        holder.imagen.setImageResource(producto.imagen)

        // Configuración del Spinner de Tallas
        val tallas = arrayOf("37", "38", "39", "40", "41", "42")
        val adapterTallas = ArrayAdapter(context, android.R.layout.simple_spinner_item, tallas)
        adapterTallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spTallas.adapter = adapterTallas

        holder.spTallas.onItemSelectedListener = null

        // Guardar la talla seleccionada en el objeto producto
        val selectedIndex = tallas.indexOf(producto.tallaSeleccionada)
        if (selectedIndex >= 0) holder.spTallas.setSelection(selectedIndex)

        holder.spTallas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                producto.tallaSeleccionada = tallas[pos]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (carroCompras.contains(producto)) {
            holder.btnAdd.text = "Añadido"
            holder.btnAdd.isEnabled = false
        } else {
            holder.btnAdd.text = "Añadir"
            holder.btnAdd.isEnabled = true
        }

        // botón de Añadir
        holder.btnAdd.setOnClickListener {
            val yaEstaEnCarrito = carroCompras.any{it.nomProducto == producto.nomProducto}

            if (!yaEstaEnCarrito) {
                carroCompras.add(producto)

                holder.btnAdd.text = "Añadido"
                holder.btnAdd.isEnabled = false

                onCartUpdated(carroCompras.size)
            }
        }
    }

    override fun getItemCount(): Int = listaProducto.size
}