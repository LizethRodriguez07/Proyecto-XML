package com.example.gestiondeventasonlinestore_dany

import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class TerminosCondicionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminos)

        val tvContenido = findViewById<TextView>(R.id.tvTerminosContenido)
        tvContenido.setText(
            Html.fromHtml(
                getString(R.string.terminos_contenido),
                Html.FROM_HTML_MODE_COMPACT
            )
        )

        findViewById<MaterialToolbar>(R.id.toolbarTerminos).setNavigationOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        findViewById<MaterialButton>(R.id.btnAceptarTerminos).setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        findViewById<MaterialButton>(R.id.btnCancelarTerminos).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}