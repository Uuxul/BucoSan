// SessionManager.kt

object SessionManager {
    // 🔑 Usar un tipo de dato que refleje el ID de la base de datos (Int o String)
    var currentUserId: String? = null // El ID de la columna 'id' de la tabla 'usuarios'
    var userName: String? = null
    var userEmail: String? = null
    // Agrega más datos (ej. telefono, rol, etc.) si es necesario
    var userPhone: String?= null

    fun clearSession() {
        currentUserId = null
        userName = null
        userEmail = null
        userPhone = null
    }
}