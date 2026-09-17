package com.example.gestiondeventasonlinestore_dany

import android.content.Context
import org.json.JSONArray

object RepositorioFavoritos {

    private const val PREF_NOMBRE = "favoritos_store_dany"
    private const val CLAVE_LISTA = "favoritos"

    fun leer(context: Context): HashSet<String> {
        val prefs = context.getSharedPreferences(PREF_NOMBRE, Context.MODE_PRIVATE)
        val guardado = prefs.getString(CLAVE_LISTA, null) ?: return HashSet()
        val favoritos = HashSet<String>()
        try {
            val arreglo = JSONArray(guardado)
            for (i in 0 until arreglo.length()) {
                favoritos.add(arreglo.optString(i))
            }
        } catch (_: Exception) {
            // Si el JSON está corrupto se devuelve la lista vacía
        }
        return favoritos
    }

    fun guardar(context: Context, favoritos: Set<String>) {
        val arreglo = JSONArray()
        for (nombre in favoritos) {
            arreglo.put(nombre)
        }
        context.getSharedPreferences(PREF_NOMBRE, Context.MODE_PRIVATE)
            .edit()
            .putString(CLAVE_LISTA, arreglo.toString())
            .apply()
    }
}