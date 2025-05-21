package com.example.meetu_application.android.data.utils

// Funzione per validare email
 fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

// Funzione per validare telefono (solo cifre, tra 8 e 15 caratteri)
 fun isValidPhone(phone: String): Boolean {
    val phoneRegex = Regex("^\\d{8,15}$")
    return phoneRegex.matches(phone)
}
