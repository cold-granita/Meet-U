package com.example.meetu_application.android.ndefemulation.ndef

import android.nfc.NdefMessage

internal interface NdefMessageBuilder {
    fun build(ndefData: NdefData): NdefMessage?
}
