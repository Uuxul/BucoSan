package com.example.prueba.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.PagoItem
import com.example.prueba.R

class PagosSeleccionAdapter(
    private val context: Context,
    private val pagosList: List<PagoItem>,
    private val onSelectionChanged: (selectedCount: Int, total: Double) -> Unit
) : RecyclerView.Adapter<PagosSeleccionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val estadoColor: View = view.findViewById(R.id.estadoColor)
        val txtNumero: TextView = view.findViewById(R.id.txtNumeroPago)
        val txtFecha: TextView = view.findViewById(R.id.txtFechaPago)
        val txtMonto: TextView = view.findViewById(R.id.txtMontoPago)

        val txtEstado: TextView = view.findViewById(R.id.txtEstado)
        val check: CheckBox = view.findViewById(R.id.checkSeleccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pago_seleccion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pago = pagosList[position]

        holder.txtNumero.text = "Pago #${pago.numeroPago}"
        holder.txtFecha.text = "Fecha: ${pago.fechaPago}"
        holder.txtMonto.text = "Monto: $${"%.2f".format(pago.monto)}"
        holder.txtEstado.text = "Estado: ${pago.estado}"

        val colorRes = when (pago.estado) {
            "Pagado" -> R.color.verde_estado
            "Pendiente" -> R.color.amarillo_estado
            "Atrasado" -> R.color.rojo_estado
            else -> R.color.gris_estado
        }
        holder.estadoColor.setBackgroundColor(ContextCompat.getColor(context, colorRes))

        // Si ya está pagado, deshabilitar selección y mostrar checkbox desactivado
        if (pago.estado == "Pagado") {
            holder.check.isEnabled = false
            holder.check.isChecked = false
            pago.seleccionado = false
        } else {
            holder.check.isEnabled = true
            holder.check.isChecked = pago.seleccionado
            holder.check.setOnCheckedChangeListener { _, isChecked ->
                pago.seleccionado = isChecked
                // Calcular total y cantidad y notificar
                val (cnt, total) = computeSelectedCountAndTotal()
                onSelectionChanged(cnt, total)
            }
            // Para manejar clicks en toda la fila
            holder.itemView.setOnClickListener {
                holder.check.isChecked = !holder.check.isChecked
            }
        }
    }

    override fun getItemCount(): Int = pagosList.size

    private fun computeSelectedCountAndTotal(): Pair<Int, Double> {
        var cnt = 0
        var total = 0.0
        for (p in pagosList) {
            if (p.seleccionado) {
                cnt++
                total += p.monto
            }
        }
        return Pair(cnt, total)
    }
}
