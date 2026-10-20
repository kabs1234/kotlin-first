package library.mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.Year
import library.mobile.data.Genre
import library.mobile.ui.BookDraft

@Composable
fun AddBookDialog(onDismiss: () -> Unit, onAdd: (BookDraft) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }
    var yearText by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf(Genre.CLASSIC) }

    val publishedYear = yearText.toIntOrNull()?.takeIf { it in EARLIEST_YEAR..Year.now().value }
    val isYearInvalid = yearText.isNotEmpty() && publishedYear == null
    val draft = publishedYear
        ?.takeIf { title.isNotBlank() && author.isNotBlank() }
        ?.let { BookDraft(title, author, it, genre) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a book") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = yearText,
                    onValueChange = { input -> yearText = input.filter(Char::isDigit).take(YEAR_DIGITS) },
                    label = { Text("Year") },
                    isError = isYearInvalid,
                    supportingText = if (isYearInvalid) {
                        { Text("Enter a year between $EARLIEST_YEAR and ${Year.now().value}") }
                    } else {
                        null
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                Text("Genre", style = MaterialTheme.typography.labelLarge)
                GenreChips(selected = setOf(genre), onGenreClick = { genre = it })
            }
        },
        confirmButton = {
            TextButton(onClick = { draft?.let(onAdd) }, enabled = draft != null) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

private const val EARLIEST_YEAR = 1450
private const val YEAR_DIGITS = 4
