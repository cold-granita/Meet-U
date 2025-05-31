/*package com.example.meetu_application.android.data.nfc

import android.app.Activity
import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import com.example.meetu_application.android.data.utils.vibrateDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class NFCReader(private val context: Context) : NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _tagData = MutableSharedFlow<String>(replay = 1)
    val tags: SharedFlow<String> = _tagData

    fun register(activity: Activity) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)

        if (nfcAdapter != null) {
            val options = Bundle()
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 500)

            nfcAdapter!!.enableReaderMode(
                activity,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options
            )
        }
    }

    override fun onTagDiscovered(tag: Tag?) {
        val ndef = Ndef.get(tag)
        val ndefMessage: NdefMessage? = ndef?.cachedNdefMessage

        if (ndefMessage != null) {
            for (record in ndefMessage.records) {
                val payload = record.payload
                val mimeType = String(record.type, Charsets.US_ASCII)
                val text =
                    if (record.tnf == NdefRecord.TNF_MIME_MEDIA && mimeType == "text/vcard") {
                        // MIME type, no language code header
                        String(payload, Charsets.UTF_8)
                    } else if (record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(
                            NdefRecord.RTD_TEXT
                        )
                    ) {
                        // Well-known text type
                        val languageCodeLength = payload[0].toInt() and 0x3F
                        String(
                            payload,
                            1 + languageCodeLength,
                            payload.size - 1 - languageCodeLength,
                            Charsets.UTF_8
                        )
                    } else {
                        // Fallback
                        String(payload, Charsets.UTF_8)
                    }

                scope.launch {
                    _tagData.emit(text)
                }
            }
            vibrateDevice(context)
        } else {
            scope.launch {
                _tagData.emit("Nessun messaggio NDEF trovato.")
            }
        }
    }
}*/

package com.example.meetu_application.android.data.nfc

import android.app.Activity
import android.content.Context
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import com.example.meetu_application.android.data.utils.vibrateDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class NFCReader(private val context: Context) : NfcAdapter.ReaderCallback {
    private var nfcAdapter: NfcAdapter? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _tagData = MutableSharedFlow<String>(replay = 1)
    val tags: SharedFlow<String> = _tagData

    fun register(activity: Activity) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)

        if (nfcAdapter != null) {
            val options = Bundle()
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 500)

            nfcAdapter!!.enableReaderMode(
                activity,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options
            )
        }
    }

    override fun onTagDiscovered(tag: Tag?) {
        if (tag == null) return

        val ndef = Ndef.get(tag)
        val ndefMessage: NdefMessage? = ndef?.cachedNdefMessage

        if (ndefMessage != null) {
            for (record in ndefMessage.records) {
                val payload = record.payload
                val mimeType = String(record.type, Charsets.US_ASCII)
                val text =
                    if (record.tnf == NdefRecord.TNF_MIME_MEDIA && mimeType == "text/vcard") {
                        String(payload, Charsets.UTF_8)
                    } else if (record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(
                            NdefRecord.RTD_TEXT
                        )
                    ) {
                        val languageCodeLength = payload[0].toInt() and 0x3F
                        String(
                            payload,
                            1 + languageCodeLength,
                            payload.size - 1 - languageCodeLength,
                            Charsets.UTF_8
                        )
                    } else {
                        String(payload, Charsets.UTF_8)
                    }

                scope.launch {
                    _tagData.emit(text)
                }
            }
            vibrateDevice(context)
        } else {
            // Provo a leggere tramite HCE (IsoDep)
            val isoDep = IsoDep.get(tag)
            if (isoDep != null) {
                try {
                    isoDep.connect()

                    // Comando SELECT APDU che corrisponde al tuo AID (aggiorna AID se necessario)
                    val selectApdu = byteArrayOf(
                        0x00, 0xA4.toByte(), 0x04, 0x00, 0x07,
                        0xF0.toByte(), 0x01, 0x02, 0x03, 0x04, 0x05, 0x06
                    )

                    Log.d("HCE", "Connesso a IsoDep, invio SELECT APDU")
                    val response = isoDep.transceive(selectApdu)
                    Log.d("HCE", "Ricevuta risposta: ${response.joinToString(" ") { "%02X".format(it) }}")

                    val hexResponse = response.joinToString(" ") { String.format("%02X", it) }
                    Log.d("HCE","APDU response: $hexResponse")


                    if (response.size >= 2 &&
                        response[response.size - 2] == 0x90.toByte() &&
                        response[response.size - 1] == 0x00.toByte()
                    ) {
                        val vCardData = response.dropLast(2).toByteArray()
                        val vCardString = String(vCardData, Charsets.UTF_8)
                        Log.d("HCE", "vCard ricevuta: $vCardString")

                        scope.launch {
                            _tagData.emit(vCardString)
                        }
                        vibrateDevice(context)
                    } else {
                        scope.launch {
                            _tagData.emit("Errore nella risposta APDU")
                        }
                    }
                } catch (e: Exception) {
                    scope.launch {
                        _tagData.emit("Errore IsoDep: ${e.message}")
                    }
                } finally {
                    try {
                        isoDep.close()
                    } catch (ignored: Exception) {
                    }
                }
            } else {
                // Nessun NDEF e nessun IsoDep -> messaggio di errore
                scope.launch {
                    _tagData.emit("Nessun messaggio NDEF trovato e tag non supportato da HCE.")
                }
            }
        }
    }
}
