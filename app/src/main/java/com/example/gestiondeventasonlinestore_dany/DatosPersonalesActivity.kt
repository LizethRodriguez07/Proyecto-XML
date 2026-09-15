package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityDatosPersonalesBinding
import java.util.ArrayList

class DatosPersonalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatosPersonalesBinding

    private var listaCompra = ArrayList<Producto>()
    private var totalPagar = 0.0
    private var modoPerfil = false

    private val departamentos = UbicacionColombia.obtener()
    private var departamentoActual: String? = null
    private var municipioActual: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosPersonalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.ajustarBarrasSistema()

        binding.toolbarDatos.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        modoPerfil = intent.getBooleanExtra("modo_perfil", false)
        if (modoPerfil) {
            binding.toolbarDatos.title = getString(R.string.perfil_titulo)
            binding.toolbarDatos.subtitle = getString(R.string.perfil_subtitulo)
            binding.btenviardatos.text = getString(R.string.perfil_guardar)
        }

        // 1. Recepción del carrito y del total desde CarroComprasActivity
        val listaRecibida = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(
                "lista_final_pedido",
                ArrayList::class.java
            ) as? ArrayList<Producto>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("lista_final_pedido") as? ArrayList<Producto>
        }
        if (listaRecibida != null) {
            listaCompra = listaRecibida
            totalPagar = intent.getDoubleExtra("total_pagar", 0.0)
            if (totalPagar == 0.0) {
                totalPagar = listaCompra.sumOf { it.precio * it.cantidad }
            }
        }

        // 2. Selectores de ubicación y perfil guardado
        setupSelectores()
        limpiarErroresAlEscribir()
        cargarPerfil()
        restaurarSeleccion(savedInstanceState)

        // 3. Lógica del botón
        binding.btenviardatos.setOnClickListener {
            if (!validar()) return@setOnClickListener

            val cliente = Cliente(
                nombre = binding.etinombre.text.toString().trim(),
                apellidos = binding.etapellidos.text.toString().trim(),
                cedula = binding.idcedula.text.toString().trim(),
                celular = binding.Phone.text.toString().trim(),
                email = binding.Email.text.toString().trim(),
                departamento = departamentoActual ?: "",
                municipio = municipioActual ?: "",
                direccion = binding.etdireccionCompleta.text.toString().trim()
            )
            RepositorioPerfil.guardar(this, cliente)

            if (modoPerfil) {
                // 4. Solo guardar el perfil y volver
                Toast.makeText(this, R.string.perfil_guardado_msg, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // 5. SALTO A PANTALLA DE ÉXITO preservando el perfil guardado
                Toast.makeText(
                    this,
                    getString(R.string.toast_procesando_pedido, cliente.nombre),
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, PedidoActivity::class.java)
                intent.putExtra("nombre_cliente", cliente.nombre)
                intent.putExtra("lista_final_pedido", listaCompra)
                intent.putExtra("total_pagar", totalPagar)
                startActivity(intent)

                // Cerramos esta pantalla para que no pueda volver al formulario
                finish()
            }
        }
    }

    // ===================== SELECTORES DEPARTAMENTO / MUNICIPIO =====================

    private fun setupSelectores() {
        val nombresDepartamentos = departamentos.keys.toList()
        binding.spDepartamento.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, nombresDepartamentos)
        )
        binding.spDepartamento.setOnItemClickListener { _, _, position, _ ->
            departamentoActual = nombresDepartamentos[position]
            binding.tilDepartamento.error = null
            cargarMunicipios()
        }
    }

    private fun cargarMunicipios() {
        val municipios = departamentos[departamentoActual] ?: emptyList()
        binding.spMunicipio.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, municipios)
        )
        binding.spMunicipio.setText("")
        municipioActual = null
        binding.spMunicipio.setOnItemClickListener { _, _, position, _ ->
            municipioActual = municipios[position]
            binding.tilMunicipio.error = null
        }
    }

    // ===================== PERSISTENCIA DE ESTADO =====================

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("departamento_actual", departamentoActual)
        outState.putString("municipio_actual", municipioActual)
    }

    private fun restaurarSeleccion(estado: Bundle?) {
        val dep = estado?.getString("departamento_actual") ?: return
        if (dep !in departamentos) return

        departamentoActual = dep
        binding.spDepartamento.setText(dep)
        cargarMunicipios()

        val mun = estado.getString("municipio_actual")
        if (mun != null && departamentos[dep]?.contains(mun) == true) {
            binding.spMunicipio.setText(mun)
            municipioActual = mun
        }
    }

    // ===================== PERFIL =====================

    private fun cargarPerfil() {
        val perfil = RepositorioPerfil.leer(this) ?: return
        binding.etinombre.setText(perfil.nombre)
        binding.etapellidos.setText(perfil.apellidos)
        binding.idcedula.setText(perfil.cedula)
        binding.Phone.setText(perfil.celular)
        binding.Email.setText(perfil.email)
        binding.etdireccionCompleta.setText(perfil.direccion)
        if (perfil.departamento.isNotEmpty()) {
            departamentoActual = perfil.departamento
            binding.spDepartamento.setText(perfil.departamento)
            cargarMunicipios()
            binding.spMunicipio.setText(perfil.municipio)
            municipioActual = perfil.municipio
        }
    }

    // ===================== VALIDACIÓN =====================

    private fun limpiarErroresAlEscribir() {
        binding.etinombre.doAfterTextChanged { binding.tilNombre.error = null }
        binding.etapellidos.doAfterTextChanged { binding.tilApellidos.error = null }
        binding.idcedula.doAfterTextChanged { binding.tilCedula.error = null }
        binding.Phone.doAfterTextChanged { binding.tilCelular.error = null }
        binding.Email.doAfterTextChanged { binding.tilEmail.error = null }
        binding.spDepartamento.setOnClickListener { binding.tilDepartamento.error = null }
        binding.spMunicipio.setOnClickListener { binding.tilMunicipio.error = null }
        binding.etdireccionCompleta.doAfterTextChanged { binding.tilDireccion.error = null }
    }

    private fun validar(): Boolean {
        var ok = true
        val cedula = binding.idcedula.text.toString().trim()
        val celular = binding.Phone.text.toString().trim()
        val email = binding.Email.text.toString().trim()

        if (binding.etinombre.text.toString().trim().isEmpty()) {
            binding.tilNombre.error = getString(R.string.error_campo_vacio)
            ok = false
        }
        if (binding.etapellidos.text.toString().trim().isEmpty()) {
            binding.tilApellidos.error = getString(R.string.error_campo_vacio)
            ok = false
        }
        if (cedula.length != 10 || !cedula.all { it in '0'..'9' }) {
            binding.tilCedula.error = getString(R.string.error_cedula)
            ok = false
        }
        if (celular.length != 10 || !celular.all { it in '0'..'9' }) {
            binding.tilCelular.error = getString(R.string.error_celular)
            ok = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_email)
            ok = false
        }
        if (departamentoActual.isNullOrBlank()) {
            binding.tilDepartamento.error = getString(R.string.error_ubicacion)
            ok = false
        }
        if (municipioActual.isNullOrBlank()) {
            binding.tilMunicipio.error = getString(R.string.error_ubicacion)
            ok = false
        }
        if (binding.etdireccionCompleta.text.toString().trim().isEmpty()) {
            binding.tilDireccion.error = getString(R.string.error_campo_vacio)
            ok = false
        }
        return ok
    }
}