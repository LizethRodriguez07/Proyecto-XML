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
        val tvDetalleContacto: TextView = itemView.findViewById(R.id.tvDetalleContacto)
        val tvDetalleDireccion: TextView = itemView.findViewById(R.id.tvDetalleDireccion)
        val tvDetallePago: TextView = itemView.findViewById(R.id.tvDetallePago)
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
            fila.findViewById<TextView>(R.id.tvLineaProducto).text = producto.nomProducto
            fila.findViewById<TextView>(R.id.tvLineaDetalle).text = context.getString(
                R.string.pedido_linea_detalle,
                producto.marca,
                producto.tallaSeleccionada,
                producto.color
            )
            fila.findViewById<TextView>(R.id.tvLineaCantidad).text = context.getString(
                R.string.pedido_linea_cantidad_x,
                producto.cantidad
            )
            holder.contenedorProductos.addView(fila)
        }

        if (pedido.clienteCedula.isNotBlank() ||
            pedido.clienteTelefono.isNotBlank() ||
            pedido.clienteEmail.isNotBlank()
        ) {
            holder.tvDetalleContacto.text = buildString {
                if (pedido.clienteCedula.isNotBlank()) {
                    append(context.getString(R.string.pedido_detalle_cedula, pedido.clienteCedula))
                }
                if (pedido.clienteTelefono.isNotBlank()) {
                    if (isNotEmpty()) append("\n")
                    append(context.getString(R.string.pedido_detalle_telefono, pedido.clienteTelefono))
                }
                if (pedido.clienteEmail.isNotBlank()) {
                    if (isNotEmpty()) append("\n")
                    append(context.getString(R.string.pedido_detalle_email, pedido.clienteEmail))
                }
            }
            holder.tvDetalleContacto.tag = true
        } else {
            holder.tvDetalleContacto.text = ""
            holder.tvDetalleContacto.tag = false
        }

        if (pedido.clienteDireccion.isNotBlank()) {
            holder.tvDetalleDireccion.text = context.getString(
                R.string.pedido_detalle_direccion, pedido.clienteDireccion
            )
            holder.tvDetalleDireccion.tag = true
        } else {
            holder.tvDetalleDireccion.text = ""
            holder.tvDetalleDireccion.tag = false
        }

        holder.tvTotalPedido.text = context.getString(
            R.string.moneda_formato,
            String.format("%,.0f", pedido.total)
        )

        if (pedido.metodoPago.isNotBlank()) {
            val cuentaLinea = if (pedido.numeroCuenta.isNotBlank()) {
                "\n${context.getString(R.string.pedido_detalle_cuenta, pedido.numeroCuenta)}"
            } else {
                ""
            }
            val montoLinea = "\n" + context.getString(
                R.string.pedido_detalle_monto,
                context.getString(
                    R.string.moneda_formato,
                    String.format("%,.0f", pedido.montoPago)
                )
            )
            holder.tvDetallePago.text = context.getString(
                R.string.pedido_detalle_metodo, pedido.metodoPago
            ) + cuentaLinea + montoLinea
            holder.tvDetallePago.tag = true
        } else {
            holder.tvDetallePago.text = ""
            holder.tvDetallePago.tag = false
        }

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
        holder.tvDetalleContacto.visibility =
            if (expandido && holder.tvDetalleContacto.tag == true) View.VISIBLE else View.GONE
        holder.tvDetalleDireccion.visibility =
            if (expandido && holder.tvDetalleDireccion.tag == true) View.VISIBLE else View.GONE
        holder.tvDetallePago.visibility =
            if (expandido && holder.tvDetallePago.tag == true) View.VISIBLE else View.GONE
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