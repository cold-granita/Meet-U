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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.meetu_application.android.data.nfc.NFCWriteCallback
import com.example.meetu_application.android.data.nfc.NFCWriter
import com.example.meetu_application.android.data.utils.emailValidator
import com.example.meetu_application.android.data.utils.isValidEmail
import com.example.meetu_application.android.data.utils.isValidPhone
import com.example.meetu_application.android.data.utils.phoneValidator
import com.example.meetu_application.android.data.utils.requiredValidator
import com.example.meetu_application.android.data.utils.websiteValidator
import com.example.meetu_application.android.ui.components.ValidatedInputField
import com.example.meetu_application.android.ui.components.WriteStatusView


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

    //campo per URL
    var website_url by remember { mutableStateOf("") }
    var touchedWebSite by remember { mutableStateOf(false) }

    // Stati per validazione (se il campo è stato toccato e perso il focus)
    var touchedFirstName by remember { mutableStateOf(false) }
    var touchedLastName by remember { mutableStateOf(false) }
    var touchedPhone by remember { mutableStateOf(false) }
    var touchedEmail by remember { mutableStateOf(false) }

    var writeStatus by remember { mutableStateOf<WriteStatus>(WriteStatus.None) }

    val nfcWriter = remember {
        NFCWriter(activity, object : NFCWriteCallback {
            override fun onWriteSuccess() {
                writeStatus = WriteStatus.Success("Tag scritto con successo!")
            }

            override fun onWriteError(error: String) {
                writeStatus = WriteStatus.Error("Errore: $error")
            }
        })
    }

    LaunchedEffect(Unit) {
        onWriterReady(nfcWriter) { status ->
            writeStatus = WriteStatus.Info(status)
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
                        onClick = {
                            selectedType = type
                            writeStatus = WriteStatus.Info("Writer NFC pronto")
                                  },
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
                    validator = null,
                    touched = false,
                    onFocusLost = {}
                )
                "url" -> ValidatedInputField(
                    label = "Sito web",
                    placeholder = "https://www.example.com",
                    value = website_url,
                    onValueChange = {
                        website_url = it
                        if (!touchedWebSite) touchedWebSite = true
                    },
                    validator = websiteValidator(),
                    touched = touchedWebSite,
                    onFocusLost = { }
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
                        "url" -> NFCWriter.createUriMessage(website_url)
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
                        writeStatus = WriteStatus.Info("Avvicina un tag NFC per scrivere...")
                    } else if (writeStatus is WriteStatus.Info && (writeStatus as WriteStatus.Info).message.isEmpty()) {
                        writeStatus = WriteStatus.Info("Formato non valido per il tipo selezionato.")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(60.dp),
                enabled = when (selectedType) {
                    "vcard" -> isWriteEnabled(selectedType, "", firstName, lastName, phone, email)
                    "url" -> isWriteEnabled(selectedType, website_url, "", "", "", "")
                    else -> isWriteEnabled(selectedType, inputData, "", "", "", "")
                }

            ) {
                Text("Scrivi su tag NFC")
            }
            WriteStatusView(
                status = writeStatus,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            nfcWriter.disableWriteMode()
        }
    }
}

sealed class WriteStatus {
    data class Success(val message: String): WriteStatus()
    data class Error(val message: String): WriteStatus()
    data class Info(val message: String): WriteStatus()
    object None: WriteStatus()
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
            validator = requiredValidator("Nome"),
            touched = touchedFirstName,
            onFocusLost = onFirstNameFocusLost
        )

        ValidatedInputField(
            label = "Cognome*",
            placeholder = "Rossi",
            value = lastName,
            onValueChange = onLastNameChange,
            validator = requiredValidator("Cognome"),
            touched = touchedLastName,
            onFocusLost = onLastNameFocusLost
        )

        ValidatedInputField(
            label = "Telefono*",
            placeholder = "0123456789",
            value = phone,
            onValueChange = onPhoneChange,
            validator = phoneValidator(),
            touched = touchedPhone,
            onFocusLost = onPhoneFocusLost,
            keyboardType = KeyboardType.Phone
        )

        ValidatedInputField(
            label = "Email*",
            placeholder = "mario@example.com",
            value = email,
            onValueChange = onEmailChange,
            validator = emailValidator(),
            touched = touchedEmail,
            onFocusLost = onEmailFocusLost,
            keyboardType = KeyboardType.Email
        )
        ValidatedInputField(
            label = "Organizzazione",
            placeholder = "Nome Azienda",
            value = organization,
            onValueChange = onOrganizationChange,
            validator = { null },
            touched = false,
            onFocusLost = {}
        )
        ValidatedInputField(
            label = "Titolo",
            placeholder = "Manager",
            value = title,
            onValueChange = onTitleChange,
            validator = { null },
            touched = false,
            onFocusLost = {}
        )
        ValidatedInputField(
            label = "Indirizzo",
            placeholder = "Via Roma 1, Milano",
            value = address,
            onValueChange = onAddressChange,
            validator = { null },
            touched = false,
            onFocusLost = {}
        )
        ValidatedInputField(
            label = "Sito web",
            placeholder = "https://www.example.com",
            value = website,
            onValueChange = onWebsiteChange,
            validator = { null },
            touched = false,
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

    "url" -> data.isNotBlank() && websiteValidator()(data) == null
    else -> data.isNotBlank()
}
