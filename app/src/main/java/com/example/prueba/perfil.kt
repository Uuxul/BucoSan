package com.example.prueba

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale
import android.util.Log

// 🔑 IMPORTANTE: Usa tu Config.BASE_URL real
private val SERVER_URL_VERIFICACION = Config.BASE_URL + "verificar_contrato.php" // Nuevo script
private val SERVER_URL_CANCELACION = Config.BASE_URL + "cancelar_paquete.php"

class perfil : AppCompatActivity() {

    // Vistas existentes
    private lateinit var txtUsuario: TextView
    private lateinit var txtCorreo: TextView
    private lateinit var txtTelefono: TextView
    private lateinit var btnRegresar: Button

    // Vistas nuevas para el contrato (asumiendo que las agregaste en activity_perfil.xml)
    private lateinit var tvStatusContrato: TextView
    private lateinit var btnCancelarContrato: Button

    // Variables de datos (usando Intent en lugar de variables globales estáticas)
    private var idUsuario: String = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // 1. INICIALIZACIÓN DE VISTAS
        txtUsuario = findViewById(R.id.txtUsuario)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtTelefono = findViewById(R.id.txtTelefono)
        btnRegresar = findViewById(R.id.btnregresar) // O btnregresar

        // Vistas de contrato (asumiendo IDs del Paso 4)
        tvStatusContrato = findViewById(R.id.tvStatusContrato)
        btnCancelarContrato = findViewById(R.id.btnCancelarContrato)


        // 2. OBTENER DATOS DEL INTENT (La forma correcta desde Login/SegundaP)
        val intent = intent
        txtUsuario.text = SessionManager.userName ?: "N/D"
        txtCorreo.text = SessionManager.userEmail ?: "N/D"
        txtTelefono.text = SessionManager.userPhone ?: "N/D"

        // 3. LISTENERS
        btnRegresar.setOnClickListener{
            finish()
        }

        // 4. LÓGICA DEL CONTRATO (se llama en onResume también)
        loadContractStatus()

        // El Listener de btnCancelarContrato se asigna dentro de loadContractStatus si el contrato está ACTIVO
    }


    // ---------------------------------------------------------------------
    // FUNCIONES DE ESTADO Y CANCELACIÓN
    // ---------------------------------------------------------------------

    private fun loadContractStatus() {
        val userId = SessionManager.currentUserId

        if (userId.isNullOrEmpty() || userId == "0") {
            tvStatusContrato.text = "Error: No hay sesión activa. Por favor, reinicia la app."
            btnCancelarContrato.visibility = View.GONE
            return
        }

        val stringRequest = object : StringRequest(
            Method.POST, SERVER_URL_VERIFICACION,
            { response ->
                val status = response.trim()
                Log.d("CONTRATO_RESPONSE", "Respuesta PHP: $status")

                if (status.startsWith("ACTIVO")) {
                    // Respuesta esperada: ACTIVO|Paquete X|500 Mbps|2025-10-27
                    val parts = status.split("|")
                    val nombre = parts.getOrElse(1) { "N/D" }
                    val velocidad = parts.getOrElse(2) { "N/D" }
                    val inicio = parts.getOrElse(3) { "N/D" }
                    val precio = parts.getOrElse(4) { "0.00" }

                    tvStatusContrato.text = "Paquete: $nombre ($velocidad)\nContratado el: $inicio\nPrecio Mensual: $$precio\nEstado: ACTIVO"
                    btnCancelarContrato.visibility = View.VISIBLE
                    btnCancelarContrato.isEnabled = true
                    btnCancelarContrato.text = "CANCELAR PAQUETE"
                    btnCancelarContrato.setOnClickListener { confirmCancelDialog() }

                } else { // Incluye "NO_CONTRATO", "CANCELADO" o cualquier error
                    tvStatusContrato.text = "No tienes un paquete de internet activo en este momento."
                    btnCancelarContrato.visibility = View.GONE
                }
            },
            { error ->
                tvStatusContrato.text = "Error de Red: No se pudo verificar el contrato."
                btnCancelarContrato.visibility = View.GONE
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["usuario_id"] = userId
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    // Diálogo de confirmación para cancelar
    private fun confirmCancelDialog() {
        // 🔑 VERIFICACIÓN CORREGIDA: Checa SessionManager
        if (SessionManager.currentUserId.isNullOrEmpty() || SessionManager.currentUserId == "0") {
            Toast.makeText(this, "Error: No se pudo obtener el ID de usuario para cancelar.", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar Cancelación")
        builder.setMessage("¿Estás seguro de que deseas cancelar tu paquete actual?")

        builder.setPositiveButton("Sí, Cancelar") { dialog, _ ->
            cancelarContratoEnDB()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    // Envía la petición de cancelación a tu servidor
    private fun cancelarContratoEnDB() {
        val stringRequest = object : StringRequest(
            Method.POST, SERVER_URL_CANCELACION,
            { response ->
                Log.e("CANCEL_RESPONSE", "Respuesta completa del servidor: $response")
                if (response.trim() == "success_cancelacion") { // Ajusta si tu PHP devuelve solo "success"
                    Toast.makeText(this, "Paquete cancelado con éxito en la DB.", Toast.LENGTH_LONG).show()
                    loadContractStatus() // Recargar la vista para reflejar el estado CANCELADO

                } else {
                    Toast.makeText(this, "Error del servidor: Ver Logcat para detalles.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de Red: No se pudo conectar al servidor de cancelación.", Toast.LENGTH_LONG).show()
            }
        ) {
            // 🔑 getParams() para CANCELAR (Ya estaba correcto, solo confirmamos)
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["usuario_id"] = SessionManager.currentUserId ?: "0"
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }

    // Se asegura de que el estado del contrato se recargue si el usuario regresa de otra pantalla
    override fun onResume() {
        super.onResume()
        loadContractStatus()
    }

    // Actualiza SharedPreferences para reflejar la cancelación
    private fun updateLocalCancellationStatus() {
        val prefs = getSharedPreferences("ContratoPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val fechaFin = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        editor.putString("CONTRATO_FECHA_FIN", fechaFin)
        editor.apply()
    }

}
