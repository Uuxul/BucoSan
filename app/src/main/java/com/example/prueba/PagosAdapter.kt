package com.example.prueba.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.R
import com.example.prueba.PagoItem

class PagosAdapter(
    private val context: Context,
    private val pagosList: List<PagoItem>
) : RecyclerView.Adapter<PagosAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val estadoColor: View = view.findViewById(R.id.estadoColor)
        val txtNumero: TextView = view.findViewById(R.id.txtNumeroPago)
        val txtFecha: TextView = view.findViewById(R.id.txtFechaPago)
        val txtMonto: TextView = view.findViewById(R.id.txtMontoPago)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pago, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pago = pagosList[position]

        holder.txtNumero.text = "Pago #${pago.numeroPago}"
        holder.txtFecha.text = "Fecha: ${pago.fechaPago}"
        holder.txtMonto.text = "Monto: $${pago.monto}"

        // Colores visuales por estado
        val colorRes = when (pago.estado) {
            "Pagado" -> R.color.verde_estado    // verde
            "Pendiente" -> R.color.amarillo_estado // amarillo
            "Atrasado" -> R.color.rojo_estado   // rojo
            else -> R.color.gris_estado         // gris por defecto
        }

        holder.estadoColor.setBackgroundColor(ContextCompat.getColor(context, colorRes))
    }

    override fun getItemCount(): Int = pagosList.size
}
