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

        val VolverLogin = findViewById<TextView>(R.id.txtVolverLogin)
        VolverLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun mostrarDialogo() {
        val builder = AlertDialog.Builder(this)


        val title = "✅ Mensaje enviado"
        val message = "Revisa tu correo para continuar con la recuperación de tu cuenta."
        val fullMessage = "$title\n\n$message"

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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val dialog = builder.create()
        // Fondo blanco con bordes redondeados
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }

}
