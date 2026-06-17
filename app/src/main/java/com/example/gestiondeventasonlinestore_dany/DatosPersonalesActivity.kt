package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class DatosPersonalesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_personales)

        // 1. Referencia de los campos
        val etNombre = findViewById<TextInputEditText>(R.id.etinombre)
        val etApellido = findViewById<TextInputEditText>(R.id.etapellidos)
        val etCedula = findViewById<TextInputEditText>(R.id.idcedula)
        val etCelular = findViewById<TextInputEditText>(R.id.Phone)
        val etEmail = findViewById<TextInputEditText>(R.id.Email)
        val etDireccion = findViewById<TextInputEditText>(R.id.etdireccion_completa)
        val btnConfirmar = findViewById<MaterialButton>(R.id.btenviardatos)

        // 2. Lógica del botón
        btnConfirmar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val cedula = etCedula.text.toString().trim()
            val celular = etCelular.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()

            // 3. Validación: Si alguno está vacío, avisamos
            if (nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty() ||
                celular.isEmpty() || email.isEmpty() || direccion.isEmpty()) {

                Toast.makeText(this, "⚠️ Por favor rellena todos los campos", Toast.LENGTH_SHORT).show()

            } else {
                // 4. SALTO A PANTALLA DE ÉXITO (Sin WhatsApp por ahora)
                Toast.makeText(this, "✅ Procesando pedido de $nombre...", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, PedidoActivity::class.java)
                startActivity(intent)

                // Cerramos esta pantalla para que no pueda volver al formulario
                finish()
            }
        }
    }
}