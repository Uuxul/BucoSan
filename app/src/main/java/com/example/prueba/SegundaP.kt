package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prueba.databinding.ItemPaqueteBinding
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray

class SegundaP : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var containerPaquetes: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda_p)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        containerPaquetes = findViewById(R.id.containerPaquetes)

        // NAVIGATION DRAWER
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // HEADER DEL NAV
        val headerView = navView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.headerName)
        val tvHeaderEmail = headerView.findViewById<TextView>(R.id.headerEmail)

        tvHeaderName.text = Globales.nombreUsuario ?: "Usuario"
        tvHeaderEmail.text = Globales.emailUsuario ?: "correo@correo.com"

        // OPCIONES DEL MENÚ
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_perfil -> startActivity(Intent(this, perfil::class.java))
                R.id.nav_carrito -> startActivity(Intent(this, perfil::class.java))
                R.id.nav_info -> startActivity(Intent(this, PagosActivity::class.java))
                R.id.nav_salir -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Cargar los paquetes del servidor
        cargarPaquetes()
    }

    // ============================================================
    //                   CARGAR PAQUETES DESDE PHP
    // ============================================================
    private fun cargarPaquetes() {

        val url = Config.BASE_URL + "VerPaquetes.php"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)

                    containerPaquetes.removeAllViews()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)

                        val nombre = obj.getString("nombre")
                        val precio = obj.getString("precio")
                        val velocidad = obj.getString("velocidad")

                        agregarPaquete(nombre, precio, velocidad)
                    }

                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar los paquetes", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    // ============================================================
    //                AGREGAR CADA PAQUETE A LA VISTA
    // ============================================================

    private fun agregarPaquete(nombre: String, precio: String, velocidad: String) {

        val view = LayoutInflater.from(this).inflate(R.layout.item_paquete, containerPaquetes, false)

        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvPrecio = view.findViewById<TextView>(R.id.tvPrecio)
        val tvVelocidad = view.findViewById<TextView>(R.id.tvVelocidad)
        val btnContratar = view.findViewById<Button>(R.id.btnContratar)

        tvNombre.text = nombre
        tvPrecio.text = "$$precio MXN"
        tvVelocidad.text = "$velocidad Mbps"

        btnContratar.setOnClickListener {

            val inflater = LayoutInflater.from(this)
            val dialogMesesView = inflater.inflate(R.layout.dialog_meses, null)

            val tvTitulo = dialogMesesView.findViewById<TextView>(R.id.tvTituloMeses)
            val tvDatos = dialogMesesView.findViewById<TextView>(R.id.tvDatosPaquete)
            val btnMes6 = dialogMesesView.findViewById<Button>(R.id.btnMes6)
            val btnMes12 = dialogMesesView.findViewById<Button>(R.id.btnMes12)
            val btnCancelar = dialogMesesView.findViewById<Button>(R.id.btnCancelarMeses)

            // Setea los datos reales
            tvTitulo.text = "Selecciona el plazo"
            tvDatos.text = "Paquete: $nombre\nPrecio: $$precio MXN"

            val dialogMeses = AlertDialog.Builder(this)
                .setView(dialogMesesView)
                .create()

            dialogMeses.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialogMeses.show()

            // ======================================
            // EVENTOS DE LOS BOTONES
            // ======================================

            btnMes6.setOnClickListener {
                dialogMeses.dismiss()
                mostrarDialogoConfirmacion(nombre, precio, "6 meses")
            }

            btnMes12.setOnClickListener {
                dialogMeses.dismiss()
                mostrarDialogoConfirmacion(nombre, precio, "12 meses")
            }

            btnCancelar.setOnClickListener {
                dialogMeses.dismiss()
            }
        }

        containerPaquetes.addView(view)
    }


    private fun mostrarDialogoConfirmacion(nombre: String, precio: String, meses: String) {

        val inflater = LayoutInflater.from(this)
        val dialogConfirmView = inflater.inflate(R.layout.dialog_custom, null)

        val titulo = dialogConfirmView.findViewById<TextView>(R.id.tvTitulo)
        val mensaje = dialogConfirmView.findViewById<TextView>(R.id.tvMensaje)
        val btnNo = dialogConfirmView.findViewById<Button>(R.id.btnNo)
        val btnSi = dialogConfirmView.findViewById<Button>(R.id.btnSi)

        titulo.text = "Confirmación"
        mensaje.text =
            "¿Seguro que quiere este paquete?\n\n" +
                    "Paquete: $nombre\n" +
                    "Precio: $$precio MXN\n" +
                    "Plazo: $meses"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogConfirmView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        btnNo.setOnClickListener { dialog.dismiss() }

        btnSi.setOnClickListener {
            dialog.dismiss()

            val intent = Intent(this, PagosActivity::class.java)
            intent.putExtra("paquete", nombre)
            intent.putExtra("precio", precio)
            intent.putExtra("meses", meses)
            startActivity(intent)

            Toast.makeText(this, "Paquete registrado con éxito", Toast.LENGTH_LONG).show()
        }
    }




    // ============================================================
    //   DIÁLOGO AL PRESIONAR "ATRÁS" PARA SALIR DE LA APP
    // ============================================================
    override fun onBackPressed() {
        super.onBackPressed()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Salir")
        builder.setMessage("¿Deseas salir de la aplicación?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            dialog.dismiss()
            startActivity(Intent(this, MainActivity::class.java))
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

}

