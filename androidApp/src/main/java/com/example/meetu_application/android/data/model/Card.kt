package com.example.meetu_application.android.data.model

import java.util.UUID

data class Card(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val surname: String,
    val telephoneNumber: String? = null,
    val email: String? = null,
    val organization: String? = null,
    val title: String? = null,
    val webSite: String? = null,
    val address: String? = null,
) {
    // Costruttore da input manuale
    companion object {
        fun fromInput(
            name: String,
            surname: String,
            telephoneNumber: String?,
            email: String?,
            organization: String?,
            title: String?,
            webSite: String?,
            address: String?
        ): Card {
            return Card(name = name, surname = surname, telephoneNumber = telephoneNumber, email = email, organization = organization, title = title, webSite = webSite, address = address)
        }
        // Costruttore da input vCard
        fun fromvCard(vCardString: String): Card {
            val lines = vCardString.lines()
            val name = lines.find { it.startsWith("FN:") }?.removePrefix("FN:") ?: "Sconosciuto"
            val telephoneNumber = lines.find { it.startsWith("TEL:") }?.removePrefix("TEL:") ?: ""
            val email = lines.find { it.startsWith("EMAIL:") }?.removePrefix("EMAIL:") ?: ""
            val surname = name.substringAfterLast(" ") // Estraggo il cognome da FN!!!
            return Card(name = name, surname = surname, telephoneNumber = telephoneNumber, email = email)
        }
    }
}
