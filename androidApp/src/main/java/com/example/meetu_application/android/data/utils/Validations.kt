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

fun requiredValidator(fieldName: String): (String) -> String? = { value ->
    if (value.isBlank()) "$fieldName è obbligatorio" else null
}

fun emailValidator(): (String) -> String? = { value ->
    when {
        value.isBlank() -> "Email è obbligatoria"
        !isValidEmail(value) -> "Email non valida"
        else -> null
    }
}

fun phoneValidator(): (String) -> String? = { value ->
    when {
        value.isBlank() -> "Telefono è obbligatorio"
        !isValidPhone(value) -> "Telefono non valido"
        else -> null
    }
}
