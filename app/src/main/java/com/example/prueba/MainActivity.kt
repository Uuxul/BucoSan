package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtRegistrar = findViewById<TextView>(R.id.txtRegistrar)

        txtRegistrar.setOnClickListener {
            val intent = Intent(this, RegistrarCuenta::class.java) // aquí va la Activity de registro
            startActivity(intent)
        }

        val textOlvidar = findViewById<TextView>(R.id.textOlvidar)

        textOlvidar.setOnClickListener {
            val intent = Intent(this, RecuperacionCuenta::class.java) // aquí va la Activity de registro
            startActivity(intent)
        }


        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val button = findViewById<Button>(R.id.buttonLogin)

        button.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            when {
                email.isEmpty() && password.isEmpty() -> {
                    showDialog("Ambas casillas están vacías")
                }
                email.isEmpty() -> {
                    showDialog("El campo correo está vacío")
                }
                password.isEmpty() -> {
                    showDialog("El campo contraseña está vacío")
                }
                else -> {
                    // PASAMOS A PANTALLA CUANDO ESTA LLENO LOS DOS CAMPOS
                    val intent = Intent(this, SegundaP::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun showDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("APP MOVILE")
        builder.setMessage(message)
        builder.setPositiveButton("✖") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}
