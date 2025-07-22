package com.example.meetu_application.android.ui.components

import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.meetu_application.android.data.model.Card

@Composable
fun AddToContactsButton(card: Card, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val darkTheme = isSystemInDarkTheme()

    OutlinedButton(
        onClick = {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE

                putExtra(ContactsContract.Intents.Insert.NAME, "${card.name} ${card.surname}")

                card.telephoneNumber?.takeIf { it.isNotBlank() }?.let {
                    putExtra(ContactsContract.Intents.Insert.PHONE, it)
                    putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                }

                card.email?.takeIf { it.isNotBlank() }?.let {
                    putExtra(ContactsContract.Intents.Insert.EMAIL, it)
                    putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                }

                card.address?.takeIf { it.isNotBlank() }?.let {
                    putExtra(ContactsContract.Intents.Insert.POSTAL, it)
                }

                card.organization?.takeIf { it.isNotBlank() }?.let {
                    putExtra(ContactsContract.Intents.Insert.COMPANY, it)
                }

                card.title?.takeIf { it.isNotBlank() }?.let {
                    putExtra(ContactsContract.Intents.Insert.JOB_TITLE, it)
                }
            }
            context.startActivity(intent)
        },
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = if (darkTheme) Color.White else MaterialTheme.colorScheme.onSecondary

        ),
        border = null
    ) {
        Icon(Icons.Default.Call, contentDescription = "Add to contacts", tint = if (darkTheme) Color.White else Color.Black)
        Spacer(modifier = Modifier.size(8.dp))
        Text("Aggiungi alla rubrica")
    }
}
