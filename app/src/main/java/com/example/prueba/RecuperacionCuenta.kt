package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RecuperacionCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperacion_cuenta)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailRecuperacion = findViewById<EditText>(R.id.emailRecuperacion)
        val btnRecuperar = findViewById<Button>(R.id.btnRecuperar)

        btnRecuperar.setOnClickListener {

            mostrarDialogo()
        }
    }

    private fun mostrarDialogo() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("✅ Mensaje enviado")
        builder.setMessage("Revisa tu correo para continuar con la recuperación de tu cuenta.")

        builder.setPositiveButton("ACEPTAR") { dialog, _ ->
            dialog.dismiss()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
