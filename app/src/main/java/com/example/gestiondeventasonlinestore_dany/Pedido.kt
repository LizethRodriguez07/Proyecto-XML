package com.example.gestiondeventasonlinestore_dany

import java.io.Serializable

data class Pedido(
    val id: Long,
    val nombreCliente: String,
    val productos: ArrayList<Producto>,
    val total: Double,
    val fecha: Long,
    val estado: String = "PENDIENTE"
) : Serializable