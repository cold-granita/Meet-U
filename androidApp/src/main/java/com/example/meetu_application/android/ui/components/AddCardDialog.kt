package com.example.meetu_application.android.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.data.utils.emailValidator
import com.example.meetu_application.android.data.utils.phoneValidator
import com.example.meetu_application.android.data.utils.requiredValidator


@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onAdd: (Card) -> Unit,
    onPickContact: () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    val primaryColor = if (darkTheme) Color(0xFF86C5FC) else MaterialTheme.colorScheme.primary
    val onPrimaryColor = if (darkTheme) Color(0xFF4514B4) else MaterialTheme.colorScheme.onPrimary

    val secondaryColor = if (darkTheme) Color(0xFF00B8FF) else MaterialTheme.colorScheme.secondary
    val onSecondaryColor = if (darkTheme) Color.Black else MaterialTheme.colorScheme.onSecondary

    val cancelTextColor = if (darkTheme) Color(0xFFFF6666) else Color.Red

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var webSite by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var phonePrefix by remember { mutableStateOf("+39") }

    // Stati touched per validazione
    var touchedName by remember { mutableStateOf(false) }
    var touchedSurname by remember { mutableStateOf(false) }
    var touchedPhone by remember { mutableStateOf(false) }
    var touchedEmail by remember { mutableStateOf(false) }

    // Prima definisco se ogni campo è valido
    val isNameValid = requiredValidator("Nome")(name) == null
    val isSurnameValid = requiredValidator("Cognome")(surname) == null
    val isPhoneValid = phoneValidator()(phone) == null
    val isEmailValid = emailValidator()(email) == null

    // Controllo complessivo
    val isFormValid = isNameValid && isSurnameValid && isPhoneValid && isEmailValid

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier
                .padding(16.dp)
                .heightIn(max = 520.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState), // Scroll verticale
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Aggiungi Card", style = MaterialTheme.typography.titleLarge)

                ValidatedInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nome*",
                    validator = requiredValidator("Nome"),
                    touched = touchedName,
                    onFocusLost = { touchedName = true },
                    modifier = Modifier.fillMaxWidth()
                )

                ValidatedInputField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = "Cognome*",
                    validator = requiredValidator("Cognome"),
                    touched = touchedSurname,
                    onFocusLost = { touchedSurname = true },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CountryCodePicker(
                        selectedDialCode = phonePrefix,
                        onDialCodeChanged = { phonePrefix = it },
                        modifier = Modifier.weight(1.25f)
                    )
                    ValidatedInputField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Telefono*",
                        validator = phoneValidator(),
                        touched = touchedPhone,
                        onFocusLost = { touchedPhone = true },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(2f)
                    )
                }

                ValidatedInputField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email*",
                    validator = emailValidator(),
                    touched = touchedEmail,
                    onFocusLost = { touchedEmail = true },
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier.fillMaxWidth()
                )

                ValidatedInputField(
                    value = organization,
                    onValueChange = { organization = it },
                    label = "Azienda",
                    validator = null,
                    touched = false,
                    onFocusLost = {},
                    modifier = Modifier.fillMaxWidth()
                )

                ValidatedInputField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Titolo",
                    validator = null,
                    touched = false,
                    onFocusLost = {},
                    modifier = Modifier.fillMaxWidth()
                )

                ValidatedInputField(
                    value = webSite,
                    onValueChange = { webSite = it },
                    label = "Sito Web",
                    validator = null,
                    touched = false,
                    onFocusLost = {},
                    modifier = Modifier.fillMaxWidth()
                )

                ValidatedInputField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Indirizzo",
                    validator = null,
                    touched = false,
                    onFocusLost = {},
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onPickContact,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Aggiungi", tint = Color.White)
                    Text(" Aggiungi da Rubrica")
                }

                Button(
                     onClick = {
                         val fullPhone = if (phone.isBlank()) null else phonePrefix + phone
                        val newCard = Card.fromInput(
                            name,
                            surname,
                            fullPhone,
                            email.ifBlank { null },
                            organization.ifBlank { null },
                            title.ifBlank { null },
                            webSite.ifBlank { null },
                            address.ifBlank { null },
                        )
                        onAdd(newCard)
                    },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Crea card", tint = Color.White)
                    Text(" Crea Card")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Annulla",
                            color= Color.Red
                        )
                    }
                }
            }
        }
    }
}
