package com.example.meetu_application.android.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.meetu_application.android.ui.screens.WriteStatus

@Composable
fun WriteStatusView(status: WriteStatus, modifier: Modifier = Modifier) {
    when (status) {
        is WriteStatus.Success -> Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Successo",
                tint = Color(0xFF4CAF50) // verde
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = status.message,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        is WriteStatus.Error -> Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Errore",
                tint = Color(0xFFF44336) // rosso
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = status.message,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        is WriteStatus.Info -> Text(
            text = status.message,
            modifier = modifier
        )
        WriteStatus.None -> {}
    }
}
