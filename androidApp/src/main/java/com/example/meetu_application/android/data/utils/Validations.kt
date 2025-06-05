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

fun isValidWebsite(website: String): Boolean {
    // Accetta URL che iniziano con https:// o http://, o che iniziano con www., oppure domini semplici come google.it
    val websiteRegex = Regex(
        "^(https?://)?(www\\.)?([\\w-]+\\.)+[\\w]{2,}(/.*)?$",
        RegexOption.IGNORE_CASE
    )
    return websiteRegex.matches(website)
}

fun websiteValidator(): (String) -> String? = { value ->
    when {
        value.isBlank() -> null  // campo opzionale, quindi non obbligatorio
        !isValidWebsite(value) -> "Sito web non valido"
        else -> null
    }
}
