package com.example.meetu_application.android.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.meetu_application.android.data.storage.deleteCard
import com.example.meetu_application.android.data.storage.saveUpdatedCard
import com.example.meetu_application.android.data.utils.emailValidator
import com.example.meetu_application.android.data.utils.phoneValidator
import com.example.meetu_application.android.data.utils.requiredValidator
import com.example.meetu_application.android.data.utils.websiteValidator
import com.example.meetu_application.android.ui.components.ConfirmDeleteCardDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditScreen(
    navController: NavHostController,
    card: com.example.meetu_application.android.data.model.Card
) {

    val context = LocalContext.current
    var name by remember { mutableStateOf(card.name) }
    var surname by remember { mutableStateOf(card.surname) }
    var title by remember { mutableStateOf(card.title ?: "") }
    var telephoneNumber by remember { mutableStateOf(card.telephoneNumber ?: "") }
    var email by remember { mutableStateOf(card.email ?: "") }
    var organization by remember { mutableStateOf(card.organization ?: "") }
    var webSite by remember { mutableStateOf(card.webSite ?: "") }
    var address by remember { mutableStateOf(card.address ?: "") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Stato per errori
    var nameError by remember { mutableStateOf<String?>(null) }
    var surnameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var websiteError by remember { mutableStateOf<String?>(null) }

    // Validators
    val nameValidator = requiredValidator("Nome")
    val surnameValidator = requiredValidator("Cognome")
    val emailValidator = emailValidator()  // crea la lambda di validazione email
    val phoneValidator = phoneValidator()  // crea la lambda di validazione telefono
    val websiteValidator = websiteValidator()  // crea la lambda di validazione sito web

    fun validateAll(): Boolean {
        nameError = nameValidator(name)
        surnameError = surnameValidator(surname)
        emailError = emailValidator(email)
        phoneError = phoneValidator(telephoneNumber)
        websiteError = websiteValidator(webSite)

        return listOf(nameError, surnameError, emailError, phoneError, websiteError).all { it == null }
    }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Modifica Carta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Torna indietro")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Elimina",
                            tint = Color.Red
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Salva solo se non ci sono errori
                    if (validateAll()) {
                        val updatedCard = card.copy(
                            name = name,
                            surname = surname,
                            title = title,
                            telephoneNumber = telephoneNumber,
                            email = email,
                            organization = organization,
                            webSite = webSite,
                            address = address
                        )
                        saveUpdatedCard(context, updatedCard)
                        navController.popBackStack() // Torna indietro dopo il salvataggio
                    } else {
                        Toast.makeText(context, "Inserisci i campi correttamente!", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = com.example.meetu_application.android.ui.theme.colorMeetU,
                modifier = Modifier.clip(CircleShape)
                ) {
                Icon(Icons.Default.Check, contentDescription = "Salva", tint = Color.White)
            }
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            //colori che userò per tutti i textfields
            val colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            focusedTextColor =MaterialTheme.colorScheme.secondary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            unfocusedContainerColor =  Color.Transparent,
            focusedContainerColor = Color.Transparent
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = nameValidator(it)
                },
                label = { Text("Nome") },
                isError = nameError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = colors
            )
            if (nameError != null) {
                Text(text = nameError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
            }

            OutlinedTextField(
                value = surname,
                onValueChange = {
                    surname = it
                    surnameError = surnameValidator(it)
                },
                label = { Text("Cognome") },
                isError = surnameError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = colors
            )
            if (surnameError != null) {
                Text(text = surnameError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titolo") },
                modifier = Modifier.fillMaxWidth(),
                colors = colors
            )
            OutlinedTextField(
                value = telephoneNumber,
                onValueChange = {
                    telephoneNumber = it
                    phoneError = phoneValidator(it)
                },
                label = { Text("Numero") },
                isError = phoneError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = colors
            )
            if (phoneError != null) {
                Text(text = phoneError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = emailValidator(it)
                },
                label = { Text("Email") },
                isError = emailError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = colors
            )
            if (emailError != null) {
                Text(text = emailError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
            }

            OutlinedTextField(
                value = organization,
                onValueChange = { organization = it },
                label = { Text("Azienda") },
                modifier = Modifier.fillMaxWidth(),
                colors = colors
            )
            OutlinedTextField(
                value = webSite,
                onValueChange = {
                    webSite = it
                    websiteError = websiteValidator(it)
                },
                label = { Text("Sito web") },
                isError = websiteError != null,
                modifier = Modifier.fillMaxWidth(),
                colors = colors
            )
            if (websiteError != null) {
                Text(text = websiteError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
            }

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Indirizzo") },
                modifier = Modifier.fillMaxWidth(),
                colors = colors
            )
        }
    }
    if (showDeleteDialog) {
        ConfirmDeleteCardDialog(
            onConfirm = {
                deleteCard(context, card)
                showDeleteDialog = false
                navController.navigate("wallet"){
                    popUpTo("wallet") { inclusive = true }
                }
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

}
