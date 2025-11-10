package com.example.prueba

data class PagoItem(
    val numeroPago: Int,
    val fechaPago: String,
    val monto: Double,
    val estado: String
)