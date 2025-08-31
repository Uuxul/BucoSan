package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ActivarCuenta : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activar_cuenta)

        // Referencias al layout
        val etCodigo = findViewById<EditText>(R.id.etCodigo)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val tvReenviar = findViewById<TextView>(R.id.tvReenviar)

        // Botón Confirmar
        btnConfirmar.setOnClickListener {
            val codigo = etCodigo.text.toString().trim()
            when {
                codigo.isEmpty() -> mostrarDialogo("⚠️ Código vacío", "Por favor, ingresa el código de activación.")
                else -> mostrarDialogoConLogin("✅ Cuenta activada", "Tu cuenta ha sido activada correctamente.")
            }
        }

        // Texto "Reenviar código"
        tvReenviar.setOnClickListener {
            mostrarDialogo("🔄 Código reenviado", "Se ha reenviado el código a tu correo electrónico.")
        }
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        val fullMessage = "$titulo\n\n$mensaje"
        val spannable = android.text.SpannableString(fullMessage)
        spannable.setSpan(
            android.text.style.ForegroundColorSpan(0xFF000000.toInt()), // color negro
            0,
            fullMessage.length,
            android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        builder.setMessage(spannable)
        builder.setPositiveButton("ACEPTAR") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }

    // Este dialogo cierra y manda al Login
    private fun mostrarDialogoConLogin(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        val fullMessage = "$titulo\n\n$mensaje"
        val spannable = android.text.SpannableString(fullMessage)
        spannable.setSpan(
            android.text.style.ForegroundColorSpan(0xFF000000.toInt()),
            0,
            fullMessage.length,
            android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        builder.setMessage(spannable)
        builder.setPositiveButton("ACEPTAR") { dialog, _ ->
            dialog.dismiss()
            // Abrimos LoginActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // cerramos ActivarCuenta
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }
}
