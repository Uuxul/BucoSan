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
            val intent = Intent(this, RegistrarCuenta::class.java)
            startActivity(intent)
        }

        val textOlvidar = findViewById<TextView>(R.id.textOlvidar)
        textOlvidar.setOnClickListener {
            val intent = Intent(this, RecuperacionCuenta::class.java)
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
                    showDialog("⚠️ Campos vacíos", "Por favor ingresa tu correo y tu contraseña para continuar.")
                }
                email.isEmpty() -> {
                    showDialog("📧 Correo faltante", "No olvides escribir tu dirección de correo electrónico.")
                }
                password.isEmpty() -> {
                    showDialog("🔒 Contraseña faltante", "Debes ingresar tu contraseña para iniciar sesión.")
                }
                else -> {

                    val intent = Intent(this, SegundaP::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)


        val fullMessage = "$title\n\n$message"
        val spannable = android.text.SpannableString(fullMessage)


        spannable.setSpan(
            android.text.style.ForegroundColorSpan(0xFF000000.toInt()),
            0, fullMessage.length,
            android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        builder.setMessage(spannable)
        builder.setPositiveButton("Entendido") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }


}
