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

                // Telefono
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

                // Email
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

                // Indirizzo
                var address: String? = null
                val addressCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID} = ?",
                    arrayOf(id),
                    null
                )
                addressCursor?.use { ac ->
                    if (ac.moveToFirst()) {
                        val addressIndex = ac.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)
                        address = ac.getString(addressIndex)
                    }
                }

                // Azienda e Posizione (Ruolo)
                var organization: String? = null
                var title: String? = null
                val orgCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE),
                    null
                )
                orgCursor?.use { oc ->
                    if (oc.moveToFirst()) {
                        val orgIndex = oc.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)
                        val titleIndex = oc.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)
                        organization = oc.getString(orgIndex)
                        title = oc.getString(titleIndex)
                    }
                }

                // Website
                var website: String? = null
                val webCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(id, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE),
                    null
                )
                webCursor?.use { wc ->
                    if (wc.moveToFirst()) {
                        val urlIndex = wc.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)
                        website = wc.getString(urlIndex)
                    }
                }

                // Nome + Cognome
                val nameParts = name.split(" ")
                val firstName = nameParts.firstOrNull() ?: ""
                val lastName = nameParts.drop(1).joinToString(" ")

                card = Card.fromInput(
                    name = firstName,
                    surname = lastName,
                    telephoneNumber = phone,
                    email = email,
                    organization = organization,
                    title = title,
                    webSite = website,
                    address = address
                )
            }
        }

        return card
    }

}
