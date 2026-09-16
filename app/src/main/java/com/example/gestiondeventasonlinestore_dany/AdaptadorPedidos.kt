package com.example.gestiondeventasonlinestore_dany

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdaptadorPedidos(
    private val context: Context,
    private val listaPedidos: List<Pedido>,
    private val onAvanzar: (Pedido) -> Unit,
    private val onEliminar: (Pedido) -> Unit
) : RecyclerView.Adapter<AdaptadorPedidos.ViewHolder>() {

    private val formatoFecha = SimpleDateFormat(
        context.getString(R.string.pedido_formato_fecha),
        Locale.getDefault()
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFechaPedido: TextView = itemView.findViewById(R.id.tvFechaPedido)
        val tvEstadoPedido: TextView = itemView.findViewById(R.id.tvEstadoPedido)
        val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        val tvDetalleItems: TextView = itemView.findViewById(R.id.tvDetalleItems)
        val tvToggleDetalle: TextView = itemView.findViewById(R.id.tvToggleDetalle)
        val contenedorProductos: LinearLayout = itemView.findViewById(R.id.contenedorProductos)
        val tvTotalPedido: TextView = itemView.findViewById(R.id.tvTotalPedido)
        val btnAvanzarPedido: MaterialButton = itemView.findViewById(R.id.btnAvanzarPedido)
        val btnEliminarPedido: MaterialButton = itemView.findViewById(R.id.btnEliminarPedido)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_pedidos, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pedido = listaPedidos[position]

        holder.tvFechaPedido.text = formatoFecha.format(Date(pedido.fecha))
        holder.tvEstadoPedido.text = estadoLabel(pedido.estado)
        holder.tvNombreCliente.text = pedido.nombreCliente
        holder.tvDetalleItems.text = context.getString(
            R.string.pedido_detalle_items,
            pedido.productos.sumOf { it.cantidad }
        )

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

        val expandido = expandidos[holder.adapterPosition] ?: false
        aplicarEstadoDetalle(holder, expandido)

        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                expandidos[pos] = !(expandidos[pos] ?: false)
                aplicarEstadoDetalle(holder, expandidos[pos] ?: false)
            }
        }

        holder.tvToggleDetalle.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                expandidos[pos] = !(expandidos[pos] ?: false)
                aplicarEstadoDetalle(holder, expandidos[pos] ?: false)
            }
        }

        holder.btnAvanzarPedido.visibility =
            if (pedido.estado == "ENTREGADO") View.GONE else View.VISIBLE
        holder.btnAvanzarPedido.setOnClickListener { onAvanzar(pedido) }
        holder.btnEliminarPedido.setOnClickListener { onEliminar(pedido) }
    }

    private fun aplicarEstadoDetalle(holder: ViewHolder, expandido: Boolean) {
        holder.contenedorProductos.visibility = if (expandido) View.VISIBLE else View.GONE
        holder.tvToggleDetalle.text = context.getString(
            if (expandido) R.string.pedido_btn_ocultar_detalle
            else R.string.pedido_btn_ver_detalle
        )
    }

    private fun estadoLabel(estado: String): String = when (estado) {
        "PENDIENTE" -> context.getString(R.string.pedido_estado_pendiente)
        "ENVIADO" -> context.getString(R.string.pedido_estado_enviado)
        "ENTREGADO" -> context.getString(R.string.pedido_estado_entregado)
        else -> estado
    }

    override fun getItemCount(): Int = listaPedidos.size

    private val expandidos = mutableMapOf<Int, Boolean>()
}