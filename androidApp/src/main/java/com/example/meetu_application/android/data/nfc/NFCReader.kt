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
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000)

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

    fun unregister(activity: Activity) {
        nfcAdapter?.disableReaderMode(activity)
    }


    override fun onTagDiscovered(tag: Tag?) {
        Log.d("HCE", "Tag discovered: $tag")
        Log.d("HCE", "Tech list: ${tag?.techList?.joinToString(", ")}")
        if (tag == null) {
            scope.launch {
                _tagData.emit("Tag NFC vuoto")
            }
            return
        }

        val ndef = Ndef.get(tag)
        val ndefMessage: NdefMessage? = ndef?.cachedNdefMessage
        Log.d("NDEF", "{$ndefMessage}")
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
                        if (payload.isNotEmpty() && payload[0].toInt() == 0) {
                            String(payload, 1, payload.size - 1, Charsets.UTF_8)
                        } else {
                            String(payload, Charsets.UTF_8)
                        }
                    }

                scope.launch {
                    _tagData.emit(text)
                }
            }

            vibrateDevice(context)
        } else {
            val isoDep = IsoDep.get(tag)
            Log.d("HCE"," isoDep value: {$isoDep}")
            if (isoDep != null) {
                try {
                    isoDep.connect()

                    // Comando SELECT APDU per selezionare l'AID
                    val selectApdu = byteArrayOf(
                        0x00, 0xA4.toByte(), 0x04, 0x00, 0x07,
                        0xF0.toByte(), 0x01, 0x02, 0x03, 0x04, 0x05, 0x06
                    )

                    Log.d("HCE", "Connesso a IsoDep, invio SELECT APDU")
                    val selectResponse = isoDep.transceive(selectApdu)
                    Log.d(
                        "HCE",
                        "Risposta SELECT: ${selectResponse.joinToString(" ") { "%02X".format(it) }}"
                    )

                    // Verifica che il SELECT sia andato a buon fine (0x90 0x00)
                    if (selectResponse.size >= 2 &&
                        selectResponse[selectResponse.size - 2] == 0x90.toByte() &&
                        selectResponse[selectResponse.size - 1] == 0x00.toByte()
                    ) {
                        Log.d("HCE", "SELECT OK, invio comando di lettura")

                        val readCommand =
                            byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x00) // comando fittizio
                        var response = isoDep.transceive(readCommand)
                        Log.d(
                            "HCE",
                            "Risposta lettura: ${response.joinToString(" ") { "%02X".format(it) }}"
                        )

                        val fullResponse = mutableListOf<Byte>()
                        fullResponse += response.dropLast(2) // iniziale, escludi SW

                        // Loop per gestire eventuali risposte 61 XX
                        while (response.size >= 2 && response[response.size - 2] == 0x61.toByte()) {
                            val le = response.last().toInt() and 0xFF
                            val getResponse =
                                byteArrayOf(0x00, 0xC0.toByte(), 0x00, 0x00, le.toByte())

                            Log.d("HCE", "Invio GET RESPONSE per $le byte")
                            response = isoDep.transceive(getResponse)
                            Log.d(
                                "HCE",
                                "Risposta GET RESPONSE: ${
                                    response.joinToString(" ") {
                                        "%02X".format(it)
                                    }
                                }"
                            )

                            fullResponse += response.dropLast(2)
                        }

                        // Verifica finale: terminazione con 0x90 0x00
                        if (response.size >= 2 &&
                            response[response.size - 2] == 0x90.toByte() &&
                            response[response.size - 1] == 0x00.toByte()
                        ) {
                            val vCardString = String(fullResponse.toByteArray(), Charsets.UTF_8)
                            Log.d("HCE", "vCard ricevuta: $vCardString")

                            scope.launch {
                                _tagData.emit(vCardString)
                            }
                            vibrateDevice(context)
                        } else {
                            scope.launch {
                                _tagData.emit("Errore nella risposta APDU finale")
                            }
                        }

                    } else {
                        scope.launch {
                            _tagData.emit("Errore nella SELECT APDU")
                        }
                    }
                } catch (e: Exception) {
                    scope.launch {
                        _tagData.emit("Errore IsoDep: ${e.message}")
                    }
                } finally {
                    try {
                        isoDep.close()
                    } catch (_: Exception) {
                    }
                }
            }else{
                scope.launch {
                    _tagData.emit("Il tag NFC è vuoto o non contiene dati validi.")
                }
            }
        }

    }
}