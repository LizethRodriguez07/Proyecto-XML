package com.example.gestiondeventasonlinestore_dany

import java.io.Serializable

data class Producto(
    val nomProducto: String,
    val marca: String,
    val descripcion: String,
    val precio: Double,
    val imagen: Int,
    var tallaSeleccionada: String = "",
    var cantidad: Int = 1
) : Serializable


