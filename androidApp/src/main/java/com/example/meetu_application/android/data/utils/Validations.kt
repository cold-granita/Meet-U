package com.example.meetu_application.android.data.utils

// Funzione per validare email
fun isValidEmail(email: String): Boolean {
    val trimmedEmail = email.trim()
    return android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()
}


// Funzione per validare telefono (solo cifre, tra 8 e 15 caratteri)
 fun isValidPhone(phone: String): Boolean {
    val cleanedPhone = phone.replace(Regex("[\\s-]"), "")
    val phoneRegex = Regex("^\\d{8,15}$")
    return phoneRegex.matches(cleanedPhone)
}

fun isValidPhoneEdit(phone: String): Boolean {
    // Consente + opzionale, cifre, spazi e trattini. Conta solo le cifre per il range.
    val cleanedPhone = phone.replace(Regex("[\\s-]"), "") // Rimuove spazi e trattini
    val phoneRegex = Regex("^\\+?\\d{8,15}$") // Controlla solo le cifre rimanenti
    return phoneRegex.matches(cleanedPhone)
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

fun phoneValidatorEdit(): (String) -> String? = { value ->
    when {
        value.isBlank() -> "Telefono è obbligatorio"
        !isValidPhoneEdit(value) -> "Telefono non valido"
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
