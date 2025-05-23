
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.meetu_application.android.data.model.Card
import com.example.meetu_application.android.data.utils.isValidEmail
import com.example.meetu_application.android.data.utils.isValidPhone
import com.example.meetu_application.android.ui.components.ValidatedInputField

@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onAdd: (Card) -> Unit,
    onPickContact: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var webSite by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var touchedName by remember { mutableStateOf(false) }
    var touchedSurname by remember { mutableStateOf(false) }
    var touchedPhone by remember { mutableStateOf(false) }
    var touchedEmail by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() &&
            surname.isNotBlank() &&
            isValidPhone(phone) &&
            isValidEmail(email)


    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 10.dp,
            modifier = Modifier
                .padding(20.dp)
                .heightIn(max = 520.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState), // Scroll verticale
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Aggiungi Card",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = W700,
                    color = MaterialTheme.colorScheme.secondary
                    )

                val textFieldColors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF9FD0FF),
                    unfocusedContainerColor = Color(0xFFC8E4FF),
                    errorContainerColor = Color(0xFFFFCDD2),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
                ValidatedInputField(
                    label = "Nome*",
                    value = name,
                    onValueChange = { name = it },
                    isError = touchedName && name.isBlank(),
                    errorMessage = if (touchedName && name.isBlank()) "Campo obbligatorio" else null,
                    onFocusLost = { touchedName = true },
                    placeholder = "Mario",
                )
                ValidatedInputField(
                    label = "Cognome*",
                    value = surname,
                    onValueChange = { surname = it },
                    isError = touchedSurname && surname.isBlank(),
                    errorMessage = if (touchedSurname && surname.isBlank()) "Campo obbligatorio" else null,
                    onFocusLost = { touchedSurname = true },
                    placeholder = "Rossi",
                )
                ValidatedInputField(
                    label = "Telefono*",
                    placeholder = "0123456789",
                    value = phone,
                    onValueChange = { phone = it },
                    isError = touchedPhone && (!isValidPhone(phone) || email.isBlank()),
                    errorMessage = when {
                        touchedPhone && phone.isBlank() -> "Campo obbligatorio"
                        touchedPhone && !isValidPhone(phone) -> "Formato telefono non valido"
                        else -> null
                    },
                    onFocusLost = { touchedPhone = true },
                    keyboardType = KeyboardType.Phone
                )
                ValidatedInputField(
                    label = "Email*",
                    placeholder = "mario@example.com",
                    value = email,
                    onValueChange = { email = it },
                    isError = touchedEmail && (!isValidEmail(email) ||email.isBlank()) ,
                    errorMessage = when {
                        touchedEmail && email.isBlank() -> "Campo obbligatorio"
                        touchedEmail && !isValidEmail(email) -> "Formato email non valido"
                        else -> null
                    },
                    onFocusLost = {  touchedEmail = true},
                    keyboardType = KeyboardType.Email
                )
                ValidatedInputField(
                    label = "Organizzazione",
                    placeholder = "Nome Azienda",
                    value = organization,
                    onValueChange = { organization = it },
                    isError = false,
                    errorMessage = null,
                    onFocusLost = {}
                )
                ValidatedInputField(
                    label = "Titolo",
                    placeholder = "Manager",
                    value = title,
                    onValueChange = { title = it },
                    isError = false,
                    errorMessage = null,
                    onFocusLost = {}
                )
                ValidatedInputField(
                    label = "Indirizzo",
                    placeholder = "Via Roma 1, Milano",
                    value = address,
                    onValueChange = { address = it },
                    isError = false,
                    errorMessage = null,
                    onFocusLost = {}
                )
                ValidatedInputField(
                    label = "Sito web",
                    placeholder = "https://www.example.com",
                    value = webSite,
                    onValueChange = { webSite = it },
                    isError = false,
                    errorMessage = null,
                    onFocusLost = {}
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onPickContact,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Aggiungi", tint = Color.White)
                    Text(" Aggiungi da Rubrica")
                }
                Button(
                    onClick = {
                        val newCard = Card.fromInput(
                            name,
                            surname,
                            phone.ifBlank { null },
                            email.ifBlank { null },
                            organization.ifBlank { null },
                            title.ifBlank { null },
                            webSite.ifBlank { null },
                            address.ifBlank { null },
                        )
                        onAdd(newCard)
                    },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.DarkGray,
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val iconTint = if (isFormValid) Color.White else Color.Gray
                    val textColor = if (isFormValid) Color.White else Color.Gray
                    Icon(Icons.Default.Check, contentDescription = "Aggiungi", tint = iconTint)
                    Text(" Crea Card", color = textColor)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annulla", color = Color.Red)
                    }
                }
            }
        }
    }
}

