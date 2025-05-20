package com.example.meetu_application.android.data.nfc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class VCardReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.meetu_application.ACTION_VCARD_RECEIVED") {
            val vCard = intent.getStringExtra("vcard") ?: return

            val vcfFile = File(context.cacheDir, "temp_contact.vcf")
            vcfFile.writeText(vCard)

            val vcfUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                vcfFile
            )

            val saveIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(vcfUri, "text/x-vcard")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(saveIntent)

            Toast.makeText(context, "Contatto ricevuto! Salvataggio in corso…", Toast.LENGTH_SHORT).show()
        }
    }
}
