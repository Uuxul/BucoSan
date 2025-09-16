package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ActivatorCentaActivity : AppCompatActivity() {

    private var codigoVerificacion: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activar_cuenta)

        val etCodigo = findViewById<EditText>(R.id.etCodigo)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val tvReenviar = findViewById<TextView>(R.id.tvReenviar)

        // Obtenemos correo del usuario desde Registro
        val correoUsuario = intent.getStringExtra("correo") ?: ""

        // Generamos y enviamos código al correo al abrir la pantalla
        generarYEnviarCodigo(correoUsuario)

        // Botón Confirmar
        btnConfirmar.setOnClickListener {
            val codigoIngresado = etCodigo.text.toString().trim()
            when {
                codigoIngresado.isEmpty() -> mostrarDialogo("⚠️ Código vacío", "Ingresa el código.")
                codigoIngresado != codigoVerificacion -> mostrarDialogo("❌ Código incorrecto", "El código no coincide.")
                else -> mostrarDialogoConLogin("✅ Cuenta activada", "Tu cuenta ha sido activada correctamente.")
            }
        }

        // Texto "Reenviar código"
        tvReenviar.setOnClickListener {
            generarYEnviarCodigo(correoUsuario)
        }
    }

    private fun generarYEnviarCodigo(correo: String) {
        // Generamos código aleatorio de 4 dígitos
        codigoVerificacion = (1000..9999).random().toString()

        val url = Config.BASE_URL + "enviar_codigo.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if (response.contains("✅")) {
                    mostrarDialogo("✔️ Código enviado", "Revisa tu correo: $correo")
                } else {
                    mostrarDialogo("❌ Error", response)
                }
            },
            Response.ErrorListener { error ->
                mostrarDialogo("⚠️ Conexión", "Error: ${error.message}")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "correo" to correo,
                    "codigo" to codigoVerificacion
                )
            }
        }

        queue.add(stringRequest)
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
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
        builder.setPositiveButton("ACEPTAR") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }

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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }
}
