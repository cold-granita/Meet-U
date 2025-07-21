/*package com.example.meetu_application.android.data.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord

// 1. Define the NdefData sealed class and specific data types
sealed class NdefData

/**
 * Contact (VCARD) data type
 */
data class ContactNdefData(val vCard: String) : NdefData()

// You can add other data types here, e.g.: UriNdefData, TextNdefData, etc.

// 2. The NdefMessageBuilder interface
internal interface NdefMessageBuilder {
    /**
     * Builds an Android NdefMessage from the given NdefData
     * Returns null if the data type is unsupported
     */
    fun build(ndefData: NdefData): NdefMessage?
}

// 3. Factory to select the right builder based on NdefData
internal object NdefMessageFactory {
    fun getMessageBuilder(ndefData: NdefData): NdefMessageBuilder = when (ndefData) {
        is ContactNdefData -> ContactNdefMessageBuilder()
        // add other mappings here, e.g.:
        // is UriNdefData -> UriNdefMessageBuilder()
        else -> throw IllegalArgumentException("Unsupported NdefData type: $ndefData")
    }
}

// 4. Builder implementation for Contact (VCARD)
internal class ContactNdefMessageBuilder : NdefMessageBuilder {
    override fun build(ndefData: NdefData): NdefMessage? {
        ndefData as? ContactNdefData ?: return null

        // Use MIME type for vCard
        val mimeType = "text/x-vcard"
        val payload = ndefData.vCard.toByteArray(Charsets.UTF_8)

        // Create a single record NDEF Message
        val record = NdefRecord.createMime(mimeType, payload)
        return NdefMessage(arrayOf(record))
    }
}

// 5. (Optional) Example usage:
// val data = ContactNdefData(myVCardString)
// val msg = NdefMessageFactory.getMessageBuilder(data).build(data)
// ndefMessageBytes = msg?.toByteArray()
*/