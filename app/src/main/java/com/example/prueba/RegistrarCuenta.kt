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
import org.json.JSONObject

class RegistrarCuenta : AppCompatActivity() {

    companion object {
        var correoUsuarioGlobal: String? = null

        // Variable global donde se guardará el nombre
        var nombreUsuarioGlobal: String? = null

        var telefonoUsuarioGlobal: String? = null

        var DireccionUsuarioGlobal: String? = null
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
        val direccion = findViewById<EditText>(R.id.Direccion)
        val password = findViewById<EditText>(R.id.passwordRegistro)
        val confPassword = findViewById<EditText>(R.id.confPasswordRegistro)
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)

        btnRegistro.setOnClickListener {
            val nombreText = nombre.text.toString().trim()
            val emailText = email.text.toString().trim()
            val telefonoText = telefono.text.toString().trim()
            val direcciontext = direccion.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val confPasswordText = confPassword.text.toString().trim()

            when {
                nombreText.isEmpty() || emailText.isEmpty() || telefonoText.isEmpty() || direcciontext.isEmpty()
                        || passwordText.isEmpty() || confPasswordText.isEmpty() -> {
                    mostrarDialogo("⚠️ Campos vacíos", "Por favor, completa todos los campos.")
                }

                passwordText != confPasswordText -> {
                    mostrarDialogo("⚠️ Contraseñas", "Las contraseñas no coinciden.")
                }

                telefonoText.length != 10 || !telefonoText.all { it.isDigit() } -> {
                    mostrarDialogo(
                        "⚠️ Teléfono",
                        "El número de teléfono debe contener exactamente 10 dígitos."
                    )
                }

                else -> {
                    correoUsuarioGlobal = emailText
                    nombreUsuarioGlobal = nombreText
                    telefonoUsuarioGlobal = telefonoText
                    DireccionUsuarioGlobal = direcciontext
                    registrarUsuario(nombreText, emailText, telefonoText, direcciontext,passwordText)
                }
            }
        }

    }

    private fun registrarUsuario(
        nombre: String,
        email: String,
        telefono: String,
        direccion: String,
        password: String
    ) {
        val url = Config.BASE_URL + "registrar.php"
        val queue = Volley.newRequestQueue(this)

        // Bloquear el botón mientras se procesa
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)
        btnRegistro.isEnabled = false

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                btnRegistro.isEnabled = true
                try {
                    val json = JSONObject(response)
                    val status = json.getString("status")
                    val msg = json.getString("msg")

                    if (status == "success") {
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        mostrarDialogo("⚠️ Error", msg)
                    }
                } catch (e: Exception) {
                    mostrarDialogo("⚠️ Error", "Respuesta inválida del servidor: $response")
                }
            },
            Response.ErrorListener { error ->
                btnRegistro.isEnabled = true // reactivar botón
                mostrarDialogo("⚠️ Conexión", "Error: ${error.message}")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nombre"] = nombre
                params["correo"] = email
                params["telefono"] = telefono
                params["direccion"] = direccion
                params["password"] = password
                return params
            }
        }

        // Evitar reintentos automáticos que dupliquen la petición
        stringRequest.retryPolicy = com.android.volley.DefaultRetryPolicy(
            0,
            0,
            com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

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
        builder.setPositiveButton("ACEPTAR") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }

}
