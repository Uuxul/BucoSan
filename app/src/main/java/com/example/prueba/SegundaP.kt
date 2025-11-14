package com.example.prueba

import android.content.Intent
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


        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        val headerView = navView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.headerName)
        val tvHeaderEmail = headerView.findViewById<TextView>(R.id.headerEmail)


        tvHeaderName.text = Globales.nombreUsuario ?: "Usuario"
        tvHeaderEmail.text = Globales.emailUsuario ?: "usuario@email.com"

        // es  lo que hay en nuestro menu
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_perfil -> {
                    val intent = Intent(this, perfil::class.java)
                    startActivity(intent)

                }
                R.id.nav_carrito ->
                {
                    val intent = Intent(this, perfil::class.java)
                    startActivity(intent)

                }
                R.id.nav_info -> {
                    val intent = Intent(this, PagosActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_salir -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
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
    //para que cuando le demos atras ya no salga solo asi
    override fun onBackPressed() {
        super.onBackPressed() //no causa error eso rojo no se porque salio xd
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Salir")
        builder.setMessage("¿Deseas salir de la aplicación?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)// nos manla al login
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // pues no lo cierra xd
        }
        builder.show()
    }


}
