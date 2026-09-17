package com.example.gestiondeventasonlinestore_dany

import java.io.Serializable

data class Pedido(
    val id: Long,
    val nombreCliente: String,
    val productos: ArrayList<Producto>,
    val total: Double,
    val fecha: Long,
    val estado: String = "PENDIENTE",
    val metodoPago: String = "",
    val numeroCuenta: String = "",
    val montoPago: Double = 0.0,
    val clienteCedula: String = "",
    val clienteTelefono: String = "",
    val clienteEmail: String = "",
    val clienteDireccion: String = ""
) : Serializable