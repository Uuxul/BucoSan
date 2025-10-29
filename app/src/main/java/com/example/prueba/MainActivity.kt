package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prueba.SegundaP
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var chkRecordar: CheckBox
    private lateinit var button: Button
    private lateinit var txtRegistrar: TextView
    private lateinit var textOlvidar: TextView
    private lateinit var tvBienvenido: TextView
    private lateinit var btnCheckConnection: Button //checar conexion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        chkRecordar = findViewById(R.id.chkRecordar)
        button = findViewById(R.id.buttonLogin)
        txtRegistrar = findViewById(R.id.txtRegistrar)
        textOlvidar = findViewById(R.id.textOlvidar)
        tvBienvenido = findViewById(R.id.textView5) // Nuevo TextView opcional para "Bienvenido de nuevo"
        btnCheckConnection = findViewById(R.id.btnCheckConnection)// checar conexion

        // 🔹 Leer SharedPreferences
        val prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
        val correoGuardado = prefs.getString("correoUsuario", "")
        val nombreGuardado = prefs.getString("nombreUsuario", "")

        if (!correoGuardado.isNullOrEmpty() && !nombreGuardado.isNullOrEmpty()) {
            editEmail.setText(correoGuardado)
            chkRecordar.isChecked = true
            tvBienvenido.text = "Bienvenido de nuevo, $nombreGuardado"
        }

        txtRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistrarCuenta::class.java))
        }

        textOlvidar.setOnClickListener {
            startActivity(Intent(this, RecuperacionCuenta::class.java))
        }

        button.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            when {
                email.isEmpty() && password.isEmpty() -> showDialog("⚠️ Campos vacíos", "Por favor ingresa tu correo y tu contraseña para continuar.")
                email.isEmpty() -> showDialog("📧 Correo faltante", "No olvides escribir tu dirección de correo electrónico.")
                password.isEmpty() -> showDialog("🔒 Contraseña faltante", "Debes ingresar tu contraseña para iniciar sesión.")
                else -> {
                    // 🔑 MODIFICACIÓN 1: Limpia SharedPreferences si la casilla NO está marcada.
                    if (!chkRecordar.isChecked) {
                        val prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
                        prefs.edit().apply {
                            remove("correoUsuario")
                            remove("nombreUsuario")
                            apply() // Confirma la eliminación
                        }
                    }
                    loginUsuario(email, password)
                }
            }
            //checar conexion
            btnCheckConnection.setOnClickListener {
                checkDatabaseConnection()
            }
        }
    }



    private fun checkDatabaseConnection() {
        // Usa el nuevo script PHP: check_connection.php
        val url = Config.BASE_URL + "check_connection.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                showDialog("✅ Estado de la Conexión", response) // Muestra si el servidor dice 'OK'
            },
            Response.ErrorListener { error ->
                showDialog("❌ Error de Conexión", "No se pudo conectar al servidor: ${error.message}") // Muestra error de red
            }
        )

        queue.add(stringRequest)
    }

    private fun loginUsuario(correo: String, password: String) {
        val url = Config.BASE_URL + "login.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        // Extracción de todos los datos necesarios
                        val nombre = jsonResponse.getString("nombre")
                        val rol = jsonResponse.getString("rol")
                        val idUser = jsonResponse.getString("id")
                        val correoRecibido = jsonResponse.getString("correo")     // 🔑 NUEVA EXTRACCIÓN
                        val telefono = jsonResponse.getString("telefono")       // 🔑 NUEVA EXTRACCIÓN

                        // Guardar variables globales (si las usas)
                        // ... (código para guardar variables globales) ...

                        SessionManager.currentUserId = idUser
                        SessionManager.userName = nombre
                        SessionManager.userEmail = correoRecibido
                        SessionManager.userPhone = telefono // Opcional

                        if (chkRecordar.isChecked) {
                            val prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
                            prefs.edit().apply {
                                putString("correoUsuario", correoRecibido)
                                putString("nombreUsuario", nombre)
                                apply() // Confirma el guardado
                            }
                        }


                        // Guardar en SharedPreferences si está marcada la casilla
                        // ... (código de SharedPreferences) ...

                        // 2. Lógica de REDIRECCIÓN basada en el ROL
                        if (rol == "cliente") {
                            // ROL CLIENTE: Acceso concedido
                            val intent = Intent(this, SegundaP::class.java)

                            // 🔑 MODIFICACIÓN: Enviar los 4 datos con los nombres que SegundaP.kt espera
                            //intent.putExtra("ID_USUARIO", idUser)         // <-- ID del usuario
                            //intent.putExtra("NOMBRE_USUARIO", nombre)     // <-- Nombre
                            //intent.putExtra("CORREO_USUARIO", correoRecibido) // <-- Correo
                            //intent.putExtra("TELEFONO_USUARIO", telefono)     // <-- Teléfono

                            startActivity(intent)
                            finish()
                        } else if (rol == "admin") {
                            // ROL ADMIN: Acceso denegado
                            showDialog("Acceso Denegado", "Usuario Administrador. Acceda desde el Panel Web para gestionar contratos.")
                        } else {
                            // Rol no reconocido
                            showDialog("Error de Sistema", "Rol de usuario desconocido. Contacte a soporte.")
                        }

                    } else {
                        // Manejar errores (Contraseña incorrecta, etc.)
                        val message = jsonResponse.getString("message")
                        showDialog("⚠️ Error de Login", message)
                    }
                } catch (e: Exception) {
                    // Manejar errores de JSON o comunicación
                    showDialog("⚠️ Error de comunicación", "Respuesta no válida del servidor: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                // Este error ocurre por problemas de red o URL incorrecta
                showDialog("⚠️ Error de Red", "No se pudo comunicar con el servidor. Revisa tu conexión de red.")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["correo"] = correo
                params["password"] = password
                return params
            }
        }

        queue.add(stringRequest)
    }

    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        val fullMessage = "$title\n\n$message"
        val spannable = android.text.SpannableString(fullMessage)
        spannable.setSpan(android.text.style.ForegroundColorSpan(0xFF000000.toInt()), 0, fullMessage.length, android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        builder.setMessage(spannable)
        builder.setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(0xFFFF0000.toInt())
    }
}
