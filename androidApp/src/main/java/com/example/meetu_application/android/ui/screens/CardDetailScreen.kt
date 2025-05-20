package com.example.meetu_application.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.data.nfc.VCardApduService
import com.example.meetu_application.android.ui.theme.colorMeetU
import com.example.meetu_application.android.utils.generateQRCode
import com.example.meetu_application.android.utils.generateVCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(card: Card, navController: NavController, onVCardReady: (String)->Unit) {

    val vCardString = remember(card) { generateVCard(card) }
    val qrCodeBitmap = remember(vCardString) { generateQRCode(vCardString, size = 300) }

    LaunchedEffect(Unit) {
        VCardApduService.isSendingEnabled = true
        VCardApduService.currentVCard = vCardString
    }

    DisposableEffect(Unit) {
        onDispose {
            VCardApduService.isSendingEnabled = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("${card.name} ${card.surname}") },
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
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

                    DetailRow(icon = Icons.Default.Phone, label = "Telefono", value = card.telephoneNumber)
                    DetailRow(icon = Icons.Default.Email, label = "Email", value = card.email)
                    DetailRow(icon = Icons.Default.Home, label = "Organization", value = card.organization)
                    DetailRow(icon = Icons.Default.Face, label = "Title", value = card.title)
                    DetailRow(icon = Icons.Default.Share, label = "Web Site", value = card.webSite)
                    DetailRow(icon = Icons.Default.LocationOn, label = "Address", value = card.address)
                    DetailRow(label = "Preferito", value = if (card.isPreferred) "Sì" else "No")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            qrCodeBitmap?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Image(
                        bitmap = it,
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(300.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun DetailRow(
    icon: ImageVector? = null,
    label: String,
    value: String?
) {
    if (value == null) return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = colorMeetU,
                modifier = Modifier
                    .size(24.dp)
            )
        }

        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

