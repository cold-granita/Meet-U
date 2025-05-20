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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.ui.components.CardView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StackedCardScreen(
    cards: List<Card>,
    onAddClick: () -> Unit,
    onExpandToWallet: () -> Unit
) {
    SharedTransitionLayout {
        val coroutineScope = rememberCoroutineScope()
        val preferredCard = cards.find { it.isPreferred } ?: cards.firstOrNull()
        val otherCards = cards.filter { it != preferredCard }

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
                CardView(
                    card = it,
                    isPreferred = true,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp)
                        .fillMaxWidth(0.9f)
                        .height(180.dp)
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
                val visibleCardCount = otherCards.size.coerceAtMost(8)
                val fabVerticalOffset = (visibleCardCount * 32).dp + 280.dp
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
                    .padding(top = (32 + 180 - 100).dp)
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
                    CardView(
                        card = card,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = offsetY)
                            .fillMaxWidth(0.9f)
                            .height(140.dp)
                            .graphicsLayer { alpha = alphaValue }
                            .zIndex(-index.toFloat()),
                        bottomGradientAlpha = bottomAlpha,
                        contentAlpha = contentAlpha

                    )
                }
            }
        }
    }
}

