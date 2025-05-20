package com.example.meetu_application.android.data.nfc

import android.app.Activity
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.Locale

class NFCWriter(private val activity: Activity, private val callback: NFCWriteCallback? = null) : NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private var writeMessage: NdefMessage? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun enableWriteMode(message: NdefMessage?) {
        writeMessage = message
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        nfcAdapter?.enableReaderMode(
            activity,
            this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
            Bundle()
        )
    }

    fun disableWriteMode() {
        nfcAdapter?.disableReaderMode(activity)
        writeMessage = null
    }

    override fun onTagDiscovered(tag: Tag?) {
        tag ?: return

        val ndef = Ndef.get(tag)
        if (ndef != null && writeMessage != null) {
            try {
                ndef.connect()
                val size = writeMessage!!.toByteArray().size
                if (ndef.isWritable && size <= ndef.maxSize) {
                    ndef.writeNdefMessage(writeMessage)
                    Log.d("NFCWriter", "Scrittura completata (${size} bytes)")
                    callback?.onWriteSuccess()
                } else {
                    val error = "Messaggio troppo grande per il tag"
                    Log.e("NFCWriter", "Tag non scrivibile o messaggio troppo grande")
                    callback?.onWriteError(error)
                }
            } catch (e: Exception) {
                val error = "Tag non scrivibile o messaggio nullo"
                Log.e("NFCWriter", "Errore scrittura NFC: ${e.message}", e)
                callback?.onWriteError(error)
            } finally {
                try {
                    ndef.close()
                } catch (e: Exception) {
                    callback?.onWriteError("Errore NFC: ${e.localizedMessage}")
                }
            }
        } else {
            // Prova con NdefFormatable
            val formatable = NdefFormatable.get(tag)
            if (formatable != null && writeMessage != null) {
                try {
                    formatable.connect()
                    formatable.format(writeMessage)
                    Log.d("NFCWriter", "Tag formattato e scritto con successo")
                } catch (e: Exception) {
                    Log.e("NFCWriter", "Errore formattazione: ${e.message}", e)
                } finally {
                    try {
                        formatable.close()
                    } catch (_: Exception) {}
                }
            } else {
                val error = "Tag non supporta NDEF"
                Log.d("NFCWriter", "Tag non supporta NDEF")
                callback?.onWriteError(error)
            }
        }
    }



    private fun parseMessage(message: String): NdefMessage? {
        // Il messaggio arriva come stringa nel formato "TYPE:payload"
        // es: "text:Ciao mondo", "url:https://...", "vcard:BEGIN:VCARD..."
        val parts = message.split(":", limit = 2)
        if (parts.size < 2) return null
        return when (parts[0].lowercase(Locale.ROOT)) {
            "text" -> createTextMessage(parts[1])
            "url" -> createUriMessage(parts[1])
            "vcard" -> createVCardMessage(parts[1])
            else -> null
        }
    }

    companion object {
        fun createTextMessage(text: String): NdefMessage {
            val payload = text.toByteArray(Charsets.UTF_8)
            val record = NdefRecord.createTextRecord(Locale.getDefault().language, text)
            return NdefMessage(arrayOf(record))
        }

        fun createUriMessage(uri: String): NdefMessage {
            val record = NdefRecord.createUri(uri)
            return NdefMessage(arrayOf(record))
        }

        fun createVCardMessage(vcard: String): NdefMessage {
            val payload = vcard.toByteArray(Charsets.UTF_8)
            val record = NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                "text/vcard".toByteArray(Charsets.US_ASCII),
                ByteArray(0),
                payload
            )
            return NdefMessage(arrayOf(record))
        }

        fun createVCardMessage(
            name: String,
            phone: String,
            email: String,
            organization: String? = null,
            title: String? = null,
            address: String? = null,
            website: String? = null
        ): NdefMessage {
            val vcardBuilder = StringBuilder().apply {
                append("BEGIN:VCARD\r\n")
                append("VERSION:3.0\r\n")
                append("FN:$name\r\n")
                append("TEL:$phone\r\n")
                append("EMAIL:$email\r\n")
                if (!organization.isNullOrBlank()) append("ORG:$organization\r\n")
                if (!title.isNullOrBlank()) append("TITLE:$title\r\n")
                if (!address.isNullOrBlank()) append("ADR:$address\r\n")
                if (!website.isNullOrBlank()) append("URL:$website\r\n")
                append("END:VCARD\r\n")
            }
            return createVCardMessage(vcardBuilder.toString())
        }
    }
}
