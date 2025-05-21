package com.example.meetu_application.android.ui.screens

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.data.storage.loadCardsFromWallet
import com.example.meetu_application.android.ui.components.ClickableCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(navController: NavHostController) {
    val context = LocalContext.current
    val cards = remember { mutableStateOf<List<Card>>(emptyList()) }

    // Stato per animazione visibile
    val visibleStates = remember {
        mutableStateListOf<MutableState<Boolean>>()
    }

    // Carica le carte all'avvio
    LaunchedEffect(Unit) {
        val loadedCards = loadCardsFromWallet(context)
        cards.value = loadedCards
        visibleStates.clear()
        visibleStates.addAll(List(loadedCards.size) { mutableStateOf(false) })

        visibleStates.forEachIndexed { index, state ->
            delay(100L * index)
            state.value = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Il mio Wallet", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Torna indietro")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(cards.value) { index, card ->
                AnimatedVisibility(
                    visible = visibleStates.getOrNull(index)?.value == true,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn()
                ) {
                    ClickableCard(
                        card = card,
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                }
            }
        }
    }
}
