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


class MainActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var chkRecordar: CheckBox
    private lateinit var button: Button
    private lateinit var txtRegistrar: TextView
    private lateinit var textOlvidar: TextView
    private lateinit var tvBienvenido: TextView

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
                else -> loginUsuario(email, password)
            }
        }
    }

    private fun loginUsuario(correo: String, password: String) {
        val url = Config.BASE_URL + "login.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                if (response.contains("✅ Login exitoso")) {
                    val parts = response.split("|")
                    val nombre = if (parts.size > 1) parts[1] else ""
                    val idUser = if (parts.size > 2) parts[2] else ""

                    // Guardar variables globales
                    RegistrarCuenta.nombreUsuarioGlobal = nombre
                    RegistrarCuenta.correoUsuarioGlobal = correo

                    // 🔹 Guardar en SharedPreferences si está marcada la casilla
                    val prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE)
                    val editor = prefs.edit()
                    if (findViewById<CheckBox>(R.id.chkRecordar).isChecked) {
                        editor.putString("nombreUsuario", nombre)
                        editor.putString("correoUsuario", correo)
                    } else {
                        editor.clear()
                    }
                    editor.apply()

                    // Abrir SegundaP
                    val intent = Intent(this, SegundaP::class.java)
                    intent.putExtra("idUsuario", idUser)
                    intent.putExtra("nombreUsuario", nombre)
                    startActivity(intent)
                    finish()
                } else {
                    showDialog("⚠️ Error de login", response)
                }
            },
            Response.ErrorListener { error ->
                showDialog("⚠️ Error de Usuario", "El correo y la contraseña no se han registrado.")

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
