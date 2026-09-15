package com.example.gestiondeventasonlinestore_dany

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

object RepositorioPerfil {

    private const val PREFS = "store_dany_perfil"
    private const val KEY_CLIENTE = "cliente_json"

    fun guardar(context: Context, cliente: Cliente) {
        val json = JSONObject().apply {
            put("nombre", cliente.nombre)
            put("apellidos", cliente.apellidos)
            put("cedula", cliente.cedula)
            put("celular", cliente.celular)
            put("email", cliente.email)
            put("departamento", cliente.departamento)
            put("municipio", cliente.municipio)
            put("direccion", cliente.direccion)
        }
        prefs(context).edit().putString(KEY_CLIENTE, json.toString()).apply()
    }

    fun leer(context: Context): Cliente? {
        val raw = prefs(context).getString(KEY_CLIENTE, null) ?: return null
        return try {
            val json = JSONObject(raw)
            Cliente(
                nombre = json.optString("nombre", ""),
                apellidos = json.optString("apellidos", ""),
                cedula = json.optString("cedula", ""),
                celular = json.optString("celular", ""),
                email = json.optString("email", ""),
                departamento = json.optString("departamento", ""),
                municipio = json.optString("municipio", ""),
                direccion = json.optString("direccion", "")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}