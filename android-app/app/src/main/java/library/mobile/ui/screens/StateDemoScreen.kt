package library.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StateDemoScreen(viewModelCount: Int, onViewModelIncrement: () -> Unit, contentPadding: PaddingValues) {
    var rememberCount by remember { mutableIntStateOf(0) }
    var saveableCount by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Tap each counter, then rotate the device.", style = MaterialTheme.typography.titleMedium)
        CounterCard(
            title = "remember",
            explanation = "Kept in the composition. Survives recomposition, but is lost when the Activity " +
                "is recreated, so it resets to 0 after rotation.",
            count = rememberCount,
            onIncrement = { rememberCount++ },
        )
        CounterCard(
            title = "rememberSaveable",
            explanation = "Written to the saved-instance-state Bundle. Survives rotation and process death, " +
                "but only for small values that fit in a Bundle.",
            count = saveableCount,
            onIncrement = { saveableCount++ },
        )
        CounterCard(
            title = "ViewModel",
            explanation = "Lives in the ViewModelStore, outside the Activity. Survives rotation with no " +
                "serialization and holds the app logic, but is lost when the screen is finished or the " +
                "process is killed.",
            count = viewModelCount,
            onIncrement = onViewModelIncrement,
        )
    }
}

@Composable
private fun CounterCard(title: String, explanation: String, count: Int, onIncrement: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text(explanation, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$count", style = MaterialTheme.typography.headlineMedium)
                FilledTonalButton(onClick = onIncrement) { Text("+1") }
            }
        }
    }
}
