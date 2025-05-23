package com.example.meetu_application.android.data.nfc

import com.example.meetu_application.android.data.model.Card


internal fun parseVCardToCard(raw: String): Card? {
    if (!raw.contains("BEGIN:VCARD", ignoreCase = true)) return null

    var name = ""
    var surname = ""
    var phone: String? = null
    var email: String? = null
    var organization: String? = null
    var title: String? = null
    var webSite: String? = null
    var address: String? = null

    raw.lines().forEach { line ->
        val trimmedLine = line.trim()
        when {
            trimmedLine.startsWith("FN:", ignoreCase = true) -> {
                val fullName = trimmedLine.removePrefix("FN:").trim()
                val parts = fullName.split(" ")
                if (parts.size == 1) {
                    name = parts[0]
                    surname = ""
                } else if (parts.size > 1) {
                    name = parts.dropLast(1).joinToString(" ")
                    surname = parts.last()
                }
            }
            trimmedLine.startsWith("N:", ignoreCase = true) -> {
                val parts = trimmedLine.removePrefix("N:").split(";")
                surname = parts.getOrNull(0)?.trim() ?: ""
                name = parts.getOrNull(1)?.trim() ?: ""
            }
            trimmedLine.startsWith("TEL:", ignoreCase = true) -> phone = trimmedLine.removePrefix("TEL:").trim()
            trimmedLine.startsWith("EMAIL:", ignoreCase = true) -> email = trimmedLine.removePrefix("EMAIL:").trim()
            trimmedLine.startsWith("ORG:", ignoreCase = true) -> organization = trimmedLine.removePrefix("ORG:").trim()
            trimmedLine.startsWith("TITLE:", ignoreCase = true) -> title = trimmedLine.removePrefix("TITLE:").trim()
            trimmedLine.startsWith("ADR:", ignoreCase = true) -> address = trimmedLine.removePrefix("ADR:").trim().replace(";", " ")
            trimmedLine.startsWith("URL:", ignoreCase = true) -> webSite = trimmedLine.removePrefix("URL:").trim()
        }
    }

    if (name.isBlank() && surname.isBlank()) return null

    return Card.fromInput(name, surname, phone, email, organization, title, webSite, address)
}
