package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PedidoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido)

        // Programamos el botón para que al hacer clic regrese al inicio
        val btnVolver = findViewById<Button>(R.id.btnVolverTienda)
        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Esto limpia las pantallas anteriores para que no se amontonen
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}
