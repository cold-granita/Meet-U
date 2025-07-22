package com.example.meetu_application.android.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.data.nfc.NFCReader
import com.example.meetu_application.android.data.nfc.parseVCardToCard
import com.example.meetu_application.android.data.storage.loadCardsFromWallet
import com.example.meetu_application.android.data.storage.saveCardsToWallet
import com.example.meetu_application.android.data.utils.isValidWebsite
import com.example.meetu_application.android.ui.components.AddToContactsButton
import com.example.meetu_application.android.ui.components.CardView
import com.example.meetu_application.android.ui.components.OpenUrlDialog
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcReaderScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity
    val nfcManager = remember { NFCReader(context) }

    var tagText by remember { mutableStateOf("Avvicina un tag NFC...") }
    var parsedCard by remember { mutableStateOf<Card?>(null) }
    var cards by remember { mutableStateOf(loadCardsFromWallet(context)) }

    var showDialog by remember { mutableStateOf(false) }
    var urlToOpen by remember { mutableStateOf<String?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current

    var dots by remember { mutableStateOf("") }

    val darkTheme = isSystemInDarkTheme()


    DisposableEffect(lifecycleOwner, activity) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    activity?.let {
                        nfcManager.register(it)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    activity?.let {
                        nfcManager.unregister(it)
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            activity?.let {
                nfcManager.unregister(it)
            }
        }
    }


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
                tagText = when {
                    tagData.isBlank() -> "Tag NFC vuoto"
                    parsed == null -> tagData // testo grezzo non convertibile in card
                    else -> "Card NFC ricevuta"
                }
            }
        } else {
            tagText = "Errore: contesto non è un'Activity"
        }
    }

    LaunchedEffect(tagText) {
        if (tagText == "Avvicina un tag NFC...") {
            while (true) {
                dots = when (dots.length) {
                    0 -> "."
                    1 -> ".."
                    2 -> "..."
                    else -> ""
                }
                kotlinx.coroutines.delay(500)
            }
        } else {
            dots = ""
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
                if (tagText == "Tag NFC vuoto") {
                    Log.d("TAG_VUOTO", "Tag vuoto")
                    Text(
                        text = "Il tag NFC è vuoto o non contiene dati validi.",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                } else if (parsedCard != null) {
                    Text(
                        text = "Contatto rilevato",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    CardView(
                        card = parsedCard!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
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
                        Icon(Icons.Default.Add, "Aggiungi al wallet")
                        Text("Aggiungi carta nel wallet")
                    }

                    AddToContactsButton(parsedCard!!)

                    Spacer(modifier = Modifier.height(30.dp))

                    val contentColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onSecondary
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = contentColor
                    ),
                        onClick = {
                            parsedCard = null
                            tagText = "Avvicina un tag NFC..."
                        },
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                    ) {
                        Text("Scansiona di nuovo", color = contentColor)
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
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Box(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Log.d("NFC_RAW", tagText.toByteArray().joinToString(","))
                            Log.d("NFC_RAW_STR", tagText)
                            val cleanedTagText = tagText.trim().replace(Regex("[^\\x20-\\x7E]"), "")

                            val ab = isValidWebsite(cleanedTagText)
                            Log.d("VALID", "Cleaned tagText: {$cleanedTagText}")
                            Log.d("VALID", "ab :{$ab}")

                            val a = isValidWebsite(tagText)
                            Log.d("VALID", "a: {$a}")
                            if (isValidWebsite(tagText)) {
                                val formattedUrl = if (tagText.startsWith("http://") || tagText.startsWith("https://")) {
                                    tagText
                                } else {
                                    "https://$tagText"
                                }

                                Text(
                                    text = tagText,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            urlToOpen = formattedUrl
                                            showDialog = true
                                        }
                                )
                            } else {
                                Text(
                                    text = if (tagText == "Avvicina un tag NFC...") "Avvicina un tag NFC$dots" else tagText,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                        }
                    }
                    val contentColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onSecondary
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = contentColor
                        ),
                        onClick = {
                            tagText = "Avvicina un tag NFC..."
                        },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Scansiona di nuovo", color=contentColor)
                    }
                }

            }
            if (showDialog && urlToOpen != null) {
                OpenUrlDialog(
                    url = urlToOpen!!,
                    context = context,
                    onDismiss = {
                        showDialog = false
                        urlToOpen = null
                    }
                )
            }

        }
    }
}