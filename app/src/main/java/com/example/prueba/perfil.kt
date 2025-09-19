package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class perfil : AppCompatActivity() {

    private lateinit var nombre: TextView
    private lateinit var correo: TextView
    private lateinit var telefono: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        nombre = findViewById(R.id.txtUsuario)
        correo = findViewById(R.id.txtCorreo)
        telefono = findViewById(R.id.txtTelefono)

        // Mostrar datos del usuario
        nombre.text = RegistrarCuenta.nombreUsuarioGlobal ?: "Usuario"
        correo.text = RegistrarCuenta.correoUsuarioGlobal ?: "usuario@email.com"
        telefono.text = RegistrarCuenta.telefonoUsuarioGlobal ?: "+52 999-123-4567"

        val btn = findViewById<Button>(R.id.btnregresar)

        btn.setOnClickListener{
            finish()
        }

    }
}
