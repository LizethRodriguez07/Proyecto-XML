package com.example.gestiondeventasonlinestore_dany

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestiondeventasonlinestore_dany.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AdaptadorProducto
    private var listaproductos = ArrayList<Producto>()
    private var carroCompras = ArrayList<Producto>()

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

        // 1. Configurar RecyclerView
        setupRecyclerView()

        // 2. Llenar la lista
        agregarProductos()

        // 3. Botón para ir al formulario de datos
        binding.btnVerCarrito.setOnClickListener {
            if (carroCompras.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor selecciona al menos un producto",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, DatosPersonalesActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvProductos.layoutManager = LinearLayoutManager(this)
        // Pasamos el listener para actualizar el contador si lo necesitas
        adapter = AdaptadorProducto(this, listaproductos, carroCompras) { cantidad ->
            binding.btnVerCarrito.text = "VER MI CARRITO ($cantidad)"
            invalidateOptionsMenu() // Esto actualiza el menú de los tres puntos

            // Aquí puedes actualizar algún texto si quieres
        }
        binding.rvProductos.adapter = adapter
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val itemCarrito = menu?.findItem(R.id.action_cart)
        if (carroCompras.isNotEmpty()) {
            itemCarrito?.title = "Carrito (${carroCompras.size})"
        } else {
            itemCarrito?.title = "Carrito"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun agregarProductos() {
        listaproductos.clear()
        listaproductos.add(
            Producto(
                "Nike Air Trainer",
                "Tallas: 39-42. Blanco/Gris",
                380000.0,
                R.drawable.niked
            )
        )
        listaproductos.add(
            Producto(
                "ADIDAS FORUM",
                "Tallas: 37-41. Negro/Blanco",
                350000.0,
                R.drawable.adidas4
            )
        )
        listaproductos.add(
            Producto(
                "PUMA STREET",
                "Tallas: 38-43. Azul/Gris",
                320000.0,
                R.drawable.puma6
            )
        )
        listaproductos.add(
            Producto(
                "NEW BALANCE 1300",
                "Tallas: 39-44. Café",
                280000.0,
                R.drawable.new6
            )
        )

        adapter.notifyDataSetChanged()
    }

    // --- LÓGICA DEL MENÚ ---

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                irAlCarrito()
                true
            }

            R.id.action_catalog -> {
                Toast.makeText(this, "Ya estás viendo el catálogo", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_profile -> {
                startActivity(Intent(this, DatosPersonalesActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun irAlCarrito() {
        if (carroCompras.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, CarroComprasActivity::class.java)
            // IMPORTANTE: Envía la lista para que el CarroComprasActivity la pueda ver
            intent.putExtra("lista_carrito", carroCompras)
            startActivity(intent)
        }
    }
}


