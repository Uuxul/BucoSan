package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response

class RegistrarCuenta : AppCompatActivity() {

    companion object {
        var correoUsuarioGlobal: String? = null
        // Variable global donde se guardará el nombre
        var nombreUsuarioGlobal: String? = null
    }

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
                telefonoText.length != 10 || !telefonoText.all { it.isDigit() } -> {
                    mostrarDialogo("⚠️ Teléfono", "El número de teléfono debe contener exactamente 10 dígitos.")
                }
                else -> {
                    correoUsuarioGlobal = emailText
                    nombreUsuarioGlobal = nombreText
                    registrarUsuario(nombreText, emailText, telefonoText, passwordText)
                }

            }
        }
    }

    private fun registrarUsuario(nombre: String, email: String, telefono: String, password: String) {
        val url = Config.BASE_URL + "registrar.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if (response.contains("✅ Registro exitoso")) {
                    // Mostrar mensaje de éxito
                    Toast.makeText(this, "Se ha enviado un enlace de verificación a tu correo electrónico", Toast.LENGTH_LONG).show()

                    // Redirigir al login
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    mostrarDialogo("⚠️ Error", response)
                }
            },
            Response.ErrorListener { error ->
                mostrarDialogo("⚠️ Conexión", "Error: ${error.message}")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nombre"] = nombre
                params["correo"] = email
                params["telefono"] = telefono
                params["password"] = password
                return params
            }
        }

        queue.add(stringRequest)
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
        builder.setPositiveButton("ACEPTAR") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }
}
