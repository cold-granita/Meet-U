package com.example.meetu_application.android.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.data.storage.loadCardsFromWallet
import com.example.meetu_application.android.ui.components.ClickableCard
import com.example.meetu_application.android.ui.components.ScrollDownArrow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(navController: NavHostController) {
    val context = LocalContext.current
    val cards = remember { mutableStateOf<List<Card>>(emptyList()) }

    val listState = rememberLazyListState()

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
            delay(25L * index)
            state.value = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Wallet", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
        ) {
        if (cards.value.isEmpty()) {
            Box( modifier = Modifier
                .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text ="Nessuna card presente.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 0.dp,
                        start = padding.calculateStartPadding(LayoutDirection.Ltr),
                        end = padding.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = padding.calculateBottomPadding()
                    )
                    .padding(horizontal = 16.dp)
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
            ScrollDownArrow(
                listState = listState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
}