package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
// IMPORTACIONES NECESARIAS PARA LA CONTRATACIÓN (VOLLEY)
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.HashMap




class SegundaP : AppCompatActivity() {

    // PROPIEDADES DE LA VISTA
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var rvPaquetes: RecyclerView

    // Variable para almacenar el ID del usuario logueado
    private var userId: Int = -1

    // URL de tu script PHP para la contratación (¡AJUSTA ESTO A TU SERVIDOR!)
    private val SERVER_URL = "http://TU_IP_AQUI/TU_CARPETA/contratar_paquete.php"

    // Clase de datos: Ahora incluye el ID numérico del paquete para la base de datos
    data class Paquete(val id: Int, val nombre: String, val velocidad: String, val precio: String)

    //contrato



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda_p)

        // 1. INICIALIZACIÓN DE VISTAS
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        rvPaquetes = findViewById(R.id.rvPaquetes)

        // 2. RECUPERACIÓN Y CARGA DE DATOS DEL USUARIO
        // Asumiendo que estos datos se pasan desde el Login/MainActivity
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario Invitado"
        val correoUsuario = intent.getStringExtra("CORREO_USUARIO") ?: "ejemplo@dominio.com"
        val telefonoUsuario = intent.getStringExtra("TELEFONO_USUARIO") ?: "N/D" // Asumimos que también se pasa el teléfono

        // ¡ID del usuario! Necesario para la contratación.
        val idUsuario = intent.getStringExtra("ID_USUARIO")?.toIntOrNull() ?: -1
        userId = idUsuario // Almacenamos el ID en la propiedad de la clase

        // Obtener y actualizar las vistas del nav_header.xml

        //val nav view nuevo, no se sabe que hace
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.headerName)
        val tvHeaderEmail = headerView.findViewById<TextView>(R.id.headerEmail)

        //se utiliza seasonmanager
        tvHeaderName.text = SessionManager.userName ?: "Usuario"
        tvHeaderEmail.text = SessionManager.userEmail ?: "usuario@email.com"


        // 3. LÓGICA DE LA BARRA DE NAVEGACIÓN (FUNCIONALIDAD NO ELIMINADA)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Listener para los ítems de la barra lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_perfil -> {
                    val intent = Intent(this, perfil::class.java)
                    // Enviamos los datos al perfil (Nombre, Correo y Teléfono)
                    intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                    intent.putExtra("CORREO_USUARIO", correoUsuario)
                    intent.putExtra("TELEFONO_USUARIO", telefonoUsuario)
                    startActivity(intent)
                }
                R.id.nav_info -> {
                    showDialog("Información", "AppUx versión prueba.")
                }
                R.id.nav_salir -> {
                    SessionManager.clearSession() // Limpia los datos
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }


        // 4. LÓGICA DEL RECYCLERVIEW (Los 4 paquetes solicitados con IDs)
        val listaPaquetes = listOf(
            Paquete(1, "PAQUETE BÁSICO", "25 MBPS", "$299.00 MXN"),   // ID 1
            Paquete(2, "PAQUETE ESTÁNDAR", "50 MBPS", "$499.00 MXN"),  // ID 2
            Paquete(3, "PAQUETE PREMIUM", "100 MBPS", "$699.00 MXN"), // ID 3
            Paquete(4, "PAQUETE GAMER", "250 MBPS", "$999.00 MXN")   // ID 4
        )

        rvPaquetes.layoutManager = LinearLayoutManager(this)
        rvPaquetes.adapter = PaqueteAdapter(listaPaquetes)
    }

    // Función de conexión HTTP para contratar el paquete
    private fun contratarPaquete(paquete: Paquete, userId: Int) {
        if (userId <= 0) {
            Toast.makeText(this, "Error: ID de usuario no válido. Intente reiniciar sesión.", Toast.LENGTH_LONG).show()
            return
        }

        val url = SERVER_URL

        // Usando Volley para la petición POST
        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                // Respuesta del servidor PHP
                if (response.trim() == "success") {
                    Toast.makeText(this, "¡Contrato exitoso! ${paquete.nombre} activado.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de red: No se pudo conectar al servidor.", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                // Parámetros que se envían al script contratar_paquete.php
                params["usuario_id"] = userId.toString()
                params["paquete_id"] = paquete.id.toString()
                return params
            }
        }

        // Agrega la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(stringRequest)
    }

    // Muestra un cuadro de diálogo (usado para R.id.nav_info)
    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // Lógica del botón de atrás (para cerrar el Drawer o preguntar si desea salir)
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START) // Cierra el Drawer si está abierto
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Salir")
            builder.setMessage("¿Deseas salir de la aplicación?")
            builder.setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    // Adaptador: Conecta los datos Paquete.kt con la plantilla item_paquete.xml
    private inner class PaqueteAdapter(private val listaPaquetes: List<Paquete>) :
        RecyclerView.Adapter<PaqueteAdapter.PaqueteViewHolder>() {

        inner class PaqueteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNombre: TextView = itemView.findViewById(R.id.tvNombrePaquete)
            val tvVelocidad: TextView = itemView.findViewById(R.id.tvVelocidad)
            val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
            val btnContratar: Button = itemView.findViewById(R.id.btnContratar)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaqueteViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_paquete, parent, false)
            return PaqueteViewHolder(view)
        }

        override fun onBindViewHolder(holder: PaqueteViewHolder, position: Int) {
            val paqueteActual = listaPaquetes[position]

            // Asignación de datos
            holder.tvNombre.text = paqueteActual.nombre
            holder.tvVelocidad.text = paqueteActual.velocidad
            holder.tvPrecio.text = paqueteActual.precio

            // 🔑 LÓGICA CORREGIDA DEL BOTÓN CONTRATAR:
            // Ahora inicia la pantalla de Confirmación (ConfirmarContrato.kt)
            holder.btnContratar.setOnClickListener {
                // Obtener datos de usuario de las propiedades de la clase principal
                val nombreUsuario = this@SegundaP.intent.getStringExtra("NOMBRE_USUARIO") ?: ""
                val correoUsuario = this@SegundaP.intent.getStringExtra("CORREO_USUARIO") ?: ""
                val telefonoUsuario = this@SegundaP.intent.getStringExtra("TELEFONO_USUARIO") ?: ""

                val intent = Intent(holder.itemView.context, ConfirmarContrato::class.java)

                // PASAR TODOS LOS DATOS
                intent.putExtra("NOMBRE_PAQUETE", paqueteActual.nombre)
                intent.putExtra("VELOCIDAD", paqueteActual.velocidad)
                intent.putExtra("PRECIO", paqueteActual.precio)

                // 🔑 AÑADE ESTAS DOS LÍNEAS NUEVAS Y CRÍTICAS
                intent.putExtra("ID_USUARIO", this@SegundaP.intent.getStringExtra("ID_USUARIO") ?: "-1")
                intent.putExtra("PAQUETE_ID", paqueteActual.id) // <-- ENVÍA EL ID NUMÉRICO

                // Datos del usuario
                intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                intent.putExtra("CORREO_USUARIO", correoUsuario)
                intent.putExtra("TELEFONO_USUARIO", telefonoUsuario)

                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return listaPaquetes.size
        }
    }
}


