package com.example.gestiondeventasonlinestore_dany

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class AdaptadorCarroCompras(
    private val tvTotal: TextView,
    private val carroCompras: ArrayList<Producto>
) : RecyclerView.Adapter<AdaptadorCarroCompras.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomProducto: TextView = itemView.findViewById(R.id.nomproducto)
        val nomdescripcion: TextView = itemView.findViewById(R.id.nomdescripcion)
        val nomprecio: TextView = itemView.findViewById(R.id.nomprecio)
        // AGREGAMOS LA IMAGEN
        val imagen: ImageView = itemView.findViewById(R.id.imageView2)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
        val tvTallaSeleccionada: TextView = itemView.findViewById(R.id.tvTallaSeleccionada)

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
        holder.nomdescripcion.text = producto.descripcion
        holder.nomprecio.text = "$ ${String.format("%,.0f", producto.precio)}"
        holder.imagen.setImageResource(producto.imagen)

        holder.tvTallaSeleccionada.text = "Talla: ${producto.tallaSeleccionada}"

        holder.btnEliminar.setOnClickListener {

            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                carroCompras.removeAt(currentPosition)
                notifyItemRemoved(currentPosition)
                notifyItemRangeChanged(currentPosition, carroCompras.size)
                actualizarTotal()
            }
        }
    }

    override fun getItemCount(): Int = carroCompras.size
    fun actualizarTotal() {
        var acumulado = 0.0
        carroCompras.forEach {
            acumulado += it.precio
        }
        tvTotal.text = "Total a pagar: $ ${String.format("%,.0f", acumulado)}"
    }
}
