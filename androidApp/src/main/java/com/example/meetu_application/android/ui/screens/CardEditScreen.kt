package com.example.meetu_application.android.ui.screens

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
            FloatingActionButton(onClick = {
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
            }, containerColor = com.example.meetu_application.android.ui.theme.colorMeetU,
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Cognome") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titolo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = telephoneNumber,
                onValueChange = { telephoneNumber = it },
                label = { Text("Numero") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = organization,
                onValueChange = { organization = it },
                label = { Text("Azienda") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = webSite,
                onValueChange = { webSite = it },
                label = { Text("Sito web") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Indirizzo") },
                modifier = Modifier.fillMaxWidth()
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
