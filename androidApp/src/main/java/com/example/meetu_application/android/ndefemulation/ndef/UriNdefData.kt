package com.example.meetu_application.android.ndefemulation.ndef
import java.net.URI

data class UriNdefData(
    val uri: String
) : NdefData() {
    constructor(uri: URI) : this(uri.toString())
}