package library.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import library.mobile.R
import library.mobile.data.Book
import library.mobile.data.Loan
import library.mobile.data.Member
import library.mobile.data.Receipt
import library.mobile.ui.LibraryUiState
import library.mobile.ui.components.BookCover
import library.mobile.ui.components.EmptyState
import library.mobile.ui.components.MemberSelector
import library.mobile.ui.components.SectionHeader
import library.mobile.ui.components.StatCard
import library.mobile.data.formatMoney

class LoansActions(
    val onMemberSelect: (Member) -> Unit,
    val onReturn: (Book) -> Unit,
    val onAdvanceDay: () -> Unit,
)

@Composable
fun LoansScreen(state: LibraryUiState, actions: LoansActions, contentPadding: PaddingValues) {
    val member = state.selectedMember

    LazyVerticalGrid(
        columns = GridCells.Adaptive(CARD_MIN_WIDTH),
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding.expandedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = fullWidth) {
            MemberSelector(members = state.members, selected = member, onSelect = actions.onMemberSelect)
        }
        item(span = fullWidth) { MemberCard(member) }
        item(span = fullWidth) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Loans", "${state.memberLoans.size} / ${member.loanLimit}", Modifier.weight(1f))
                StatCard("Day", "${state.today}", Modifier.weight(1f))
                StatCard("Fees paid", formatMoney(state.memberFees), Modifier.weight(1f))
            }
        }
        item(span = fullWidth) {
            Button(onClick = actions.onAdvanceDay, modifier = Modifier.fillMaxWidth()) {
                Icon(painterResource(R.drawable.ic_next_day), contentDescription = null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Next day")
            }
        }
        item(span = fullWidth) { SectionHeader("Current loans") }
        if (state.memberLoans.isEmpty()) {
            item(span = fullWidth) { EmptyState("${member.name} has no books right now. Borrow one in the catalog.") }
        }
        items(state.memberLoans, key = { it.book.isbn }) { loan ->
            LoanCard(loan, today = state.today, onReturn = { actions.onReturn(loan.book) })
        }
        if (state.memberReceipts.isNotEmpty()) {
            item(span = fullWidth) { SectionHeader("Returned") }
            items(state.memberReceipts.asReversed()) { receipt -> ReceiptRow(receipt) }
        }
    }
}

@Composable
private fun MemberCard(member: Member) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_person), contentDescription = null, Modifier.size(40.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("${member.name} · ${member.role}", style = MaterialTheme.typography.titleMedium)
                Text(member.describe(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun LoanCard(loan: Loan, today: Int, onReturn: () -> Unit) {
    val isOverdue = loan.isOverdue(today)
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            BookCover(loan.book, Modifier.size(width = 40.dp, height = 52.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(loan.book.title, style = MaterialTheme.typography.titleSmall)
                Text(
                    if (isOverdue) "Overdue! Was due on day ${loan.dueDay}" else "Due on day ${loan.dueDay}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (isOverdue) {
                    Text(
                        "Fee if returned today: ${formatMoney(Receipt(loan, today).fee)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            OutlinedButton(onClick = onReturn) { Text("Return") }
        }
    }
}

@Composable
private fun ReceiptRow(receipt: Receipt) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
            Text(receipt.loan.book.title, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Returned on day ${receipt.returnedOnDay}" +
                    if (receipt.daysLate > 0) ", ${receipt.daysLate} days late" else ", on time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(formatMoney(receipt.fee), style = MaterialTheme.typography.titleSmall)
    }
}
