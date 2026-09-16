package com.example.gestiondeventasonlinestore_dany

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object RepositorioPedidos {

    private const val PREFS = "store_dany_pedidos"
    private const val KEY_LISTA = "pedidos_json"

    fun guardar(context: Context, pedido: Pedido) {
        val lista = ArrayList<Pedido>()
        lista.add(pedido)
        lista.addAll(leer(context))
        guardarLista(context, lista)
    }

    fun actualizarEstado(context: Context, id: Long, nuevoEstado: String) {
        val lista = leer(context).map {
            if (it.id == id) it.copy(estado = nuevoEstado) else it
        }
        guardarLista(context, ArrayList(lista))
    }

    fun eliminar(context: Context, id: Long) {
        val lista = leer(context).filterNot { it.id == id }
        guardarLista(context, ArrayList(lista))
    }

    fun leer(context: Context): List<Pedido> {
        val raw = prefs(context).getString(KEY_LISTA, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val lista = ArrayList<Pedido>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val productosArr = obj.getJSONArray("productos")
                val productos = ArrayList<Producto>()
                for (j in 0 until productosArr.length()) {
                    val p = productosArr.getJSONObject(j)
                    productos.add(
                        Producto(
                            nomProducto = p.optString("nomProducto"),
                            marca = p.optString("marca"),
                            descripcion = p.optString("descripcion"),
                            precio = p.optDouble("precio"),
                            imagen = p.optInt("imagen"),
                            tallaSeleccionada = p.optString("tallaSeleccionada"),
                            cantidad = p.optInt("cantidad", 1)
                        )
                    )
                }
                lista.add(
                    Pedido(
                        id = obj.optLong("id"),
                        nombreCliente = obj.optString("nombreCliente"),
                        productos = productos,
                        total = obj.optDouble("total"),
                        fecha = obj.optLong("fecha"),
                        estado = obj.optString("estado", "PENDIENTE")
                    )
                )
            }
            lista
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun guardarLista(context: Context, lista: ArrayList<Pedido>) {
        val json = JSONArray()
        for (p in lista) {
            val productos = JSONArray()
            for (prod in p.productos) {
                productos.put(
                    JSONObject().apply {
                        put("nomProducto", prod.nomProducto)
                        put("marca", prod.marca)
                        put("descripcion", prod.descripcion)
                        put("precio", prod.precio)
                        put("imagen", prod.imagen)
                        put("tallaSeleccionada", prod.tallaSeleccionada)
                        put("cantidad", prod.cantidad)
                    }
                )
            }
            json.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("nombreCliente", p.nombreCliente)
                    put("total", p.total)
                    put("fecha", p.fecha)
                    put("estado", p.estado)
                    put("productos", productos)
                }
            )
        }
        prefs(context).edit().putString(KEY_LISTA, json.toString()).apply()
    }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}