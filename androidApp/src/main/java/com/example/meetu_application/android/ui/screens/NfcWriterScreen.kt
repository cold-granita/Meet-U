package com.example.meetu_application.android.ui.screens

import android.app.Activity
import android.nfc.NdefMessage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.meetu_application.android.data.nfc.NFCWriteCallback
import com.example.meetu_application.android.data.nfc.NFCWriter
import com.example.meetu_application.android.data.utils.isValidEmail
import com.example.meetu_application.android.data.utils.isValidPhone
import com.example.meetu_application.android.ui.components.ValidatedInputField


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
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }

    // Stati per validazione (se il campo è stato toccato e perso il focus)
    var touchedFirstName by remember { mutableStateOf(false) }
    var touchedLastName by remember { mutableStateOf(false) }
    var touchedPhone by remember { mutableStateOf(false) }
    var touchedEmail by remember { mutableStateOf(false) }

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

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("NFC Writer", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
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
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Scegli il tipo di dato:", style = MaterialTheme.typography.labelLarge)
            Row {
                listOf("text", "url", "vcard").forEach { type ->
                    OutlinedButton(
                        onClick = { selectedType = type },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedType == type) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (selectedType == type)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            type.uppercase(),
                            color = if (selectedType == type)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.White
                        )
                    }
                }
            }

            when (selectedType) {
                "text" -> ValidatedInputField(
                    label = "Inserisci testo",
                    placeholder = "Ciao mondo",
                    value = inputData,
                    onValueChange = { inputData = it },
                    isError = false,
                    errorMessage = null,
                    onFocusLost = {}
                )
                "url" -> ValidatedInputField(
                    label = "Inserisci link (https://...)",
                    placeholder = "https://www.example.com",
                    value = inputData,
                    onValueChange = { inputData = it },
                    isError = false,
                    errorMessage = null,
                    onFocusLost = {}
                )
                "vcard" -> VCardForm(
                    firstName = firstName,
                    onFirstNameChange = { firstName = it },
                    lastName = lastName,
                    onLastNameChange = { lastName = it },
                    phone = phone,
                    onPhoneChange = { phone = it },
                    email = email,
                    onEmailChange = { email = it },
                    organization = organization,
                    onOrganizationChange = { organization = it },
                    title = title,
                    onTitleChange = { title = it },
                    address = address,
                    onAddressChange = { address = it },
                    website = website,
                    onWebsiteChange = { website = it },
                    touchedFirstName = touchedFirstName,
                    onFirstNameFocusLost = { touchedFirstName = true },
                    touchedLastName = touchedLastName,
                    onLastNameFocusLost = { touchedLastName = true },
                    touchedPhone = touchedPhone,
                    onPhoneFocusLost = { touchedPhone = true },
                    touchedEmail = touchedEmail,
                    onEmailFocusLost = { touchedEmail = true }
                )
            }

            Button(
                onClick = {
                    val message = when (selectedType) {
                        "text" -> NFCWriter.createTextMessage(inputData)
                        "url" -> NFCWriter.createUriMessage(inputData)
                        "vcard" -> createVCardMessageOrNull(
                            firstName,
                            lastName,
                            phone,
                            email,
                            organization,
                            title,
                            address,
                            website
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
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(60.dp),
                enabled = isWriteEnabled(
                    selectedType,
                    inputData,
                    firstName,
                    lastName,
                    phone,
                    email
                )
            ) {
                Text("Scrivi su tag NFC")
            }

            Text(
                text = writeStatus,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
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
private fun VCardForm(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
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
    onWebsiteChange: (String) -> Unit,
    touchedFirstName: Boolean,
    onFirstNameFocusLost: () -> Unit,
    touchedLastName: Boolean,
    onLastNameFocusLost: () -> Unit,
    touchedPhone: Boolean,
    onPhoneFocusLost: () -> Unit,
    touchedEmail: Boolean,
    onEmailFocusLost: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ValidatedInputField(
            label = "Nome*",
            placeholder = "Mario",
            value = firstName,
            onValueChange = onFirstNameChange,
            isError = touchedFirstName && firstName.isBlank(),
            errorMessage = if (touchedFirstName && firstName.isBlank()) "Campo obbligatorio" else null,
            onFocusLost = onFirstNameFocusLost
        )
        ValidatedInputField(
            label = "Cognome*",
            placeholder = "Rossi",
            value = lastName,
            onValueChange = onLastNameChange,
            isError = touchedLastName && lastName.isBlank(),
            errorMessage = if (touchedLastName && lastName.isBlank()) "Campo obbligatorio" else null,
            onFocusLost = onLastNameFocusLost
        )
        ValidatedInputField(
            label = "Telefono*",
            placeholder = "0123456789",
            value = phone,
            onValueChange = onPhoneChange,
            isError = touchedPhone && (!isValidPhone(phone) || email.isBlank()),
            errorMessage = when {
                touchedPhone && phone.isBlank() -> "Campo obbligatorio"
                touchedPhone && !isValidPhone(phone) -> "Formato telefono non valido"
                else -> null
            },
            onFocusLost = onPhoneFocusLost,
            keyboardType = KeyboardType.Phone
        )
        ValidatedInputField(
            label = "Email*",
            placeholder = "mario@example.com",
            value = email,
            onValueChange = onEmailChange,
            isError = touchedEmail && (!isValidEmail(email) ||email.isBlank()) ,
            errorMessage = when {
                touchedEmail && email.isBlank() -> "Campo obbligatorio"
                touchedEmail && !isValidEmail(email) -> "Formato email non valido"
                else -> null
            },
            onFocusLost = onEmailFocusLost,
            keyboardType = KeyboardType.Email
        )
        ValidatedInputField(
            label = "Organizzazione",
            placeholder = "Nome Azienda",
            value = organization,
            onValueChange = onOrganizationChange,
            isError = false,
            errorMessage = null,
            onFocusLost = {}
        )
        ValidatedInputField(
            label = "Titolo",
            placeholder = "Manager",
            value = title,
            onValueChange = onTitleChange,
            isError = false,
            errorMessage = null,
            onFocusLost = {}
        )
        ValidatedInputField(
            label = "Indirizzo",
            placeholder = "Via Roma 1, Milano",
            value = address,
            onValueChange = onAddressChange,
            isError = false,
            errorMessage = null,
            onFocusLost = {}
        )
        ValidatedInputField(
            label = "Sito web",
            placeholder = "https://www.example.com",
            value = website,
            onValueChange = onWebsiteChange,
            isError = false,
            errorMessage = null,
            onFocusLost = {}
        )
    }
}

private fun createVCardMessageOrNull(
    firstName: String,
    lastName: String,
    phone: String,
    email: String,
    organization: String,
    title: String,
    address: String,
    website: String
): NdefMessage? {
    if (firstName.isBlank() || lastName.isBlank() || phone.isBlank() || email.isBlank()) {
        return null
    }
    if (!isValidPhone(phone) || !isValidEmail(email)) {
        return null
    }
    return NFCWriter.createVCardMessage(
        name = "$firstName $lastName",
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
    firstName: String,
    lastName: String,
    phone: String,
    email: String
) = when (type) {
    "vcard" -> firstName.isNotBlank()
            && lastName.isNotBlank()
            && phone.isNotBlank()
            && email.isNotBlank()
            && isValidPhone(phone)
            && isValidEmail(email)

    else -> data.isNotBlank()
}
