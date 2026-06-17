package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class PedidoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido)

        // 1. Vinculamos el botón usando MaterialButton para mantener tu línea estética premium
        val btnVolver = findViewById<MaterialButton>(R.id.btnVolverTienda)

        btnVolver.setOnClickListener {
            // 2. Redirigimos al usuario al catálogo principal (MainActivity)
            val intent = Intent(this, MainActivity::class.java).apply {
                // Estas banderas cierran absolutamente todas las pantallas de la compra actual
                // e inician la MainActivity completamente nueva con el carrito en ceros
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}