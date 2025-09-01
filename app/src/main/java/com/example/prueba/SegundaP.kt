package com.example.prueba

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SegundaP : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_segunda_p)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Recuperar el nombre desde la variable global de RegistrarCuenta
        val nombreUsuario = RegistrarCuenta.nombreUsuarioGlobal

        // ✅ Mostrarlo en un TextView (debes tenerlo en tu XML con id textViewNombre)
        val textView = findViewById<TextView>(R.id.nombre)
        textView.text = "BIENVENIDO, $nombreUsuario 👋"
    }
}
