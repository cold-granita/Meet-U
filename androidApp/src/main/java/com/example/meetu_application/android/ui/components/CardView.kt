package com.example.meetu_application.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.meetu_application.android.R
import com.example.meetu_application.android.data.model.Card

@Composable
fun CardView(
    card: Card,
    modifier: Modifier = Modifier,
    bottomGradientAlpha: Float = 1f,
    contentAlpha: Float = 1f
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 220.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
                    .heightIn(220.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF5B84FF),
                                Color(0xFF67BFFF).copy(alpha = bottomGradientAlpha)
                            )
                        )
                    )
            ) {
                // ✅ Canvas decorativa
                Canvas(modifier = Modifier
                    .fillMaxSize()
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF3C6DFF), Color(0xFF4791FF)),
                            center = Offset(x = canvasWidth, y = 0f),
                            radius = canvasWidth * 0.3f
                        ),
                        radius = canvasWidth * 0.3f,
                        center = Offset(x = canvasWidth, y = 0f)
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF4975FD), Color(0xFF5498FF)),
                            center = Offset(x = 0f, y = canvasHeight),
                            radius = canvasWidth * 0.25f
                        ),
                        radius = canvasWidth * 0.25f,
                        center = Offset(x = 0f, y = canvasHeight)
                    )
                }

                // Overlay trasparente per effetto dissolvenza
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White.copy(alpha = 1f - contentAlpha))
                        .clip(RoundedCornerShape(16.dp))
                )

                // ✅ Contenuto senza alpha diretta
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    // Parte in alto: nome, cognome e titolo
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${card.name} ${card.surname}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        card.title?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Parte in basso: altri campi
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        card.telephoneNumber?.takeIf { it.isNotBlank() }?.let {
                            IconTextRow(Icons.Default.Phone, it, Color.White)
                        }
                        card.email?.takeIf { it.isNotBlank() }?.let {
                            IconTextRow(Icons.Default.Email, it, Color.White)
                        }
                        card.webSite?.takeIf { it.isNotBlank() }?.let {
                            IconTextRow(Icons.Default.Share, it, Color.White)
                        }
                        card.organization?.takeIf { it.isNotBlank() }?.let {
                            IconTextRow(painterResource(id = R.drawable.company), it, Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IconTextRow(icon: ImageVector, text: String, contentColor: Color = Color.White) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier
                .size(15.dp)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun IconTextRow(painter: Painter, text: String, contentColor: Color = Color.White) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}
