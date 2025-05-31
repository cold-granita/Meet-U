package com.example.meetu_application.android.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.meetu_application.android.R
import com.example.meetu_application.android.data.storage.loadCardsFromWallet
import com.example.meetu_application.android.data.storage.saveCardsToWallet
import com.example.meetu_application.android.data.utils.ContactHelper
import com.example.meetu_application.android.ui.components.AddCardDialog
import com.example.meetu_application.android.ui.components.IconWithLabel

@Composable
fun MainScreen(navController: NavHostController) {
    var contactsReady by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var cards by remember { mutableStateOf(loadCardsFromWallet(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        contactsReady = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Permesso contatti negato", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        contactsReady = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        if (!contactsReady) {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let {
            val card = ContactHelper.getCardFromContactUri(context.contentResolver, it)
            card?.let { newCard ->
                cards = cards + newCard
                saveCardsToWallet(context, cards)
                Toast.makeText(context, "Carta contatto aggiunta!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            StackedCardScreen(
                cards = cards,
                navController = navController,
                onAddClick = { showDialog = true },
                onExpandToWallet = {
                    Toast.makeText(context, "Navigazione wallet", Toast.LENGTH_SHORT).show()
                    navController.navigate("wallet")
                }


            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                IconWithLabel(
                    icon = painterResource(id = R.drawable.wallet_icon),
                    label = "Wallet",
                    onClick = { navController.navigate("wallet") },
                    modifier = Modifier.weight(1f)
                )
                IconWithLabel(
                    icon = painterResource(id = R.drawable.nfc_writer_icon),
                    label = "NFC writer",
                    onClick = { navController.navigate("nfcWriter") },
                    modifier = Modifier.weight(1f)
                )
                IconWithLabel(
                    icon = painterResource(id = R.drawable.nfc_reader_icon),
                    label = "NFC reader",
                    onClick = { navController.navigate("nfcReader") },
                    modifier = Modifier.weight(1f)
                )
            }


            if (showDialog) {
                AddCardDialog(
                    onDismiss = { showDialog = false },
                    onAdd = { newCard ->
                        cards = cards + newCard
                        saveCardsToWallet(context, cards)
                        Toast.makeText(context, "Carta aggiunta con successo!", Toast.LENGTH_SHORT).show()
                        showDialog = false
                    },
                            onPickContact = {
                        if (contactsReady) {
                            pickContactLauncher.launch(null)
                        } else {
                            Toast.makeText(context, "Devi concedere il permesso ai contatti", Toast.LENGTH_SHORT).show()
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                        showDialog = false
                    }
                )
            }
        }
    }
}

