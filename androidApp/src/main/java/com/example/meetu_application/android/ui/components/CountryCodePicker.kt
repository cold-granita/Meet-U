package com.example.meetu_application.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Country(
    val code: String,
    val emoji: String,
    val dialCode: String
)

// Lista base di paesi con emoji e prefisso
val countries = listOf(
    Country("IT", "🇮🇹", "+39"),
    Country("US", "🇺🇸", "+1"),
    Country("GB", "🇬🇧", "+44"),
    Country("FR", "🇫🇷", "+33"),
    Country("DE", "🇩🇪", "+49"),
    Country("ES", "🇪🇸", "+34"),
    Country("CN", "🇨🇳", "+86"),
    Country("JP", "🇯🇵", "+81"),
    Country("IN", "🇮🇳", "+91"),
    Country("CA", "🇨🇦", "+1"),
    Country("BR", "🇧🇷", "+55"),
    Country("AU", "🇦🇺", "+61"),
    Country("RU", "🇷🇺", "+7"),
    Country("MX", "🇲🇽", "+52"),
    Country("AR", "🇦🇷", "+54"),
    Country("ZA", "🇿🇦", "+27"),
    Country("KR", "🇰🇷", "+82"),
    Country("TR", "🇹🇷", "+90"),
    Country("EG", "🇪🇬", "+20")
    // aggiungerne altri
)

@Composable
fun CountryCodePicker(
    selectedDialCode: String,
    onDialCodeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val darkTheme = isSystemInDarkTheme()

    // Colori per modalità chiara/scura
    val backgroundColor = if (darkTheme) Color(0xFF0D47A1) else Color(0xFFC8E4FF)  // blu scuro o azzurrino
    val dropdownBackgroundColor = if (darkTheme) Color(0xFF1565C0) else Color.White // sfondo menu a tendina
    val textColor = if (darkTheme) Color.White else Color.Black

    // Trova paese selezionato dalla lista
    val selectedCountry = countries.find { it.dialCode == selectedDialCode } ?: countries[0]

    Surface(
        modifier = modifier.clickable { expanded = true },
        shape = MaterialTheme.shapes.small,
        tonalElevation = 4.dp,
        color = backgroundColor
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = "${selectedCountry.emoji} ${selectedCountry.dialCode}", color = textColor)
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Seleziona prefisso")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 300.dp),
            containerColor = dropdownBackgroundColor
        ) {
            countries.forEach { country ->
                DropdownMenuItem(
                    text = { Text(text = "${country.emoji} ${country.dialCode}") },
                    onClick = {
                        onDialCodeChanged(country.dialCode)
                        expanded = false
                    }
                )
            }
        }
    }
}
