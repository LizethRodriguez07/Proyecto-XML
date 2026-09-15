package com.example.gestiondeventasonlinestore_dany

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdaptadorPedidos(
    private val context: Context,
    private val listaPedidos: List<Pedido>
) : RecyclerView.Adapter<AdaptadorPedidos.ViewHolder>() {

    private val formatoFecha = SimpleDateFormat(
        context.getString(R.string.pedido_formato_fecha),
        Locale.getDefault()
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFechaPedido: TextView = itemView.findViewById(R.id.tvFechaPedido)
        val tvEstadoPedido: TextView = itemView.findViewById(R.id.tvEstadoPedido)
        val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        val contenedorProductos: LinearLayout = itemView.findViewById(R.id.contenedorProductos)
        val tvTotalPedido: TextView = itemView.findViewById(R.id.tvTotalPedido)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_pedidos, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pedido = listaPedidos[position]

        holder.tvFechaPedido.text = formatoFecha.format(Date(pedido.fecha))
        holder.tvEstadoPedido.setText(R.string.pedido_estado_pendiente)
        holder.tvNombreCliente.text = pedido.nombreCliente

        holder.contenedorProductos.removeAllViews()
        val inflater = LayoutInflater.from(holder.itemView.context)
        for (producto in pedido.productos) {
            val fila = inflater.inflate(
                R.layout.item_linea_pedido,
                holder.contenedorProductos,
                false
            )
            fila.findViewById<TextView>(R.id.tvLineaProducto).text =
                context.getString(
                    R.string.pedido_linea_producto,
                    producto.nomProducto,
                    producto.marca
                )
            fila.findViewById<TextView>(R.id.tvLineaCantidad).text =
                context.getString(
                    R.string.pedido_linea_cantidad,
                    producto.tallaSeleccionada,
                    producto.cantidad
                )
            holder.contenedorProductos.addView(fila)
        }

        holder.tvTotalPedido.text = context.getString(
            R.string.moneda_formato,
            String.format("%,.0f", pedido.total)
        )
    }

    override fun getItemCount(): Int = listaPedidos.size
}