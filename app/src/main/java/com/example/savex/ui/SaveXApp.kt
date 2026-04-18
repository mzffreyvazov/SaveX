package com.example.savex.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.InsertChartOutlined
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

private const val ROUTE_HOME = "home"
private const val ROUTE_STARRED = "starred"
private const val ROUTE_COLLECTIONS = "collections"
private const val ROUTE_ARCHIVED = "archived"
private const val ROUTE_SAVE = "save"

private data class TopLevelDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
)

private data class DrawerDestination(
    val title: String,
    val icon: ImageVector,
)

private val topLevelDestinations = listOf(
    TopLevelDestination(ROUTE_HOME, "Home", Icons.Outlined.Home),
    TopLevelDestination(ROUTE_STARRED, "Starred", Icons.Outlined.StarOutline),
    TopLevelDestination(ROUTE_COLLECTIONS, "Collections", Icons.Outlined.CollectionsBookmark),
    TopLevelDestination(ROUTE_ARCHIVED, "Archived", Icons.Outlined.Archive),
)

private val drawerDestinations = listOf(
    DrawerDestination("Tags", Icons.AutoMirrored.Outlined.Label),
    DrawerDestination("Snoozed", Icons.Outlined.Bedtime),
    DrawerDestination("Bin", Icons.Outlined.Delete),
    DrawerDestination("My stats", Icons.Outlined.InsertChartOutlined),
    DrawerDestination("Sync status", Icons.Outlined.Sync),
    DrawerDestination("Settings", Icons.Outlined.Settings),
    DrawerDestination("Help and feedback", Icons.AutoMirrored.Outlined.HelpOutline),
)

