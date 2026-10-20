package library.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import library.mobile.data.Book
import library.mobile.data.Genre
import library.mobile.data.Member
import library.mobile.ui.LibraryUiState
import library.mobile.ui.components.BookCard
import library.mobile.ui.components.EmptyState
import library.mobile.ui.components.GenreChips
import library.mobile.ui.components.LabeledCheckbox
import library.mobile.ui.components.MemberSelector
import library.mobile.ui.components.SearchField

class CatalogActions(
    val onQueryChange: (String) -> Unit,
    val onGenreToggle: (Genre) -> Unit,
    val onAvailableOnlyToggle: () -> Unit,
    val onMemberSelect: (Member) -> Unit,
    val onBorrow: (Book) -> Unit,
    val onReturn: (Book) -> Unit,
)

@Composable
fun CatalogScreen(state: LibraryUiState, actions: CatalogActions, contentPadding: PaddingValues) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(CARD_MIN_WIDTH),
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(contentPadding)
            .imePadding(),
        contentPadding = contentPadding.expandedBy(16.dp, extraBottom = FAB_CLEARANCE),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = fullWidth) { CatalogFilters(state, actions) }
        if (state.isLoadingArrivals) {
            item(span = fullWidth) { ArrivalsProgress() }
        }
        if (state.catalog.isEmpty()) {
            item(span = fullWidth) { EmptyState("No books match your search") }
        }
        items(state.catalog, key = { it.book.isbn }) { item ->
            BookCard(
                item = item,
                selectedMember = state.selectedMember,
                today = state.today,
                onBorrow = { actions.onBorrow(item.book) },
                onReturn = { actions.onReturn(item.book) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun CatalogFilters(state: LibraryUiState, actions: CatalogActions) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SearchField(query = state.filter.query, onQueryChange = actions.onQueryChange)
        GenreChips(selected = state.filter.genres, onGenreClick = actions.onGenreToggle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            LabeledCheckbox(
                label = "Available only",
                isChecked = state.filter.isAvailableOnly,
                onCheckedChange = { actions.onAvailableOnlyToggle() },
            )
            Text(
                "${state.catalog.size} books",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            "Borrowing as",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        MemberSelector(members = state.members, selected = state.selectedMember, onSelect = actions.onMemberSelect)
    }
}

@Composable
private fun ArrivalsProgress() {
    Column {
        Text(
            "Loading new arrivals from publishers…",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(Modifier.fillMaxWidth())
    }
}

private val FAB_CLEARANCE = 72.dp
