package library.mobile.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import library.mobile.R
import library.mobile.ui.components.AddBookDialog
import library.mobile.ui.screens.CatalogActions
import library.mobile.ui.screens.CatalogScreen
import library.mobile.ui.screens.LoansActions
import library.mobile.ui.screens.LoansScreen
import library.mobile.ui.screens.StateDemoScreen

enum class LibraryTab(val label: String, @param:DrawableRes val icon: Int) {
    CATALOG("Catalog", R.drawable.ic_book),
    LOANS("My loans", R.drawable.ic_person),
    STATE("State demo", R.drawable.ic_rotate),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryApp(viewModel: LibraryViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(LibraryTab.CATALOG) }
    var isAddDialogOpen by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.onMessageShown()
    }

    val navigationType = navigationLayoutType()
    NavigationSuiteScaffold(
        navigationSuiteType = navigationType,
        navigationItemVerticalArrangement = Arrangement.Center,
        navigationItems = {
            LibraryTab.entries.forEach { tab ->
                NavigationSuiteItem(
                    selected = tab == selectedTab,
                    onClick = { selectedTab = tab },
                    icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                    label = { Text(tab.label) },
                    navigationSuiteType = navigationType,
                )
            }
        },
    ) {
        key(selectedTab) {
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            TabScaffold(
                state = state,
                scrollBehavior = scrollBehavior,
                snackbarHostState = snackbarHostState,
                floatingActionButton = {
                    if (selectedTab == LibraryTab.CATALOG) {
                        AddBookButton(onClick = { isAddDialogOpen = true })
                    }
                },
            ) { contentPadding ->
                when (selectedTab) {
                    LibraryTab.CATALOG -> CatalogScreen(
                        state = state,
                        actions = rememberCatalogActions(viewModel),
                        contentPadding = contentPadding,
                        onSearchFocus = { scrollBehavior.collapse() },
                    )
                    LibraryTab.LOANS -> LoansScreen(state, rememberLoansActions(viewModel), contentPadding)
                    LibraryTab.STATE -> StateDemoScreen(state.viewModelCounter, viewModel::incrementCounter, contentPadding)
                }
            }
        }
    }

    if (isAddDialogOpen) {
        AddBookDialog(
            onDismiss = { isAddDialogOpen = false },
            onAdd = { draft ->
                viewModel.addBook(draft)
                isAddDialogOpen = false
            },
        )
    }
}

@Composable
private fun navigationLayoutType(): NavigationSuiteType {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        NavigationSuiteType.NavigationRail
    } else {
        NavigationSuiteType.NavigationBar
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabScaffold(
    state: LibraryUiState,
    scrollBehavior: TopAppBarScrollBehavior,
    snackbarHostState: SnackbarHostState,
    floatingActionButton: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { LibraryTopBar(state, scrollBehavior) },
        floatingActionButton = floatingActionButton,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryTopBar(state: LibraryUiState, scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        title = {
            Column {
                Text("University Library")
                Text(
                    "Day ${state.today} · ${state.selectedMember.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface),
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBarScrollBehavior.collapse() {
    state.heightOffset = state.heightOffsetLimit
}

@Composable
private fun AddBookButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = { Icon(painterResource(R.drawable.ic_add), contentDescription = "Add book") },
        text = { Text("Add book") },
    )
}

@Composable
private fun rememberCatalogActions(viewModel: LibraryViewModel): CatalogActions = remember(viewModel) {
    CatalogActions(
        onQueryChange = viewModel::onQueryChange,
        onGenreToggle = viewModel::onGenreToggle,
        onAvailableOnlyToggle = viewModel::onAvailableOnlyToggle,
        onMemberSelect = viewModel::onMemberSelect,
        onBorrow = viewModel::borrow,
        onReturn = viewModel::returnBook,
    )
}

@Composable
private fun rememberLoansActions(viewModel: LibraryViewModel): LoansActions = remember(viewModel) {
    LoansActions(
        onMemberSelect = viewModel::onMemberSelect,
        onReturn = viewModel::returnBook,
        onAdvanceDay = viewModel::advanceDay,
    )
}