private val homeSearchCatalog = listOf(
    "The Architecture of a Modern Android App",
    "PostgreSQL JSONB Tutorial",
    "10 Best Jetpack Compose Tips",
    "Spring Boot 3 + JWT Auth",
    "Review Today",
    "Recently Saved",
    "Tags",
    "Snoozed",
    "Collections",
    "Archived",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SaveXApp(
    sharedText: String?,
    onSharedTextConsumed: () -> Unit,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val overlayInteractionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    var showCreateCollectionDialog by rememberSaveable { mutableStateOf(false) }
    var isFabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var homeSearchQuery by rememberSaveable { mutableStateOf("") }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isHomeDestination = currentDestination?.route == ROUTE_HOME
    val currentTitle = topLevelDestinations.firstOrNull { it.route == currentDestination?.route }?.title
        ?: if (currentDestination?.route == ROUTE_SAVE) "Save item" else "SaveX"

    val homeSearchResults = remember(homeSearchQuery) {
        homeSearchCatalog
            .filter { homeSearchQuery.isBlank() || it.contains(homeSearchQuery, ignoreCase = true) }
            .take(12)
    }

    LaunchedEffect(currentDestination?.route) {
        isFabMenuExpanded = false
    }

    BackHandler(enabled = isFabMenuExpanded) {
        isFabMenuExpanded = false
    }

    LaunchedEffect(sharedText) {
        if (!sharedText.isNullOrBlank()) {
            navController.navigate(ROUTE_SAVE)
            snackbarHostState.showSnackbar("Shared content loaded into Save.")
            onSharedTextConsumed()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
            ) {
                Text(
                    text = "SaveX",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                )
                drawerDestinations.forEachIndexed { index, destination ->
                    if (index == 2 || index == 5) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    NavigationDrawerItem(
                        label = { Text(destination.title) },
                        icon = { Icon(destination.icon, contentDescription = null) },
                        selected = false,
                        onClick = { },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    )
                }
            }
        },
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                HomeTopBar(
                    query = homeSearchQuery,
                    onQueryChange = { homeSearchQuery = it },
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onProfileClick = { },
                    showSearchBar = isHomeDestination,
                    title = currentTitle
                )
            },
            bottomBar = {
                ShortNavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    topLevelDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        ShortNavigationBarItem(
                            selected = selected,
                            onClick = {
                                isFabMenuExpanded = false
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = ShortNavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            icon = { Icon(destination.icon, contentDescription = destination.title) },
                            label = {
                                Text(
                                    text = destination.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                )
                            },
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = ROUTE_HOME,
                    modifier = Modifier.padding(innerPadding),
                ) {
                    composable(ROUTE_HOME) {
                        HomeScreen(modifier = Modifier.fillMaxSize())
                    }
                    composable(ROUTE_STARRED) {
                        LibraryPlaceholderScreen(
                            title = "Starred",
                            subtitle = "High-priority collections and items will live here.",
                            items = listOf(
                                "Starred collections pinned first",
                                "Starred individual items below",
                                "Fast path for what matters most",
                            ),
                        )
                    }
                    composable(ROUTE_COLLECTIONS) {
                        LibraryPlaceholderScreen(
                            title = "Collections",
                            subtitle = "Folders become the primary organizational layer for saved content.",
                            items = listOf(
                                "Folder-first browsing",
                                "Collection-level reminders",
                                "Easy grouping for themes and projects",
                            ),
                        )
                    }
                    composable(ROUTE_ARCHIVED) {
                        LibraryPlaceholderScreen(
                            title = "Archived",
                            subtitle = "Archived collections and items stay searchable without cluttering the active library.",
                            items = listOf(
                                "Archived collections pinned first",
                                "Archived items underneath",
                                "Safe soft-delete workflow later",
                            ),
                        )
                    }
                    composable(ROUTE_SAVE) {
                        SaveScreen(
                            initialSharedText = sharedText,
                            onCreateCollection = { showCreateCollectionDialog = true },
                        )
                    }
                }

                if (isFabMenuExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = overlayInteractionSource,
                                indication = null,
                            ) { isFabMenuExpanded = false },
                    )
                }

                AnimatedVisibility(
                    visible = isHomeDestination && homeSearchQuery.isNotBlank(),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding()),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (homeSearchResults.isEmpty()) {
                                item {
                                    Text(
                                        text = "No results for \"$homeSearchQuery\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                    )
                                }
                            } else {
                                items(homeSearchResults, key = { it }) { result ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { homeSearchQuery = result },
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        tonalElevation = 0.dp,
                                    ) {
                                        Text(
                                            text = result,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HomeFabMenu(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    expanded = isFabMenuExpanded,
                    onExpandedChange = { isFabMenuExpanded = it },
                    onCreateCollectionClick = {
                        isFabMenuExpanded = false
                        showCreateCollectionDialog = true
                    },
                    onSaveClick = {
                        isFabMenuExpanded = false
                        if (currentDestination?.route != ROUTE_SAVE) {
                            navController.navigate(ROUTE_SAVE)
                        }
                    },
                )
            }
        }
    }

    if (showCreateCollectionDialog) {
        CreateCollectionDialog(
            onDismiss = { showCreateCollectionDialog = false },
            onCreate = { showCreateCollectionDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenDrawer: () -> Unit,
    onProfileClick: () -> Unit,
    showSearchBar: Boolean,
    title: String,
) {
    var isSearchExpanded by rememberSaveable { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Outlined.Menu, contentDescription = "Open navigation drawer")
            }
        },
        title = {
            if (showSearchBar) {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = query,
                            onQueryChange = {
                                onQueryChange(it)
                                isSearchExpanded = true
                            },
                            onSearch = { isSearchExpanded = false },
                            expanded = isSearchExpanded,
                            onExpandedChange = { isSearchExpanded = it },
                            placeholder = { Text("Search") },
                        )
                    },
                    expanded = isSearchExpanded,
                    onExpandedChange = { isSearchExpanded = it },
                ) {
                    homeSearchCatalog
                        .filter { query.isBlank() || it.contains(query, ignoreCase = true) }
                        .take(8)
                        .forEach { suggestion ->
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onQueryChange(suggestion)
                                        isSearchExpanded = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            )
                        }
                }
            } else {
                Text(title)
            }
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Outlined.PersonOutline, contentDescription = "Profile")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeFabMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCreateCollectionClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    FloatingActionButtonMenu(
        modifier = modifier,
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = onExpandedChange,
                containerSize = ToggleFloatingActionButtonDefaults.containerSizeMedium(),
                containerCornerRadius = ToggleFloatingActionButtonDefaults.containerCornerRadiusMedium(),
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) Icons.Outlined.Close else Icons.Outlined.Add
                    }
                }
                Icon(
                    imageVector = imageVector,
                    contentDescription = if (expanded) "Close actions" else "Open actions",
                    modifier = Modifier.animateIcon(
                        checkedProgress = { checkedProgress },
                        size = ToggleFloatingActionButtonDefaults.iconSizeMedium(),
                    ),
                )
            }
        },
    ) {
        FloatingActionButtonMenuItem(
            modifier = Modifier.padding(bottom = 2.dp),
            onClick = onCreateCollectionClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CreateNewFolder,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            },
            text = { Text("Create collection", style = MaterialTheme.typography.titleMedium) },
        )

        FloatingActionButtonMenuItem(
            modifier = Modifier.padding(top = 2.dp),
            onClick = onSaveClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Link,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            },
            text = { Text("Save item", style = MaterialTheme.typography.titleMedium) },
        )
    }
}

@Composable
private fun LibraryPlaceholderScreen(
    title: String,
    subtitle: String,
    items: List<String>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(items) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(text = item, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Scaffolded as part of the initial project setup so we can replace placeholders feature by feature.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create collection") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Collection name") },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name.trim()) },
                enabled = name.isNotBlank(),
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
