package com.example.meetu_application.android.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.data.nfc.NFCReader
import com.example.meetu_application.android.data.nfc.parseVCardToCard
import com.example.meetu_application.android.data.storage.loadCardsFromWallet
import com.example.meetu_application.android.data.storage.saveCardsToWallet
import com.example.meetu_application.android.ui.components.ClickableCard
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcReaderScreen(navController: NavController) {
    val context = LocalContext.current
    val nfcManager = remember { NFCReader(context) }
    var tagText by remember { mutableStateOf("Avvicina un tag NFC...") }
    var parsedCard by remember { mutableStateOf<Card?>(null) }
    var cards by remember { mutableStateOf(loadCardsFromWallet(context)) }

    DisposableEffect(navController) {
        onDispose {
            parsedCard = null
            tagText = "Avvicina un tag NFC..."
        }
    }

    LaunchedEffect(Unit) {
        if (context is Activity) {
            nfcManager.register(context)
            nfcManager.tags.collectLatest { tagData ->
                val parsed = parseVCardToCard(tagData)
                parsedCard = parsed
                tagText = parsed?.let { "Card NFC ricevuta" } ?: tagData
            }
        } else {
            tagText = "Errore: contesto non è un'Activity"
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Lettore NFC", style = MaterialTheme.typography.titleLarge)
                },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (parsedCard != null) {
                    Text(
                        text = "Contatto rilevato",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ClickableCard(
                        card = parsedCard!!,
                        navController = navController as NavHostController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Button(
                        onClick = {
                            parsedCard?.let { card ->
                                cards = cards + card
                                saveCardsToWallet(context, cards)
                                Toast.makeText(
                                    context,
                                    "Carta aggiunta con successo!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Salva contatto")
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = {
                            parsedCard = null
                            tagText = "Avvicina un tag NFC..."
                        },
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                    ) {
                        Text("Scansiona di nuovo")
                    }

                } else {
                    Text(
                        text = if (tagText == "Avvicina un tag NFC...") "Pronto per la scansione" else "Testo rilevato",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Text(
                            text = tagText,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = {
                            tagText = "Avvicina un tag NFC..."
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Scansiona di nuovo")
                    }
                }
            }

        }
    }
}
