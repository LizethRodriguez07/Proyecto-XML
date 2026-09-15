package com.example.gestiondeventasonlinestore_dany

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class AdaptadorCarroCompras(
    private val carroCompras: ArrayList<Producto>,
    private val onListaCambio: () -> Unit
) : RecyclerView.Adapter<AdaptadorCarroCompras.ViewHolder>() {

    private val tallas = arrayOf("37", "38", "39", "40", "41", "42")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomProducto: TextView = itemView.findViewById(R.id.nomproducto)
        val nomdescripcion: TextView = itemView.findViewById(R.id.nomdescripcion)
        val tvMarca: TextView = itemView.findViewById(R.id.tvMarca)
        val tvPrecioUnitario: TextView = itemView.findViewById(R.id.tvPrecioUnitario)
        val nomprecio: TextView = itemView.findViewById(R.id.nomprecio)
        val imagen: ImageView = itemView.findViewById(R.id.imageView2)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
        val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
        val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val spTallaCarro: Spinner = itemView.findViewById(R.id.spTallaCarro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(
            R.layout.item_rv_carro_compras,
            parent,
            false
        )
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = carroCompras[position]

        holder.nomProducto.text = producto.nomProducto
        holder.tvMarca.text = "Marca: ${producto.marca}"
        holder.nomdescripcion.text = producto.descripcion
        holder.imagen.setImageResource(producto.imagen)
        holder.tvPrecioUnitario.text = "Precio: $ $${
            String.format("%,.0f", producto.precio)
        }"
        holder.nomprecio.text = "Subtotal: $ ${
            String.format("%,.0f", producto.precio * producto.cantidad)
        }"
        holder.tvCantidad.text = "${producto.cantidad}"

        holder.btnMenos.setOnClickListener {
            val current = holder.adapterPosition
            if (current == RecyclerView.NO_POSITION) return@setOnClickListener
            val item = carroCompras[current]
            item.cantidad--
            if (item.cantidad <= 0) {
                carroCompras.removeAt(current)
                notifyItemRemoved(current)
                notifyItemRangeChanged(current, carroCompras.size)
            } else {
                actualizarLinea(holder, item)
            }
            onListaCambio()
        }

        holder.btnMas.setOnClickListener {
            val current = holder.adapterPosition
            if (current == RecyclerView.NO_POSITION) return@setOnClickListener
            val item = carroCompras[current]
            item.cantidad++
            actualizarLinea(holder, item)
            onListaCambio()
        }

        holder.btnEliminar.setOnClickListener {
            val current = holder.adapterPosition
            if (current != RecyclerView.NO_POSITION) {
                carroCompras.removeAt(current)
                notifyItemRemoved(current)
                notifyItemRangeChanged(current, carroCompras.size)
                onListaCambio()
            }
        }

        // Selector de talla editable
        val adapterTallas = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_item,
            tallas
        )
        adapterTallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spTallaCarro.adapter = adapterTallas

        holder.spTallaCarro.onItemSelectedListener = null
        val selectedIndex = tallas.indexOf(producto.tallaSeleccionada)
        if (selectedIndex >= 0) holder.spTallaCarro.setSelection(selectedIndex)

        holder.spTallaCarro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val current = holder.adapterPosition
                if (current == RecyclerView.NO_POSITION) return
                val item = carroCompras[current]
                if (tallas[pos] == item.tallaSeleccionada) return

                item.tallaSeleccionada = tallas[pos]

                // Fusionar con otra línea del mismo producto y talla si existe
                var otroIndice = -1
                for (i in carroCompras.indices) {
                    if (i == current) continue
                    if (carroCompras[i].nomProducto == item.nomProducto &&
                        carroCompras[i].tallaSeleccionada == item.tallaSeleccionada
                    ) {
                        otroIndice = i
                        break
                    }
                }

                if (otroIndice != -1) {
                    carroCompras[otroIndice].cantidad += item.cantidad
                    carroCompras.removeAt(current)
                    notifyDataSetChanged()
                } else {
                    notifyItemChanged(current)
                }
                onListaCambio()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun actualizarLinea(holder: ViewHolder, item: Producto) {
        holder.tvCantidad.text = "${item.cantidad}"
        holder.nomprecio.text = "Subtotal: $ ${
            String.format("%,.0f", item.precio * item.cantidad)
        }"
    }

    override fun getItemCount(): Int = carroCompras.size
}