package com.example.meetu_application.android.data.nfc

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class VCardApduService : HostApduService() {

    companion object {
        var currentVCard: String = ""
        var isSendingEnabled: Boolean = false  // Attiva solo nella schermata dettaglio
        private val SELECT_APDU = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00)
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        Log.d("HCE", "Ricevuto APDU: ${commandApdu?.joinToString(" ") { "%02X".format(it) }}")

        if (commandApdu == null) return byteArrayOf(0x6F.toByte(), 0x00.toByte()) // Errore

        if (commandApdu.size >= SELECT_APDU.size &&
            commandApdu.sliceArray(0 until SELECT_APDU.size).contentEquals(SELECT_APDU)) {
            Log.d("HCE", "Comando SELECT ricevuto")
            return byteArrayOf(0x90.toByte(), 0x00.toByte()) // OK
        }

        if (currentVCard.isBlank()) {
            Log.d("HCE", "vCard vuota")
            return byteArrayOf(0x6A.toByte(), 0x82.toByte()) // File non trovato
        }

        // Invia broadcast solo se isSendingEnabled è true (opzionale)
        if (isSendingEnabled) {
            sendVCardBroadcast(currentVCard)
            Log.d("HCE", "vCard inviata via broadcast")
        }

        val vCardBytes = currentVCard.toByteArray(Charsets.UTF_8)
        return vCardBytes + byteArrayOf(0x90.toByte(), 0x00.toByte()) // Risposta OK + dati
    }

    override fun onDeactivated(reason: Int) {
        Log.d("VCardApduService", "Deactivated with reason $reason")
    }

    private fun sendVCardBroadcast(vCard: String) {
        val intent = Intent("com.example.meetu_application.ACTION_VCARD_RECEIVED").apply {
            putExtra("vcard", vCard)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        }
        sendBroadcast(intent)
    }
}


