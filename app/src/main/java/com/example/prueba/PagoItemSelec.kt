package com.example.prueba

class PagoItemSelec (
    val id: Int,                // id de la tabla fechas_pagos
    val numeroPago: Int,
    val fechaPago: String,
    val monto: Double,
    val estado: String,
    var seleccionado: Boolean = false // para selección en lista
)