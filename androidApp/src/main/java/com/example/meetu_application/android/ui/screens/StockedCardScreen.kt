package com.example.meetu_application.android.ui.screens

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.data.storage.PreferenceManager
import com.example.meetu_application.android.ui.components.ClickableCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StackedCardScreen(
    cards: List<Card>,
    navController: NavHostController,
    onAddClick: () -> Unit,
    onExpandToWallet: () -> Unit
) {
    SharedTransitionLayout {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        var isReady by remember { mutableStateOf(false) }

        val preferredCardId by PreferenceManager.getPreferredCardId(context).collectAsState(initial = null)

        // Ritarda la UI finché cards è popolata e preferredCardId ha emesso almeno una volta
        LaunchedEffect(cards, preferredCardId) {
            if (cards.isNotEmpty()) {
                delay(100) // extra delay per evitare glitch visivo
                isReady = true
            }
        }

        if (!isReady && cards.size >= 2) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
            return@SharedTransitionLayout
        }

        val preferredCard = cards.find { it.id == preferredCardId } ?: cards.firstOrNull()
        val otherCards = cards.filter { it.id != preferredCard?.id }

        var offsetYTotal by remember { mutableStateOf(0f) }
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, dragAmount ->
                            offsetYTotal += dragAmount
                            if (offsetYTotal > 100f) expanded = true
                            if (offsetYTotal < -100f) expanded = false
                        },
                        onDragEnd = {
                            if (expanded) {
                                coroutineScope.launch {
                                    // ora puoi usare delay!
                                    delay(300L)
                                    onExpandToWallet()
                                }
                            }
                            offsetYTotal = 0f
                        },
                        onDragCancel = { offsetYTotal = 0f }
                    )
                }
        ) {
            preferredCard?.let {
                    ClickableCard(
                        card = it,
                        navController = navController,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 32.dp)
                            .fillMaxWidth(0.9f)
                            .height(200.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(16.dp), // usa lo stesso shape della Card
                                clip = false              // non taglia i bordi
                            )
                            .zIndex(2f)
                    )
            }
            if(cards.isEmpty()){
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = com.example.meetu_application.android.ui.theme.colorMeetU,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .zIndex(5f)
                        .size(56.dp)
                        .clip(CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aggiungi", tint = Color.White)
                }
            }else{
               /* val visibleCardCount = otherCards.size.coerceAtMost(7)
                val baseOffset = (visibleCardCount * 32).dp + 300.dp
                val fabVerticalOffset = if (otherCards.size > 7) baseOffset + 24.dp else baseOffset
                */
                val baseFabOffset = 400.dp // posizione base per 4 o meno carte
                val extraOffsetPerCard = 55.dp
                val fabVerticalOffset = if (otherCards.size > 4) {
                    baseFabOffset + (otherCards.size - 4).coerceAtMost(3) * extraOffsetPerCard
                } else {
                    baseFabOffset
                }
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = com.example.meetu_application.android.ui.theme.colorMeetU,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = fabVerticalOffset)
                        .zIndex(5f)
                        .size(56.dp)
                        .clip(CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aggiungi", tint = Color.White)
                }

            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = (32 + 180 - 150).dp) //lontananza dalla carta preferred
            ) {
                otherCards.forEachIndexed { index, card ->
                    val offsetY = (index * 32).dp
                    val alphaValue by animateFloatAsState(
                        targetValue = when {
                            index >= 8 -> 0f
                            index >= 4 -> 0.6f
                            else -> 1f
                        },
                        label = "AlphaAnimation"
                    )
                    val bottomAlpha = (1f - index / 10f).coerceIn(0.2f, 1f) // Più alto è l'indice, più trasparente in basso
                    val contentAlpha = (1f - index / 8f).coerceIn(0.1f, 1f) // Dissolvo il contenuto (scritte)
                    ClickableCard(
                        card = card,
                        navController = navController,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = offsetY)
                            .fillMaxWidth(0.9f)
                            .height(200.dp)
                            .alpha(alphaValue)
                            .zIndex(-index.toFloat()),
                        bottomGradientAlpha = bottomAlpha,
                        contentAlpha = contentAlpha
                    )
                }
                // Mostra "+X carte" se ci sono più di 7
                if (otherCards.size > 7) {
                    val extraCards = otherCards.size - 7
                    androidx.compose.material3.Text(
                        text = "+$extraCards carte",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = ((7 * 32) + 200 + 8).dp) // posizionato sotto l'ultima carta visibile
                    )
                }
            }
        }
    }
}

