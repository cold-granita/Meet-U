package com.example.meetu_application.android.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.meetu_application.android.data.nfc.NFCWriteCallback
import com.example.meetu_application.android.data.nfc.NFCWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcWriterScreen(
    navController: NavController,
    onWriterReady: (NFCWriter, (String) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return


    var selectedType by remember { mutableStateOf("text") }
    var inputData by remember { mutableStateOf("") }

    // Campi separati per vCard
    var vcardName by remember { mutableStateOf("") }
    var vcardPhone by remember { mutableStateOf("") }
    var vcardEmail by remember { mutableStateOf("") }
    var vcardOrganization by remember { mutableStateOf("") }
    var vcardTitle by remember { mutableStateOf("") }
    var vcardAddress by remember { mutableStateOf("") }
    var vcardWebsite by remember { mutableStateOf("") }

    var writeStatus by remember { mutableStateOf("") }

    val nfcWriter = remember {
        NFCWriter(activity, object : NFCWriteCallback {
            override fun onWriteSuccess() {
                writeStatus = "✅ Tag scritto con successo!"
            }

            override fun onWriteError(error: String) {
                writeStatus = "❌ Errore: $error"
            }
        })
    }
    LaunchedEffect(Unit) {
        onWriterReady(nfcWriter) { status ->
            writeStatus = status
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("NFC WRITER", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Torna indietro"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Scegli il tipo di dato:", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                listOf("text", "url", "vcard").forEach { type ->
                    OutlinedButton(
                        onClick = { selectedType = type },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedType == type) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = selectedType == type)
                    ) {
                        Text(
                            type.uppercase(),
                            color = if (selectedType == type)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedType) {
                "text" -> InputField(
                    label = "Inserisci testo",
                    placeholder = "Ciao mondo",
                    value = inputData,
                    onValueChange = { inputData = it }
                )
                "url" -> InputField(
                    label = "Inserisci link (https://...)",
                    placeholder = "https://www.example.com",
                    value = inputData,
                    onValueChange = { inputData = it }
                )
                "vcard" -> VCardForm(
                    name = vcardName,
                    onNameChange = { vcardName = it },
                    phone = vcardPhone,
                    onPhoneChange = { vcardPhone = it },
                    email = vcardEmail,
                    onEmailChange = { vcardEmail = it },
                    organization = vcardOrganization,
                    onOrganizationChange = { vcardOrganization = it },
                    title = vcardTitle,
                    onTitleChange = { vcardTitle = it },
                    address = vcardAddress,
                    onAddressChange = { vcardAddress = it },
                    website = vcardWebsite,
                    onWebsiteChange = { vcardWebsite = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val message = when (selectedType) {
                        "text" -> NFCWriter.createTextMessage(inputData)
                        "url" -> NFCWriter.createUriMessage(inputData)
                        "vcard" -> createVCardMessageOrNull(
                            vcardName,
                            vcardPhone,
                            vcardEmail,
                            vcardOrganization,
                            vcardTitle,
                            vcardAddress,
                            vcardWebsite
                        )
                        else -> null
                    }

                    if (message != null) {
                        nfcWriter.enableWriteMode(message)
                        writeStatus = "Avvicina un tag NFC per scrivere..."
                    } else if (writeStatus.isEmpty()) {
                        writeStatus = "Formato non valido per il tipo selezionato."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isWriteEnabled(selectedType, inputData, vcardName, vcardPhone, vcardEmail)
            ) {
                Text("Scrivi su tag NFC")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = writeStatus,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            nfcWriter.disableWriteMode()
        }
    }
}

@Composable
private fun InputField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun VCardForm(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    organization: String,
    onOrganizationChange: (String) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    website: String,
    onWebsiteChange: (String) -> Unit
) {
    Column {
        TextField(value = name, onValueChange = onNameChange, label = { Text("Nome completo*") }, placeholder = { Text("Mario Rossi") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = phone, onValueChange = onPhoneChange, label = { Text("Telefono*") }, placeholder = { Text("1234567890") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = email, onValueChange = onEmailChange, label = { Text("Email*") }, placeholder = { Text("mario@example.com") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = organization, onValueChange = onOrganizationChange, label = { Text("Organizzazione") }, placeholder = { Text("Nome Azienda") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = title, onValueChange = onTitleChange, label = { Text("Titolo") }, placeholder = { Text("Manager") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = address, onValueChange = onAddressChange, label = { Text("Indirizzo") }, placeholder = { Text("Via Roma 1, Milano") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = website, onValueChange = onWebsiteChange, label = { Text("Sito web") }, placeholder = { Text("https://www.example.com") }, modifier = Modifier.fillMaxWidth())
    }
}

private fun createVCardMessageOrNull(
    name: String,
    phone: String,
    email: String,
    organization: String,
    title: String,
    address: String,
    website: String
) = if (name.isBlank() || phone.isBlank() || email.isBlank()) {
    null
} else {
    NFCWriter.createVCardMessage(
        name = name,
        phone = phone,
        email = email,
        organization = organization.takeIf { it.isNotBlank() },
        title = title.takeIf { it.isNotBlank() },
        address = address.takeIf { it.isNotBlank() },
        website = website.takeIf { it.isNotBlank() }
    )
}

private fun isWriteEnabled(
    type: String,
    data: String,
    name: String,
    phone: String,
    email: String
) = when (type) {
    "vcard" -> name.isNotBlank() && phone.isNotBlank() && email.isNotBlank()
    else -> data.isNotBlank()
}