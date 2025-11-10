package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prueba.PagoItem
import com.example.prueba.adapter.PagosAdapter
import org.json.JSONArray
import org.json.JSONObject

class perfil : AppCompatActivity() {

    private lateinit var recyclerPagos: RecyclerView
    private lateinit var btnFechas: Button
    private val listaPagos = mutableListOf<PagoItem>()
    private val contratoId = 25 //Por ahora fijo, luego dinamico
    private lateinit var nombre: TextView
    private lateinit var correo: TextView
    private lateinit var telefono: TextView

    private lateinit var direccion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        recyclerPagos = findViewById(R.id.recyclerPagos)
        recyclerPagos.layoutManager = LinearLayoutManager(this)
        btnFechas = findViewById(R.id.btnfechas)

        nombre = findViewById(R.id.txtUsuario)
        correo = findViewById(R.id.txtCorreo)
        telefono = findViewById(R.id.txtTelefono)
        direccion = findViewById(R.id.txtDireccion)

        // Mostrar datos del usuario
        nombre.text = RegistrarCuenta.nombreUsuarioGlobal ?: "Usuario"
        correo.text = RegistrarCuenta.correoUsuarioGlobal ?: "usuario@email.com"
        direccion.text = RegistrarCuenta.DireccionUsuarioGlobal ?: "UsuarioDirecc"
        telefono.text = RegistrarCuenta.telefonoUsuarioGlobal ?: "+52 999-123-4567"

        val btnFechas = findViewById<Button>(R.id.btnfechas)
        val btn = findViewById<Button>(R.id.btnregresar)

        btnFechas.setOnClickListener {
            generarfechas()
        }

        btn.setOnClickListener{
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        cargarFechas()
    }

    private fun generarfechas() {
        val urlGenerar = Config.BASE_URL + "generar_fechas.php"

        val request = object : StringRequest(Method.POST, urlGenerar,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getString("status") == "success") {
                        Toast.makeText(this, "Fechas generadas correctamente", Toast.LENGTH_SHORT).show()
                        cargarFechas()
                    } else {
                        Toast.makeText(this, json.getString("msg"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("contrato_id" to contratoId.toString())
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
    private fun cargarFechas() {
        val url = Config.BASE_URL + "obtener_fechas.php"

        val request = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getString("status") == "success") {
                        val array: JSONArray = json.getJSONArray("fechas")
                        listaPagos.clear()

                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            val pago = PagoItem(
                                numeroPago = obj.getInt("numero_pago"),
                                fechaPago = obj.getString("fecha_pago"),
                                monto = obj.getDouble("monto"),
                                estado = obj.getString("estado")
                            )
                            listaPagos.add(pago)
                        }

                        recyclerPagos.adapter = PagosAdapter(this, listaPagos)
                    } else {
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Error al obtener fechas: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("contrato_id" to contratoId.toString())
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
