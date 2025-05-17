package com.example.meetu_application.android.data.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import com.example.meetu_application.android.data.model.Card

object ContactHelper {

    fun getCardFromContactUri(contentResolver: ContentResolver, uri: Uri): Card? {
        val cursor = contentResolver.query(uri, null, null, null, null) ?: return null

        var card: Card? = null

        cursor.use { c ->
            if (c.moveToFirst()) {
                val nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val idIndex = c.getColumnIndex(ContactsContract.Contacts._ID)

                val name = c.getString(nameIndex) ?: "Sconosciuto"
                val id = c.getString(idIndex)

                // Recupera il telefono
                var phone: String? = null
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(id),
                    null
                )
                phoneCursor?.use { pc ->
                    if (pc.moveToFirst()) {
                        val phoneIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        phone = pc.getString(phoneIndex)
                    }
                }

                // Recupera l'email
                var email: String? = null
                val emailCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                    arrayOf(id),
                    null
                )
                emailCursor?.use { ec ->
                    if (ec.moveToFirst()) {
                        val emailIndex = ec.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                        email = ec.getString(emailIndex)
                    }
                }

                // Split nome e cognome
                val nameParts = name.split(" ")
                val firstName = nameParts.firstOrNull() ?: ""
                val lastName = nameParts.drop(1).joinToString(" ")

                card = Card.fromInput(firstName, lastName, phone, email)
            }
        }
        return card
    }
}
