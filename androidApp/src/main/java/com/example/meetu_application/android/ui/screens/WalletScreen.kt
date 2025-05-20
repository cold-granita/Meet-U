package com.example.meetu_application.android.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.meetu_application.android.data.storage.loadCardsFromWallet
import com.example.meetu_application.android.ui.components.CardView
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(navController: NavHostController) {
    val context = LocalContext.current
    val cards = remember { mutableStateOf(loadCardsFromWallet(context)) }

    // Stato per animazione manuale
    val visibleStates = remember {
        cards.value.map { mutableStateOf(false) }
    }

    // Avvia l'animazione in sequenza
    LaunchedEffect(Unit) {
        visibleStates.forEachIndexed { index, state ->
            delay(100L * index) // delay tra una carta e l'altra
            state.value = true
        }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                "Il mio Wallet",
                style = MaterialTheme.typography.titleLarge
            )
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
    }) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(cards.value) { index, card ->
                AnimatedVisibility(
                    visible = visibleStates[index].value,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                ) {
                    CardView(
                        card = card,
                        onClick = {
                            navController.navigate("cardDetail/${card.id}")
                            Log.d("DEBUG", "Clicked card id: ${card.id}")

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }
    }
}