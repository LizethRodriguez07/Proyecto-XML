package com.example.gestiondeventasonlinestore_dany

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Promocion(
    val tag: String,
    val titulo: String,
    val sub: String
)

class AdaptadorPromociones(
    private val listaPromos: List<Promocion>
) : RecyclerView.Adapter<AdaptadorPromociones.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTag: TextView = itemView.findViewById(R.id.tvTagPromo)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloPromo)
        val tvSub: TextView = itemView.findViewById(R.id.tvSubPromo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_promo, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val promo = listaPromos[position]
        holder.tvTag.text = promo.tag
        holder.tvTitulo.text = promo.titulo
        holder.tvSub.text = promo.sub
    }

    override fun getItemCount(): Int = listaPromos.size
}