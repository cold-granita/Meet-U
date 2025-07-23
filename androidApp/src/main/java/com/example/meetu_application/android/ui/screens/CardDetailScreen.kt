package com.example.meetu_application.android.ui.screens

import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.meetu_application.android.R
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.data.nfc.VCardApduService
import com.example.meetu_application.android.data.storage.PreferenceManager
import com.example.meetu_application.android.ui.components.AddToContactsButton
import com.example.meetu_application.android.ui.components.NFCSignalAnimation
import com.example.meetu_application.android.ui.theme.colorMeetU
import com.example.meetu_application.android.utils.generateQRCode
import com.example.meetu_application.android.utils.generateVCard
import kotlinx.coroutines.launch


    @OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(card: Card, navController: NavController) {
        val context = LocalContext.current
        Log.d("HCE",">>> CardDetailScreen creato con card: ${card.name} ${card.surname}")

        val vCardString = remember(card) { generateVCard(card) }
        Log.d("HCE",">>> generateVCard restituisce: $vCardString")

        val qrCodeBitmap = remember(vCardString) { generateQRCode(vCardString, size = 300) }

    LaunchedEffect(Unit) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        val isHceSupported = context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_NFC_HOST_CARD_EMULATION
        )
        Log.d("HCE", "Supporto HCE: $isHceSupported")

        Log.d("HCE",">>> Imposto currentVCard in VCardApduService")
        VCardApduService.isSendingEnabled = true
        VCardApduService.currentVCard = vCardString
        Log.d("HCE",">>> Current VCard set: $vCardString")
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("HCE",">>> Disposing CardDetailScreen, resetto VCard")
            VCardApduService.isSendingEnabled = false
            VCardApduService.currentVCard = ""
        }
    }

    val preferredCardId by PreferenceManager.getPreferredCardId(context).collectAsState(initial = null)
    val isPreferred = preferredCardId == card.id
    val coroutineScope = rememberCoroutineScope()

    val darkTheme = isSystemInDarkTheme()

        Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("${card.name} ${card.surname}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("wallet") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Torna indietro"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            PreferenceManager.setPreferredCardId(context, card.id)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = if (isPreferred) R.drawable.star else R.drawable.star_outlined),
                            contentDescription = "Stella preferita",
                            modifier = Modifier.size(27.dp),
                            tint = Color(0xffffbb00)
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NFCSignalAnimation(modifier = Modifier.size(60.dp))
                Text(
                    text = "Avvicina ad un Meet-U",
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(start = 4.dp)

                )
            }

            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "${card.name} ${card.surname}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorMeetU
                    )

                    card.telephoneNumber?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(icon = Icons.Default.Phone, label = "Telefono", value = it)
                    }
                    card.email?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(icon = Icons.Default.Email, label = "Email", value = it)
                    }
                    card.webSite?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(icon = Icons.Default.Share, label = "Web Site", value = it)
                    }

                    card.organization?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(painterIcon = painterResource( R.drawable.company), label = "Organization", value = it)
                    }
                    card.title?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(icon = Icons.Default.Face, label = "Title", value = it)
                    }
                    card.address?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(icon = Icons.Default.LocationOn, label = "Address", value = it)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {navController.navigate("cardEdit/${card.id}") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = if (darkTheme) Color.White else MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Icon(Icons.Default.Create, contentDescription = "Edit card", tint = Color.White)
                Text(" Modifica la Card")
            }

            Spacer(modifier = Modifier.height(10.dp))

            AddToContactsButton(card, modifier = Modifier.padding(horizontal = 24.dp))

            Spacer(modifier = Modifier.height(24.dp))

            qrCodeBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector? = null,
    painterIcon: Painter? = null,
    label: String,
    value: String?
    ) {
    if (value == null) return

    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = colorMeetU,
                        modifier = Modifier.size(24.dp)
                    )
                }
                painterIcon != null -> {
                    Icon(
                        painter = painterIcon,
                        contentDescription = label,
                        tint = colorMeetU,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column {
                Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
        }
}
