package com.example.prueba

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale

// 🔑 IMPORTANTE: Usa tu Config.BASE_URL real (Asumo que la tienes definida en Config.kt)
private val SERVER_URL_CONTRATO = Config.BASE_URL + "contratar_paquete.php"

class ConfirmarContrato : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmar_contrato)

        val intent = intent

        // 1. Recepción de datos del paquete y el usuario
        val nombrePaquete = intent.getStringExtra("NOMBRE_PAQUETE") ?: ""
        val velocidad = intent.getStringExtra("VELOCIDAD") ?: ""
        val precio = intent.getStringExtra("PRECIO") ?: "0.00"

        // 🔑 Necesitas estos datos de usuario y el ID numérico del paquete
        val idUsuario = SessionManager.currentUserId ?: "-1"
        val paqueteId = intent.getIntExtra("PAQUETE_ID", 0) // <--- CRÍTICO: Recibimos el ID numérico

        val nombreCliente = SessionManager.userName ?: "Datos incompletos"
        val correoCliente = SessionManager.userEmail ?: "Datos incompletos"
        val telefonoCliente = SessionManager.userPhone ?: "N/D"

        // 2. Asignación de datos a la vista
        findViewById<TextView>(R.id.tvNombrePaqueteConf).text = nombrePaquete
        findViewById<TextView>(R.id.tvDetallesPaqueteConf).text = "$velocidad | $precio"
        findViewById<TextView>(R.id.tvNombreClienteConf).text = nombreCliente
        findViewById<TextView>(R.id.tvCorreoClienteConf).text = correoCliente
        findViewById<TextView>(R.id.tvTelefonoClienteConf).text = telefonoCliente

        // 3. Lógica del Botón Finalizar (MODIFICADA)
        findViewById<Button>(R.id.btnFinalizarContrato).setOnClickListener {

            if (idUsuario != "-1" && paqueteId > 0) {
                // 🔑 Si los datos son válidos, llama a la función que usa Volley
                registrarContratoEnDB(idUsuario, paqueteId, nombrePaquete)
            } else {
                Toast.makeText(this, "Error: Datos incompletos. Intente de nuevo.", Toast.LENGTH_LONG).show()
            }
        }
    } // FIN del onCreate

    // ---------------------------------------------------------------------
    // 🔑 FUNCIONES AÑADIDAS AQUÍ (fuera del onCreate)
    // ---------------------------------------------------------------------

    private fun registrarContratoEnDB(userId: String, paqueteId: Int, nombrePaquete: String) {

        val stringRequest = object : StringRequest(
            Method.POST, SERVER_URL_CONTRATO,
            { response ->
                if (response.trim() == "success") {
                    Toast.makeText(this, "Contrato registrado con éxito en la DB.", Toast.LENGTH_LONG).show()

                    // Si es exitoso, ahora sí guardamos en SharedPreferences y cerramos
                    saveContractToPrefs()
                } else {
                    // El servidor reportó un error (Ej: "Error: El usuario ya tiene un paquete activo...")
                    Toast.makeText(this, "Error de Contrato: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de Red: No se pudo conectar al servidor de contratos.", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                // 🔑 CLAVE: Usa el ID del SessionManager. (Reemplaza tu antiguo 'usuario_id' y el 'paqueteId')
                params["usuario_id"] = SessionManager.currentUserId ?: "0"
                params["paquete_id"] = paqueteId.toString() // Usa la variable paqueteId de esta actividad.

                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    // 🔑 Función de SharedPreferences para guardar datos locales
    private fun saveContractToPrefs() {
        val prefs = getSharedPreferences("ContratoPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val fechaContrato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Obtenemos los datos del paquete que recibimos en el Intent
        val nombrePaquete = intent.getStringExtra("NOMBRE_PAQUETE") ?: ""
        val velocidad = intent.getStringExtra("VELOCIDAD") ?: ""
        val precio = intent.getStringExtra("PRECIO") ?: ""

        editor.putString("CONTRATADO", "true")
        editor.putString("CONTRATO_NOMBRE", nombrePaquete)
        editor.putString("CONTRATO_VELOCIDAD", velocidad)
        editor.putString("CONTRATO_PRECIO", precio)
        editor.putString("CONTRATO_FECHA_INICIO", fechaContrato)
        editor.putString("CONTRATO_FECHA_FIN", "")
        editor.apply()

        // Cerramos la actividad después de guardar
        finish()
    }
}