package com.example.prueba

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView


class SegundaP : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda_p)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Configurar toggle del Drawer
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 🔹 Aquí obtenemos la vista del header
        val headerView = navView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.headerName)
        val tvHeaderEmail = headerView.findViewById<TextView>(R.id.headerEmail)

        // 🔹 Y aquí asignamos el nombre y correo de la variable global
        tvHeaderName.text = RegistrarCuenta.nombreUsuarioGlobal ?: "Usuario"
        tvHeaderEmail.text = RegistrarCuenta.correoUsuarioGlobal ?: "usuario@email.com"

        // Listener de items del Navigation Drawer
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_perfil -> { /* Abrir PerfilActivity */ }
                R.id.nav_info -> {
                    showDialog("Información", "AppUx versión prueba")
                }
                R.id.nav_salir -> finish()
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

}
