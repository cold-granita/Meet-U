
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.meetu_application.android.data.model.Card

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

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 10.dp,
            modifier = Modifier
                .padding(20.dp)
                .heightIn(max = 520.dp)
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

                val textFieldColors = TextFieldDefaults.colors( ///sistema qua-
                    focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                )
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
                OutlinedTextField(value = surname, onValueChange = { surname = it }, label = { Text("Cognome") }, modifier = Modifier.fillMaxWidth(),colors = textFieldColors)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefono") }, modifier = Modifier.fillMaxWidth(),colors = textFieldColors)
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),colors = textFieldColors)
                OutlinedTextField(value = organization, onValueChange = { organization = it }, label = { Text("Organization") }, modifier = Modifier.fillMaxWidth(),colors = textFieldColors)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(),colors = textFieldColors)
                OutlinedTextField(value = webSite, onValueChange = { webSite = it }, label = { Text("Web Site") }, modifier = Modifier.fillMaxWidth(),colors = textFieldColors)
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(),colors = textFieldColors)

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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Aggiungi", tint = Color.White)
                    Text(" Crea Card")
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
