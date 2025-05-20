package com.example.meetu_application.android.data.nfc

interface NFCWriteCallback {
    fun onWriteSuccess()
    fun onWriteError(error: String)
}
