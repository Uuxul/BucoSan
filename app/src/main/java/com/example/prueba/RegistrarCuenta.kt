package com.example.prueba

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarCuenta : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_cuenta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nombre = findViewById<EditText>(R.id.nombreRegistro)
        val email = findViewById<EditText>(R.id.emailRegistro)
        val telefono = findViewById<EditText>(R.id.Telefono)
        val password = findViewById<EditText>(R.id.passwordRegistro)
        val confPassword = findViewById<EditText>(R.id.confPasswordRegistro)
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)

        btnRegistro.setOnClickListener {
            val nombreText = nombre.text.toString().trim()
            val emailText = email.text.toString().trim()
            val telefonoText = telefono.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val confPasswordText = confPassword.text.toString().trim()

            when {
                nombreText.isEmpty() || emailText.isEmpty() || telefonoText.isEmpty()
                        || passwordText.isEmpty() || confPasswordText.isEmpty() -> {

                    mostrarDialogo("⚠️ Campos vacíos", "Por favor, completa todos los campos.")
                }
                passwordText != confPasswordText -> {

                    mostrarDialogo("⚠️ Contraseñas", "Las contraseñas no coinciden.")
                }
                else -> {

                    mostrarDialogo("✅ Usuario agregado", "El usuario ha sido registrado correctamente.")
                }
            }
        }
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)


        val fullMessage = "$titulo\n\n$mensaje"
        val spannable = android.text.SpannableString(fullMessage)
        spannable.setSpan(
            android.text.style.ForegroundColorSpan(0xFF000000.toInt()), // negro
            0,
            fullMessage.length,
            android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        builder.setMessage(spannable)
        builder.setPositiveButton("ACEPTAR") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }
}
