package com.example.prueba

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.prueba.adapter.PagosSeleccionAdapter
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class PagosActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnGenerar: Button

    private val listaPagos = mutableListOf<PagoItem>()
    private val contratoId = Globales.contratoId //estatico hasta que uxul termine contratos
    val userId = Globales.usuarioId

    private lateinit var adapter: PagosSeleccionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos)

        recycler = findViewById(R.id.recyclerSeleccionPagos)
        tvTotal = findViewById(R.id.tvTotal)
        btnGenerar = findViewById(R.id.btnGenerarRecibo)

        adapter = PagosSeleccionAdapter(this, listaPagos) { selectedCount, total ->
            tvTotal.text = "Total: $${"%.2f".format(total)}"
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnGenerar.setOnClickListener {
            val seleccionados = listaPagos.filter { it.seleccionado }
            if (seleccionados.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Confirmación
            AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("Generar recibo para ${seleccionados.size} pago(s) por un total de $${"%.2f".format(seleccionados.sumOf { it.monto })}?")
                .setPositiveButton("Sí") { _, _ ->
                    generarReciboYRegistrar(seleccionados)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        cargarFechas()
    }

    private fun cargarFechas() {
        val url = Config.BASE_URL + "obtener_fechas.php"
        val req = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getString("status") == "success") {
                        val array = json.getJSONArray("fechas")
                        listaPagos.clear()
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            val pago = PagoItem(
                                id = obj.getInt("id"),
                                numeroPago = obj.getInt("numero_pago"),
                                fechaPago = obj.getString("fecha_pago"),
                                monto = obj.getDouble("monto"),
                                estado = obj.getString("estado"),
                                seleccionado = false
                            )
                            listaPagos.add(pago)
                        }
                        adapter.notifyDataSetChanged()
                        // actualizar total inicial
                        val total = listaPagos.filter { it.seleccionado }.sumOf { it.monto }
                        tvTotal.text = "Total: $${"%.2f".format(total)}"
                    } else {
                        Toast.makeText(this, json.optString("message","Error al obtener fechas"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parseando respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("contrato_id" to contratoId.toString())
            }
        }
        Volley.newRequestQueue(this).add(req)
    }

    private fun generarReciboYRegistrar(seleccionados: List<PagoItem>) {
        val referencia = generarReferencia(contratoId, userId)
        val montoTotal = seleccionados.sumOf { it.monto }
        // 1) Generar PDF y guardarlo localmente
        val pdfFileName = "recibo_ $referencia.pdf"
        val descripcion = "Recibo de pago - Ref: $referencia"
        val savedUri = generarPdfEnDownloads(this, pdfFileName, descripcion, seleccionados, montoTotal)
        if (savedUri == null) {
            Toast.makeText(this, "Error al guardar PDF", Toast.LENGTH_LONG).show()
            return
        }

        // 2) Llamada al servidor para insertar pagos y pagos_detalle
        registrarPagoEnServidor(userId, contratoId, "Efectivo", montoTotal, referencia, seleccionados.map { it.id }, savedUri)
    }

    private fun generarReferencia(contratoId: Int, usuarioId: Int): String {
        val ts = System.currentTimeMillis()
        val rnd = (1000..9999).random()
        return "CON-$contratoId-U$usuarioId-$ts-$rnd"
    }

    /**
     * Genera un PDF simple y lo guarda en Downloads (MediaStore). Retorna la Uri si se guardó.
     */
    private fun generarPdfEnDownloads(
        context: Context,
        fileName: String,
        descripcion: String,
        items: List<PagoItem>,
        total: Double
    ): Uri? {
        return try {
            val doc = android.graphics.pdf.PdfDocument()
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = doc.startPage(pageInfo)
            val canvas = page.canvas
            val paint = android.graphics.Paint()
            paint.textSize = 16f

            var y = 40f
            paint.textSize = 18f
            canvas.drawText("Recibo de Pago", 40f, y, paint)
            y += 28f
            paint.textSize = 12f
            canvas.drawText("Referencia: ${fileName.replace(".pdf","")}", 40f, y, paint)
            y += 20f
            canvas.drawText("Fecha: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}", 40f, y, paint)
            y += 26f
            canvas.drawText("Contrato ID: $contratoId   Usuario ID: $userId", 40f, y, paint)
            y += 26f
            canvas.drawText("Detalles:", 40f, y, paint)
            y += 20f

            paint.textSize = 11f
            for (it in items) {
                if (y > 780f) break
                canvas.drawText("• Pago #${it.numeroPago} - Fecha: ${it.fechaPago} - Monto: ${"%.2f".format(it.monto)}", 45f, y, paint)
                y += 18f
            }

            y += 8f
            paint.textSize = 13f
            canvas.drawText("Total: $${"%.2f".format(total)}", 40f, y, paint)

            doc.finishPage(page)

            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")

                // IMPORTANTE: NO DESCRIPTION (NO EXISTE EN ESTA API)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }

            val uri: Uri? = resolver.insert(collection, values)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { out ->
                    doc.writeTo(out)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                }
            }

            doc.close()
            uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    /**
     * Envía la información al servidor para insertar en pagos y pagos_detalle.
     * - fechaPagoIds: lista de ids de fechas_pagos (Int)
     * - pdfUri: Uri del PDF guardado localmente (opcionalmente puedes subirlo en otro endpoint)
     */
    private fun registrarPagoEnServidor(
        usuarioId: Int,
        contratoId: Int,
        metodoPago: String,
        montoTotal: Double,
        referencia: String,
        fechaPagoIds: List<Int>,
        pdfUri: Uri?
    ) {
        val url = Config.BASE_URL + "registrar_pago.php"

        val queue = Volley.newRequestQueue(this)

        val request = object : StringRequest(Method.POST, url,
            { response ->
                Log.e("PAGO_DEBUG", "Respuesta completa del servidor:\n$response")

                try {
                    val json = JSONObject(response)
                    if (json.optBoolean("success", false)) {
                        Toast.makeText(
                            this,
                            "Pago registrado (ID: ${json.optInt("pago_id")})",
                            Toast.LENGTH_LONG
                        ).show()

                        // Si hay PDF, aquí iría la lógica de subida
                        if (pdfUri != null) {
                            // subirPDF(pdfUri, json.optInt("pago_id"))
                        }

                        cargarFechas()
                    } else {
                        val errorMsg = json.optString("error", "Error desconocido")
                        Toast.makeText(this, "Error del servidor: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "JSON inválido del servidor", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                error.printStackTrace()

                var errorMessage = error.message ?: "Error desconocido"

                // Extraer cuerpo del error del servidor si existe
                if (error.networkResponse != null) {
                    val body = String(error.networkResponse.data)
                    Log.e("PAGO_DEBUG", "Cuerpo del error HTTP:\n$body")
                    errorMessage = "HTTP ${error.networkResponse.statusCode}: $body"
                }

                Toast.makeText(this, "Error en petición:\n$errorMessage", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val arr = JSONArray()
                fechaPagoIds.forEach { arr.put(it) }

                return hashMapOf(
                    "usuario_id" to usuarioId.toString(),
                    "contrato_id" to contratoId.toString(),
                    "metodo_pago" to metodoPago,
                    "monto_total" to "%.2f".format(montoTotal),
                    "referencia" to referencia,
                    "fecha_pago_ids" to arr.toString()
                )
            }
        }

        queue.add(request)
    }

    private fun uploadPdfToServer(pdfUri: Uri, referencia: String) {
        // Implementa subida multipart (Volley no soporta nativo multipart fácilmente).
        // Recomiendo usar Volley + okhttp3 MultipartRequest o una librería como Retrofit/OkHttp.
    }
}
