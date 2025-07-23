package com.example.meetu_application.android.utils

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.meetu_application.android.data.model.Card
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generateVCard(card: Card): String {
    return buildString {
        appendLine("BEGIN:VCARD")
        appendLine("VERSION:3.0")
        appendLine("N:${card.surname};${card.name}")
        appendLine("FN:${card.name} ${card.surname}")
        card.telephoneNumber?.let { appendLine("TEL:$it") }
        card.email?.let { appendLine("EMAIL:$it") }
        card.address?.let { appendLine("ADR:;;$it") } // struttura: ;;street;city;state;zip;country
        card.organization?.let { appendLine("ORG:$it") }
        card.title?.let { appendLine("TITLE:$it") }
        card.webSite?.let { appendLine("URL:$it") }
        appendLine("END:VCARD")
    }
}

fun generateQRCode(content: String, size: Int = 512): ImageBitmap? {
    return try {
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }

        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
