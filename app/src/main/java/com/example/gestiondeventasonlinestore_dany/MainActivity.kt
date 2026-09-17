package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.view.GravityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AdaptadorProducto
    private var listaproductos = ArrayList<Producto>()
    private var listaFiltrada = ArrayList<Producto>()
    private var marcaSeleccionada: String? = null
    private var carroCompras = ArrayList<Producto>()
    private val favoritos = HashSet<String>()
    private var soloFavoritos = false

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

    private val detalleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        sincronizarFavoritos()
        val data = result.data
        if (result.resultCode == RESULT_OK && data != null) {
            val producto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data.getSerializableExtra(
                    "producto_detalle",
                    Producto::class.java
                ) as? Producto
            } else {
                @Suppress("DEPRECATION")
                data.getSerializableExtra("producto_detalle") as? Producto
            }
            if (producto != null) {
                val indice = carroCompras.indexOfFirst {
                    it.nomProducto == producto.nomProducto &&
                        it.tallaSeleccionada == producto.tallaSeleccionada
                }
                if (indice != -1) {
                    carroCompras[indice].cantidad++
                } else {
                    carroCompras.add(producto)
                }
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
        binding.root.ajustarBarrasSistema()

        configurarDrawer()
        setupNavigationView()
        setupPromociones()

        favoritos.clear()
        favoritos.addAll(RepositorioFavoritos.leer(this))

        // 1. Configurar RecyclerView
        setupRecyclerView()

        // 2. Buscador y filtros por marca
        configurarBuscador()

        binding.chipFavoritos.setOnCheckedChangeListener { _, checked ->
            soloFavoritos = checked
            if (soloFavoritos) {
                mostrarVistaCatalogo()
            }
            aplicarFiltros()
        }

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

        // 2.1 Vista inicial (Inicio por defecto)
        if (savedInstanceState?.getBoolean("vista_inicio", true) != false) {
            mostrarVistaInicio()
        } else {
            mostrarVistaCatalogo()
        }

        // 3. Botón para ver el carrito
        binding.btnVerCarrito.setOnClickListener {
            if (carroCompras.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.toast_selecciona_producto),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                irAlCarrito()
            }
        }

        // 3.1 Botón "Ver catálogo" desde Inicio
        binding.btnVerCatalogo.setOnClickListener {
            mostrarVistaCatalogo()
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
        val flechaMenu = DrawerArrowDrawable(this).apply {
            color = Color.parseColor("#D4AF37")
            setBarThickness(9f)
            setBarLength(72f)
            setGapSize(15f)
        }
        toggle.drawerArrowDrawable = flechaMenu
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupNavigationView() {
        binding.navView.setNavigationItemSelectedListener { item ->
            binding.drawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.nav_inicio -> {
                    mostrarVistaInicio()
                }

                R.id.nav_catalogo -> {
                    mostrarVistaCatalogo()
                }

                R.id.nav_carrito -> {
                    irAlCarrito()
                }

                R.id.nav_favoritos -> {
                    if (binding.chipFavoritos.isChecked) {
                        binding.chipFavoritos.isChecked = false
                    } else {
                        mostrarVistaCatalogo()
                        binding.chipFavoritos.isChecked = true
                    }
                }

                R.id.nav_pedidos -> {
                    startActivity(Intent(this, MisPedidosActivity::class.java))
                }

                R.id.nav_perfil -> {
                    startActivity(
                        Intent(this, DatosPersonalesActivity::class.java)
                            .putExtra("modo_perfil", true)
                    )
                }

                R.id.nav_ayuda -> {
                    startActivity(Intent(this, AyudaActivity::class.java))
                }

                R.id.nav_acerca -> {
                    startActivity(Intent(this, AcercaDeActivity::class.java))
                }

                else -> {}
            }
            true
        }
    }

    private fun setupRecyclerView() {
        binding.rvProductos.layoutManager = LinearLayoutManager(this)
        adapter = AdaptadorProducto(this, listaFiltrada, carroCompras, favoritos,
            onCartUpdated = { actualizarContadorCarrito() },
            onItemClick = { producto ->
                detalleLauncher.launch(
                    Intent(this, DetalleProductoActivity::class.java)
                        .putExtra("producto", producto)
                )
            },
            onToggleFavorito = { producto ->
                toggleFavorito(producto)
            }
        )
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
                R.id.chipReebok -> "Reebok"
                else -> null
            }
            aplicarFiltros()
        }
    }

    private fun setupPromociones() {
        binding.rvPromos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPromos.adapter = AdaptadorPromociones(
            listOf(
                Promocion(
                    getString(R.string.promo_tag_promo),
                    getString(R.string.promo_1_titulo),
                    getString(R.string.promo_1_sub)
                ),
                Promocion(
                    getString(R.string.promo_tag_servicio),
                    getString(R.string.promo_2_titulo),
                    getString(R.string.promo_2_sub)
                ),
                Promocion(
                    getString(R.string.promo_tag_garantia),
                    getString(R.string.promo_3_titulo),
                    getString(R.string.promo_3_sub)
                )
            )
        )
    }

    private fun mostrarVistaInicio() {
        binding.heroCatalogo.visibility = android.view.View.VISIBLE
        binding.rvPromos.visibility = android.view.View.VISIBLE
        binding.filaConfianza.visibility = android.view.View.VISIBLE
        binding.btnVerCatalogo.visibility = android.view.View.VISIBLE
        binding.filtrosContainer.visibility = android.view.View.GONE
        binding.rvProductos.visibility = android.view.View.GONE
        binding.tvSinResultados.visibility = android.view.View.GONE
        binding.btnVerCarrito.visibility = android.view.View.GONE
        binding.navView.menu.findItem(R.id.nav_inicio)?.isChecked = true
        binding.rvProductos.scrollToPosition(0)
    }

    private fun mostrarVistaCatalogo() {
        binding.heroCatalogo.visibility = android.view.View.GONE
        binding.rvPromos.visibility = android.view.View.GONE
        binding.filaConfianza.visibility = android.view.View.GONE
        binding.btnVerCatalogo.visibility = android.view.View.GONE
        binding.filtrosContainer.visibility = android.view.View.VISIBLE
        binding.rvProductos.visibility = android.view.View.VISIBLE
        binding.btnVerCarrito.visibility = android.view.View.VISIBLE
        binding.navView.menu.findItem(R.id.nav_catalogo)?.isChecked = true
        binding.rvProductos.scrollToPosition(0)
        binding.rvProductos.scheduleLayoutAnimation()
        aplicarFiltros()
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
            val coincideFavorito = !soloFavoritos || favoritos.contains(producto.nomProducto)

            if (coincideTexto && coincideMarca && coincideFavorito) {
                listaFiltrada.add(producto)
            }
        }

        adapter.notifyDataSetChanged()
        binding.tvSinResultados.visibility =
            if (listaFiltrada.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        if (listaFiltrada.isEmpty()) {
            binding.tvSinResultados.text = when {
                soloFavoritos && favoritos.isEmpty() -> getString(R.string.fav_vacio_sub)
                soloFavoritos -> getString(R.string.fav_sin_resultados)
                else -> getString(R.string.sin_resultados)
            }
        }
    }

    private fun toggleFavorito(producto: Producto) {
        val clave = producto.nomProducto
        val estaba = favoritos.contains(clave)
        if (estaba) {
            favoritos.remove(clave)
            Toast.makeText(
                this,
                getString(R.string.toast_favorito_quitado, producto.nomProducto),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            favoritos.add(clave)
            Toast.makeText(
                this,
                getString(R.string.toast_favorito_anadido, producto.nomProducto),
                Toast.LENGTH_SHORT
            ).show()
        }
        RepositorioFavoritos.guardar(this, favoritos)
        if (soloFavoritos && favoritos.isEmpty()) {
            binding.chipFavoritos.isChecked = false
        }
        aplicarFiltros()
    }

    private fun sincronizarFavoritos() {
        favoritos.clear()
        favoritos.addAll(RepositorioFavoritos.leer(this))
        if (soloFavoritos && favoritos.isEmpty()) {
            binding.chipFavoritos.isChecked = false
        }
        adapter.notifyDataSetChanged()
    }

    private fun actualizarContadorCarrito() {
        val unidades = carroCompras.sumOf { it.cantidad }
        binding.btnVerCarrito.text = getString(R.string.main_ver_carrito_conteo, unidades)

        binding.tvContadorBanner.text = unidades.toString()
        binding.contadorBannerCard.visibility =
            if (unidades > 0) android.view.View.VISIBLE else android.view.View.GONE

        val itemCarrito = binding.navView.menu.findItem(R.id.nav_carrito)
        itemCarrito?.title = if (carroCompras.isEmpty()) {
            getString(R.string.carro_titulo)
        } else {
            getString(R.string.main_mi_carrito_conteo, unidades)
        }
    }

    private fun agregarProductos() {
        listaproductos.clear()
        listaproductos.add(
            Producto(
                "Nike Air Trainer",
                "Nike",
                "Tallas: 37-42. Blanco/Gris",
                380000.0,
                R.drawable.niked
            )
        )
        listaproductos.add(
            Producto(
                "ADIDAS FORUM",
                "Adidas",
                "Tallas: 37-42. Negro/Blanco",
                350000.0,
                R.drawable.adidas4
            )
        )
        listaproductos.add(
            Producto(
                "PUMA STREET",
                "Puma",
                "Tallas: 37-42. Azul/Gris",
                320000.0,
                R.drawable.puma6
            )
        )
        listaproductos.add(
            Producto(
                "NEW BALANCE 1300",
                "New Balance",
                "Tallas: 37-42. Café",
                280000.0,
                R.drawable.new6
            )
        )
        listaproductos.add(
            Producto(
                "REEBOK CLASSIC",
                "Reebok",
                "Tallas: 37-42. Blanco/Verde",
                300000.0,
                R.drawable.reebok_classic
            )
        )

        aplicarFiltros()
    }

    private fun irAlCarrito() {
        if (carroCompras.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_carrito_vacio), Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, CarroComprasActivity::class.java)
            intent.putExtra("lista_carrito", carroCompras)
            carroLauncher.launch(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("carro_compras_rotacion", carroCompras)
        outState.putSerializable("filtro_marca_rotacion", marcaSeleccionada)
        outState.putBoolean(
            "vista_inicio",
            binding.heroCatalogo.visibility == android.view.View.VISIBLE
        )
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