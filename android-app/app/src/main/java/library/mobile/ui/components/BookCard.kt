package library.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import library.mobile.R
import library.mobile.data.Book
import library.mobile.data.Member
import library.mobile.ui.BookItem
import library.mobile.ui.theme.coverColor

@Composable
fun BookCard(
    item: BookItem,
    selectedMember: Member,
    today: Int,
    onBorrow: () -> Unit,
    onReturn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        onClick = { isExpanded = !isExpanded },
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BookCover(item.book, Modifier.size(width = 52.dp, height = 68.dp))
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        item.book.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "${item.book.author} · ${item.book.publishedYear}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    LoanStatus(item, today)
                }
                Spacer(Modifier.width(8.dp))
                BookAction(item, selectedMember, onBorrow, onReturn)
            }
            if (isExpanded) {
                BookDetails(item.book)
            }
        }
    }
}

@Composable
fun BookCover(book: Book, modifier: Modifier = Modifier) {
    val color = book.genres.firstOrNull()?.coverColor() ?: MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_book),
            contentDescription = null,
            modifier = Modifier.size(26.dp),
            colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.9f)),
        )
    }
}

@Composable
private fun LoanStatus(item: BookItem, today: Int) {
    val loan = item.loan
    val (text, color) = when {
        loan == null -> "Available" to MaterialTheme.colorScheme.primary
        loan.isOverdue(today) -> "Overdue since day ${loan.dueDay} · ${loan.member.name}" to MaterialTheme.colorScheme.error
        else -> "Due day ${loan.dueDay} · ${loan.member.name}" to MaterialTheme.colorScheme.secondary
    }
    Text(text, style = MaterialTheme.typography.labelLarge, color = color)
}

@Composable
private fun BookDetails(book: Book) {
    Column(Modifier.padding(top = 12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            book.genres.forEach { genre ->
                SuggestionChip(onClick = {}, label = { Text(genre.label) })
            }
        }
        Text(
            "ISBN ${book.isbn.take(ISBN_PREVIEW_LENGTH)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BookAction(item: BookItem, selectedMember: Member, onBorrow: () -> Unit, onReturn: () -> Unit) {
    when (item.loan?.member) {
        null -> FilledTonalButton(onClick = onBorrow) { Text("Borrow") }
        selectedMember -> OutlinedButton(onClick = onReturn) { Text("Return") }
        else -> OutlinedButton(onClick = {}, enabled = false) { Text("On loan") }
    }
}

private const val ISBN_PREVIEW_LENGTH = 13
