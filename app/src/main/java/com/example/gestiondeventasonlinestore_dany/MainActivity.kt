package com.example.gestiondeventasonlinestore_dany

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AdaptadorProducto
    private var listaproductos = ArrayList<Producto>()
    private var listaFiltrada = ArrayList<Producto>()
    private var marcaSeleccionada: String? = null
    private var carroCompras = ArrayList<Producto>()

    private val carroLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == RESULT_OK && data != null) {
            val listaRecibida = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data.getSerializableExtra(
                    "lista_carrito",
                    ArrayList::class.java
                ) as? ArrayList<Producto>
            } else {
                @Suppress("DEPRECATION")
                data.getSerializableExtra("lista_carrito") as? ArrayList<Producto>
            }

            if (listaRecibida != null) {
                carroCompras.clear()
                carroCompras.addAll(listaRecibida)
                adapter.notifyDataSetChanged()
                actualizarContadorCarrito()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Configurar Padding para barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarDrawer()
        setupNavigationView()

        // 1. Configurar RecyclerView
        setupRecyclerView()

        // 2. Buscador y filtros por marca
        configurarBuscador()

        // 3. Llenar la lista
        agregarProductos()

        // 3.1 Recuperar carrito y filtro tras rotación/cambio de configuración
        savedInstanceState?.let { estado ->
            val carroGuardado = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                estado.getSerializable(
                    "carro_compras_rotacion",
                    ArrayList::class.java
                ) as? ArrayList<Producto>
            } else {
                @Suppress("DEPRECATION")
                estado.getSerializable("carro_compras_rotacion") as? ArrayList<Producto>
            }
            val marcaGuardada = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                estado.getSerializable(
                    "filtro_marca_rotacion",
                    String::class.java
                ) as? String
            } else {
                @Suppress("DEPRECATION")
                estado.getSerializable("filtro_marca_rotacion") as? String
            }
            if (carroGuardado != null) {
                carroCompras.clear()
                carroCompras.addAll(carroGuardado)
            }
            if (marcaGuardada != null) {
                marcaSeleccionada = marcaGuardada
            }
        }
        adapter.notifyDataSetChanged()
        actualizarContadorCarrito()
        aplicarFiltros()

        // 3. Botón para ver el carrito
        binding.btnVerCarrito.setOnClickListener {
            if (carroCompras.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor selecciona al menos un producto",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                irAlCarrito()
            }
        }
    }

    private fun configurarDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        toggle.drawerArrowDrawable.color = Color.parseColor("#D4AF37")
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupNavigationView() {
        binding.navView.setNavigationItemSelectedListener { item ->
            binding.drawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.nav_catalogo -> {
                    // Ya estamos en el catálogo
                }

                R.id.nav_carrito -> {
                    irAlCarrito()
                }

                R.id.nav_pedidos -> {
                    startActivity(Intent(this, MisPedidosActivity::class.java))
                }

                R.id.nav_perfil -> {
                    startActivity(Intent(this, DatosPersonalesActivity::class.java))
                }

                R.id.nav_ayuda -> {
                    mostrarDialogo(
                        getString(R.string.ayuda_titulo),
                        getString(R.string.ayuda_mensaje)
                    )
                }

                R.id.nav_acerca -> {
                    mostrarDialogo(
                        getString(R.string.acerca_titulo),
                        getString(R.string.acerca_mensaje)
                    )
                }

                else -> {}
            }
            true
        }
    }

    private fun setupRecyclerView() {
        binding.rvProductos.layoutManager = LinearLayoutManager(this)
        adapter = AdaptadorProducto(this, listaFiltrada, carroCompras) {
            actualizarContadorCarrito()
        }
        binding.rvProductos.adapter = adapter
    }

    private fun configurarBuscador() {
        binding.etBuscar.doAfterTextChanged {
            aplicarFiltros()
        }

        binding.chipGroupMarca.setOnCheckedStateChangeListener { _, checkedIds ->
            marcaSeleccionada = when (checkedIds.firstOrNull()) {
                R.id.chipNike -> "Nike"
                R.id.chipAdidas -> "Adidas"
                R.id.chipPuma -> "Puma"
                R.id.chipNewBalance -> "New Balance"
                else -> null
            }
            aplicarFiltros()
        }
    }

    private fun aplicarFiltros() {
        val texto = binding.etBuscar.text?.toString()?.trim()?.lowercase() ?: ""

        listaFiltrada.clear()
        for (producto in listaproductos) {
            val coincideTexto = texto.isEmpty() ||
                producto.nomProducto.lowercase().contains(texto) ||
                producto.marca.lowercase().contains(texto)
            val coincideMarca = marcaSeleccionada == null ||
                producto.marca.equals(marcaSeleccionada, ignoreCase = true)

            if (coincideTexto && coincideMarca) {
                listaFiltrada.add(producto)
            }
        }

        adapter.notifyDataSetChanged()
        binding.tvSinResultados.visibility =
            if (listaFiltrada.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun actualizarContadorCarrito() {
        val unidades = carroCompras.sumOf { it.cantidad }
        binding.btnVerCarrito.text = "VER MI CARRITO ($unidades)"

        val itemCarrito = binding.navView.menu.findItem(R.id.nav_carrito)
        itemCarrito?.title = if (carroCompras.isEmpty()) {
            "Mi Carrito"
        } else {
            "Mi Carrito ($unidades)"
        }
    }

    private fun agregarProductos() {
        listaproductos.clear()
        listaproductos.add(
            Producto(
                "Nike Air Trainer",
                "Nike",
                "Tallas: 39-42. Blanco/Gris",
                380000.0,
                R.drawable.niked
            )
        )
        listaproductos.add(
            Producto(
                "ADIDAS FORUM",
                "Adidas",
                "Tallas: 37-41. Negro/Blanco",
                350000.0,
                R.drawable.adidas4
            )
        )
        listaproductos.add(
            Producto(
                "PUMA STREET",
                "Puma",
                "Tallas: 38-43. Azul/Gris",
                320000.0,
                R.drawable.puma6
            )
        )
        listaproductos.add(
            Producto(
                "NEW BALANCE 1300",
                "New Balance",
                "Tallas: 39-44. Café",
                280000.0,
                R.drawable.new6
            )
        )

        aplicarFiltros()
    }

    private fun irAlCarrito() {
        if (carroCompras.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, CarroComprasActivity::class.java)
            intent.putExtra("lista_carrito", carroCompras)
            carroLauncher.launch(intent)
        }
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        val view = layoutInflater.inflate(R.layout.dialog_mensaje, null)
        view.findViewById<TextView>(R.id.tvTituloDialogo).text = titulo
        view.findViewById<TextView>(R.id.tvMensajeDialogo).text = mensaje

        val dialog = AlertDialog.Builder(this).setView(view).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.findViewById<MaterialButton>(R.id.btnCerrarDialogo).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("carro_compras_rotacion", carroCompras)
        outState.putSerializable("filtro_marca_rotacion", marcaSeleccionada)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}