package com.example.gestiondeventasonlinestore_dany

import java.io.Serializable

data class Cliente(
    val nombre: String,
    val apellidos: String,
    val cedula: String,
    val celular: String,
    val email: String,
    val departamento: String,
    val municipio: String,
    val direccion: String
) : Serializable