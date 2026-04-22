package com.example.savex.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.InsertChartOutlined
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Menu
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
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberSearchBarState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

private const val ROUTE_HOME = "home"
private const val ROUTE_STARRED = "starred"
private const val ROUTE_COLLECTIONS = "collections"
private const val ROUTE_ARCHIVED = "archived"
private const val ROUTE_SAVE = "save"
private const val PROFILE_AVATAR_URL = "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix"
private val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

private data class TopLevelDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

private data class DrawerDestination(
    val title: String,
    val icon: ImageVector,
)

private val topLevelDestinations = listOf(
    TopLevelDestination(ROUTE_HOME, "Home", Icons.Outlined.Home, Icons.Filled.Home),
    TopLevelDestination(ROUTE_STARRED, "Starred", Icons.Outlined.StarOutline, Icons.Filled.Star),
    TopLevelDestination(ROUTE_COLLECTIONS, "Collections", Icons.Outlined.Folder, Icons.Filled.Folder),
    TopLevelDestination(ROUTE_ARCHIVED, "Archived", Icons.Outlined.Archive, Icons.Filled.Archive),
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
    val scope = rememberCoroutineScope()

    var showCreateCollectionDialog by rememberSaveable { mutableStateOf(false) }
    var isFabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var homeSearchQuery by rememberSaveable { mutableStateOf("") }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isHomeDestination = currentDestination?.route == ROUTE_HOME
    val currentTitle = topLevelDestinations.firstOrNull { it.route == currentDestination?.route }?.title
        ?: if (currentDestination?.route == ROUTE_SAVE) "Save item" else "SaveX"

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
                    containerColor = MaterialTheme.colorScheme.surface,
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
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            ),
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.icon,
                                    contentDescription = destination.title,
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    ),
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
                            .clickable(indication = null, interactionSource = null) {
                                isFabMenuExpanded = false
                            },
                    )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenDrawer: () -> Unit,
    onProfileClick: () -> Unit,
    showSearchBar: Boolean,
    title: String,
) {
    // FIX: The searchBarState is always created, regardless of showSearchBar.
    // Previously the entire composable was conditionally swapped (SearchBar vs
    // CenterAlignedTopAppBar), which caused the leading icon to hard-jump between
    // two separate layout trees with no shared position. By keeping a single
    // searchBarState alive we let the M3 SearchBar animate within one stable slot.
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState(
        initialValue = SearchBarValue.Collapsed,
        // Slightly longer expand so the container visually "catches up" with the
        // icon crossfade — 350ms gives EmphasizedEasing enough range to feel fluid
        // without being sluggish.
        animationSpecForExpand = tween(durationMillis = 350, easing = EmphasizedEasing),
        // Collapse is intentionally faster (200ms) so dismissing the search feels
        // snappy and responsive rather than lingering.
        animationSpecForCollapse = tween(durationMillis = 200, easing = FastOutSlowInEasing),
    )

    val isSearchExpanded =
        searchBarState.currentValue == SearchBarValue.Expanded ||
                searchBarState.targetValue == SearchBarValue.Expanded

    // Crossover at 35% (not 50%) so the icon starts fading while the container is
    // still visibly growing — the two motions overlap and feel like a single gesture.
    // At 50% the container is already most of the way open, making the icon feel late.
    // 0.03f: low enough to catch the very start of a collapse (so BackHandler stays
    // active) but high enough to ignore spurious sub-pixel drift at rest.
    val isSearchTransitionActive =
        searchBarState.targetValue == SearchBarValue.Expanded || searchBarState.progress > 0.03f

    val searchSuggestions = remember(query) {
        homeSearchCatalog
            .filter { query.isBlank() || it.contains(query, ignoreCase = true) }
            .take(12)
    }

    fun setSearchExpanded(expanded: Boolean) {
        scope.launch {
            if (expanded) searchBarState.animateToExpanded()
            else searchBarState.animateToCollapsed()
        }
    }

    BackHandler(enabled = isSearchTransitionActive) {
        setSearchExpanded(false)
    }

    // One shared inputField lambda used by both SearchBar and
    // ExpandedFullScreenContainedSearchBar keeps the field layout stable while the
    // identity the leading icon is composed in a single, stable layout node — the
    // state so the menu and profile reappear in sync after collapse.
    val inputField: @Composable () -> Unit = {
        SearchBarDefaults.InputField(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { },
            expanded = isSearchExpanded,
            onExpandedChange = ::setSearchExpanded,
            placeholder = { Text("Search") },
            colors = SearchBarDefaults.inputFieldColors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            leadingIcon = {
                IconButton(
                    onClick = {
                        if (isSearchTransitionActive) setSearchExpanded(false)
                        else onOpenDrawer()
                    },
                ) {
                    // Crossfade kicks in at 35% progress and runs for 120ms.
                    // With a 350ms expand, 35% ≈ t=122ms, so the fade spans
                    // roughly t=122ms→242ms — the middle third of the expansion.
                    // This makes the icon swap feel baked into the opening motion
                    // rather than happening before it (too early) or after (too late).
                    // LinearEasing keeps the opacity ramp perfectly neutral so neither
                    // icon "pops" while the other is still visible.
                    Icon(
                        imageVector = if (isSearchExpanded) {
                            Icons.AutoMirrored.Outlined.ArrowBack
                        } else {
                            Icons.Outlined.Menu
                        },
                        contentDescription = if (isSearchExpanded) {
                            "Close search"
                        } else {
                            "Open navigation drawer"
                        },
                    )
                }
            },
            trailingIcon = {
                Box(
                    modifier = Modifier.width(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    // FIX: AnimatedContent replaces the raw if/else so the trailing
                    // icon fades between states rather than hard-swapping.
                    AnimatedContent(
                        targetState = when {
                            isSearchExpanded && query.isNotBlank() -> TrailingIconState.CLEAR
                            !isSearchExpanded -> TrailingIconState.PROFILE
                            else -> TrailingIconState.NONE
                        },
                        label = "trailingIcon",
                    ) { state ->
                        when (state) {
                            TrailingIconState.CLEAR -> IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Clear search query")
                            }
                            TrailingIconState.PROFILE -> ProfileAvatarButton(onClick = onProfileClick)
                            TrailingIconState.NONE -> Box(modifier = Modifier.size(48.dp))
                        }
                    }
                }
            },
        )
    }

    // FIX: The top bar is now a single always-present Surface that contains the
    // SearchBar when on the home screen, or a plain title row otherwise.
    // We no longer swap between two completely different composables
    // (SearchBar vs CenterAlignedTopAppBar), so the layout tree is stable across
    // the nav transition and there is no positional jump.
    if (showSearchBar) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                SearchBar(
                    state = searchBarState,
                    inputField = inputField,
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        dividerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0f),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        ExpandedFullScreenContainedSearchBar(
            state = searchBarState,
            inputField = inputField,
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                dividerColor = MaterialTheme.colorScheme.outline,
            ),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                item {
                    Text(
                        text = if (query.isBlank()) "Recent searches" else "Matching results",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }

                if (searchSuggestions.isEmpty()) {
                    item {
                        Text(
                            text = "No results for \"$query\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                } else {
                    items(searchSuggestions, key = { it }) { suggestion ->
                        ListItem(
                            headlineContent = { Text(suggestion) },
                            leadingContent = {
                                Icon(
                                    Icons.Outlined.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onQueryChange(suggestion)
                                    setSearchExpanded(false)
                                },
                        )
                    }
                }
            }
        }
    } else {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            navigationIcon = {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Outlined.Menu, contentDescription = "Open navigation drawer")
                }
            },
            title = { Text(title) },
            actions = {
                ProfileAvatarButton(onClick = onProfileClick)
            },
        )
    }
}

// Sealed state for the trailing icon slot — avoids stringly-typed branching
// and gives AnimatedContent a stable, equatable key to diff against.
@Composable
private fun ProfileAvatarButton(
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(42.dp),
    ) {
        AsyncImage(
            model = PROFILE_AVATAR_URL,
            contentDescription = "Profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = CircleShape,
                ),
        )
    }
}

private enum class TrailingIconState { CLEAR, PROFILE, NONE }


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
