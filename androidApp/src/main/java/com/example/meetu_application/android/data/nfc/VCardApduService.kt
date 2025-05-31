package com.example.meetu_application.android.data.nfc

import android.content.Context
import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

class VCardApduService : HostApduService() {

    companion object {
        var currentVCard: String = ""
        var isSendingEnabled: Boolean = false  // Attiva solo nella schermata dettaglio
        private val SELECT_APDU = byteArrayOf(
            0x00.toByte(),
            0xA4.toByte(),
            0x04.toByte(),
            0x00.toByte(),
            0x07.toByte(), // lunghezza AID = 7 byte
            0xF0.toByte(),
            0x01.toByte(),
            0x02.toByte(),
            0x03.toByte(),
            0x04.toByte(),
            0x05.toByte(),
            0x06.toByte()
        )
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) return byteArrayOf(0x6F.toByte(), 0x00.toByte()) // Errore

        if (commandApdu.size >= SELECT_APDU.size &&
            commandApdu.sliceArray(0 until SELECT_APDU.size).contentEquals(SELECT_APDU)) {
            Log.d("HCE", "Comando SELECT con AID corretto ricevuto")
            return byteArrayOf(0x90.toByte(), 0x00.toByte()) // OK
        }

        Log.d("HCE", "Ricevuto APDU: ${commandApdu?.joinToString(" ") { "%02X".format(it) }}")

        if (currentVCard.isBlank()) {
            Log.w("HCE", "vCard vuota, ritorno file non trovato")
            return byteArrayOf(0x6A.toByte(), 0x82.toByte()) // File non trovato
        }

        if (isSendingEnabled) {
            Log.i("HCE", "Invio vCard via broadcast")
            sendVCardBroadcast(currentVCard)

            // Vibrazione breve per confermare invio
            vibrateDevice()
            Log.i("HCE", "Vibrazione eseguita")
        } else {
            Log.w("HCE", "Invio disabilitato, non mando vCard")
        }

        val vCardBytes = currentVCard.toByteArray(Charsets.UTF_8)
        Log.d("HCE", "Rispondo con vCard di ${vCardBytes.size} byte + OK")
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
        Log.d("HCE", "Broadcast ACTION_VCARD_RECEIVED inviato")
    }

    private fun vibrateDevice() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(100)
            }
            Log.d("HCE", "Vibrazione attivata")
        } ?: Log.w("HCE", "Vibrator non disponibile")
    }
}
