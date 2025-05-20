package com.example.meetu_application.android.data.nfc

import android.app.Activity
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class NFCReader : NfcAdapter.ReaderCallback {
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
                val payload = String(record.payload, Charsets.UTF_8)
                scope.launch {
                    _tagData.emit(payload)
                }
            }
        } else {
            scope.launch {
                _tagData.emit("Nessun messaggio NDEF trovato.")
            }
        }
    }
}