package com.example.meetu_application.android.ndefemulation.ndef

internal object NdefMessageFactory {

    internal fun getMessageBuilder(ndefData: NdefData): NdefMessageBuilder = when (ndefData) {
        is NdefRecordData -> NdefRecordMessageBuilder()
        is TextNdefData -> TextNdefMessageBuilder()
        is UriNdefData -> UriNdefMessageBuilder()
        is WifiNetworkNdefData -> WifiNdefMessageBuilder()
        is ContactNdefData -> ContactNdefMessageBuilder()
        is LocationNdefData -> LocationNdefMessageBuilder()
        else -> throw IllegalArgumentException("Unknown NdefData type: ${ndefData::class.java}")
    }
}