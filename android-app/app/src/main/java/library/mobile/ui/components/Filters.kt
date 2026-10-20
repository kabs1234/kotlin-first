package library.mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import library.mobile.R
import library.mobile.data.Genre
import library.mobile.data.Member

@Composable
fun SearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search by title or author") },
        leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
    )
}

@Composable
fun GenreChips(
    selected: Set<Genre>,
    onGenreClick: (Genre) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(Genre.entries) { genre ->
            FilterChip(
                selected = genre in selected,
                onClick = { onGenreClick(genre) },
                label = { Text(genre.label) },
            )
        }
    }
}

@Composable
fun MemberSelector(
    members: List<Member>,
    selected: Member,
    onSelect: (Member) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(members, key = { it.id }) { member ->
            FilterChip(
                selected = member == selected,
                onClick = { onSelect(member) },
                label = { Text(member.name) },
                leadingIcon = {
                    Icon(painterResource(R.drawable.ic_person), contentDescription = null, Modifier.padding(2.dp))
                },
            )
        }
    }
}

@Composable
fun LabeledCheckbox(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.toggleable(value = isChecked, onValueChange = onCheckedChange, role = Role.Checkbox),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = isChecked, onCheckedChange = null)
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
