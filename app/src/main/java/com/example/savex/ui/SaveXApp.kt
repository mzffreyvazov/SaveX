package com.example.savex.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
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
private const val PROFILE_AVATAR_URL = "https://api.dicebear.com/9.x/avataaars/png?seed=Felix&size=96"
private const val HOME_SEARCH_BAR_BOUNDS_KEY = "home_search_bar_bounds"
private const val HOME_SEARCH_LEADING_KEY = "home_search_leading"
private const val HOME_SEARCH_TRAILING_KEY = "home_search_trailing"
private const val TAB_SWITCH_ENTER_DURATION_MS = 180
private const val TAB_SWITCH_EXIT_DURATION_MS = 90
private const val TAB_SWITCH_ENTER_DELAY_MS = 35
private const val TAB_SWITCH_INITIAL_SCALE = 0.98f
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
private val topLevelRoutes = topLevelDestinations.mapTo(mutableSetOf()) { it.route }

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

private fun NavDestination.isTopLevelTabDestination(): Boolean =
    hierarchy.any { destination -> destination.route in topLevelRoutes }

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isTopLevelTabSwitch(): Boolean =
    initialState.destination.isTopLevelTabDestination() &&
        targetState.destination.isTopLevelTabDestination()

private fun tabSwitchEnterTransition(): EnterTransition =
    fadeIn(
        animationSpec = tween(
            durationMillis = TAB_SWITCH_ENTER_DURATION_MS,
            delayMillis = TAB_SWITCH_ENTER_DELAY_MS,
            easing = EmphasizedEasing,
        ),
    ) + scaleIn(
        initialScale = TAB_SWITCH_INITIAL_SCALE,
        animationSpec = tween(
            durationMillis = TAB_SWITCH_ENTER_DURATION_MS,
            delayMillis = TAB_SWITCH_ENTER_DELAY_MS,
            easing = EmphasizedEasing,
        ),
    )

private fun tabSwitchExitTransition(): ExitTransition =
    fadeOut(
        animationSpec = tween(
            durationMillis = TAB_SWITCH_EXIT_DURATION_MS,
            easing = FastOutSlowInEasing,
        ),
    ) + scaleOut(
        targetScale = TAB_SWITCH_INITIAL_SCALE,
        animationSpec = tween(
            durationMillis = TAB_SWITCH_EXIT_DURATION_MS,
            easing = FastOutSlowInEasing,
        ),
    )

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class,
)
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
    var showSaveSheet by rememberSaveable { mutableStateOf(false) }
    var saveSheetInitialSharedText by rememberSaveable { mutableStateOf<String?>(null) }
    var allowSaveSheetPartialExpand by rememberSaveable { mutableStateOf(false) }
    var isFabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var homeSearchQuery by rememberSaveable { mutableStateOf("") }
    var isHomeSearchExpanded by rememberSaveable { mutableStateOf(false) }
    val saveSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { nextValue ->
            nextValue != SheetValue.PartiallyExpanded || allowSaveSheetPartialExpand
        },
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    LaunchedEffect(currentDestination?.route) {
        isFabMenuExpanded = false
    }

    LaunchedEffect(showSaveSheet) {
        if (showSaveSheet) {
            isFabMenuExpanded = false
            saveSheetState.expand()
        }
    }

    LaunchedEffect(showSaveSheet, saveSheetState.currentValue) {
        if (showSaveSheet && saveSheetState.currentValue == SheetValue.Expanded) {
            allowSaveSheetPartialExpand = true
        }
    }

    LaunchedEffect(isHomeSearchExpanded) {
        if (isHomeSearchExpanded) {
            isFabMenuExpanded = false
        }
    }

    BackHandler(enabled = isFabMenuExpanded) {
        isFabMenuExpanded = false
    }

    LaunchedEffect(sharedText) {
        if (!sharedText.isNullOrBlank()) {
            saveSheetInitialSharedText = sharedText
            allowSaveSheetPartialExpand = false
            showSaveSheet = true
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
        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        SharedHomeTopBar(
                            query = homeSearchQuery,
                            expanded = isHomeSearchExpanded,
                            onQueryChange = { homeSearchQuery = it },
                            onExpandedChange = { isHomeSearchExpanded = it },
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onProfileClick = { },
                            sharedTransitionScope = this@SharedTransitionLayout,
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
                            enterTransition = {
                                if (isTopLevelTabSwitch()) {
                                    tabSwitchEnterTransition()
                                } else {
                                    EnterTransition.None
                                }
                            },
                            exitTransition = {
                                if (isTopLevelTabSwitch()) {
                                    tabSwitchExitTransition()
                                } else {
                                    ExitTransition.None
                                }
                            },
                            popEnterTransition = {
                                if (isTopLevelTabSwitch()) {
                                    tabSwitchEnterTransition()
                                } else {
                                    EnterTransition.None
                                }
                            },
                            popExitTransition = {
                                if (isTopLevelTabSwitch()) {
                                    tabSwitchExitTransition()
                                } else {
                                    ExitTransition.None
                                }
                            },
                        ) {
                            composable(ROUTE_HOME) {
                                HomeScreen(modifier = Modifier.fillMaxSize())
                            }
                            composable(ROUTE_STARRED) {
                                StarredScreen(modifier = Modifier.fillMaxSize())
                            }
                            composable(ROUTE_COLLECTIONS) {
                                CollectionsScreen(modifier = Modifier.fillMaxSize())
                            }
                            composable(ROUTE_ARCHIVED) {
                                ArchiveScreen(modifier = Modifier.fillMaxSize())
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

                        if (!isHomeSearchExpanded) {
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
                                    saveSheetInitialSharedText = null
                                    allowSaveSheetPartialExpand = false
                                    showSaveSheet = true
                                },
                            )
                        }
                    }
                }

                SharedHomeSearchOverlay(
                    query = homeSearchQuery,
                    expanded = isHomeSearchExpanded,
                    onQueryChange = { homeSearchQuery = it },
                    onExpandedChange = { isHomeSearchExpanded = it },
                    onProfileClick = { },
                    sharedTransitionScope = this@SharedTransitionLayout,
                )

                if (showSaveSheet) {
                    val sheetTopInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    ModalBottomSheet(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top = sheetTopInset),
                        onDismissRequest = {
                            showSaveSheet = false
                            saveSheetInitialSharedText = null
                            allowSaveSheetPartialExpand = false
                        },
                        sheetState = saveSheetState,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
                        dragHandle = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 3.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(34.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f)),
                                )
                            }
                        },
                    ) {
                        SaveScreen(
                            initialSharedText = saveSheetInitialSharedText,
                            onClose = {
                                showSaveSheet = false
                                saveSheetInitialSharedText = null
                                allowSaveSheetPartialExpand = false
                            },
                            onPrimaryActionClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Save flow is ready for wiring.")
                                }
                            },
                            onCreateCollection = { showCreateCollectionDialog = true },
                            onReviewScheduleClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Review schedule picker is ready for wiring.")
                                }
                            },
                        )
                    }
                }
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
) {
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
}

// Sealed state for the trailing icon slot — avoids stringly-typed branching
// and gives AnimatedContent a stable, equatable key to diff against.
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
private fun SharedHomeTopBar(
    query: String,
    expanded: Boolean,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onOpenDrawer: () -> Unit,
    onProfileClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                .height(SearchBarDefaults.InputFieldHeight + 16.dp),
        ) {
            AnimatedVisibility(
                visible = !expanded,
                enter = fadeIn(animationSpec = tween(durationMillis = 120)),
                exit = fadeOut(animationSpec = tween(durationMillis = 90)),
                modifier = Modifier.fillMaxSize(),
            ) {
                SharedHomeSearchBar(
                    query = query,
                    expanded = false,
                    onQueryChange = onQueryChange,
                    onExpandedChange = onExpandedChange,
                    onLeadingClick = onOpenDrawer,
                    onProfileClick = onProfileClick,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    barModifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedHomeSearchOverlay(
    query: String,
    expanded: Boolean,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onProfileClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    val searchSuggestions = remember(query) {
        homeSearchCatalog
            .filter { query.isBlank() || it.contains(query, ignoreCase = true) }
            .take(12)
    }

    LaunchedEffect(expanded) {
        if (expanded) {
            withFrameNanos { }
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
        }
    }

    BackHandler(enabled = expanded) {
        onExpandedChange(false)
    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(animationSpec = tween(durationMillis = 180)),
        exit = fadeOut(animationSpec = tween(durationMillis = 120)),
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { onExpandedChange(false) },
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                    .imePadding(),
            ) {
                SharedHomeSearchBar(
                    query = query,
                    expanded = true,
                    onQueryChange = onQueryChange,
                    onExpandedChange = onExpandedChange,
                    onLeadingClick = { onExpandedChange(false) },
                    onProfileClick = onProfileClick,
                    containerColor = MaterialTheme.colorScheme.surface,
                    barModifier = Modifier.fillMaxWidth(),
                    inputFieldModifier = Modifier.focusRequester(focusRequester),
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedVisibility,
                )

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(durationMillis = 180, delayMillis = 60)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 90)),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
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
                                            onExpandedChange(false)
                                        },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedHomeSearchBar(
    query: String,
    expanded: Boolean,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onLeadingClick: () -> Unit,
    onProfileClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color,
    barModifier: Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
    inputFieldModifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        Surface(
            color = containerColor,
            shape = SearchBarDefaults.inputFieldShape,
            modifier = barModifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = HOME_SEARCH_BAR_BOUNDS_KEY),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = fadeIn(animationSpec = tween(durationMillis = 120)),
                exit = fadeOut(animationSpec = tween(durationMillis = 90)),
                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
            ),
        ) {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = { },
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                modifier = inputFieldModifier.fillMaxWidth(),
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
                    SharedSearchLeadingIcon(
                        expanded = expanded,
                        onClick = onLeadingClick,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                },
                trailingIcon = {
                    SharedSearchTrailingIcon(
                        state = when {
                            expanded && query.isNotBlank() -> TrailingIconState.CLEAR
                            !expanded -> TrailingIconState.PROFILE
                            else -> TrailingIconState.NONE
                        },
                        onClear = { onQueryChange("") },
                        onProfileClick = onProfileClick,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedSearchLeadingIcon(
    expanded: Boolean,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = HOME_SEARCH_LEADING_KEY),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(animationSpec = tween(durationMillis = 120)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 90)),
                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                ),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = if (expanded) LeadingIconState.BACK else LeadingIconState.MENU,
                transitionSpec = {
                    fadeIn(animationSpec = tween(durationMillis = 140, easing = EmphasizedEasing)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing))
                },
                label = "leadingIcon",
            ) { state ->
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = if (state == LeadingIconState.BACK) {
                            Icons.AutoMirrored.Outlined.ArrowBack
                        } else {
                            Icons.Outlined.Menu
                        },
                        contentDescription = if (state == LeadingIconState.BACK) {
                            "Close search"
                        } else {
                            "Open navigation drawer"
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedSearchTrailingIcon(
    state: TrailingIconState,
    onClear: () -> Unit,
    onProfileClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: androidx.compose.animation.AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = HOME_SEARCH_TRAILING_KEY),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(animationSpec = tween(durationMillis = 120)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 90)),
                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
                ),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = state,
                transitionSpec = {
                    fadeIn(animationSpec = tween(durationMillis = 140, easing = EmphasizedEasing)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing))
                },
                label = "trailingIcon",
            ) { targetState ->
                when (targetState) {
                    TrailingIconState.CLEAR -> IconButton(onClick = onClear) {
                        Icon(Icons.Outlined.Close, contentDescription = "Clear search query")
                    }
                    TrailingIconState.PROFILE -> ProfileAvatarButton(onClick = onProfileClick)
                    TrailingIconState.NONE -> Box(modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatarButton(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = PROFILE_AVATAR_URL,
            contentDescription = "Profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private enum class LeadingIconState { MENU, BACK }
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
                containerColor = ToggleFloatingActionButtonDefaults.containerColor(
                    initialColor = MaterialTheme.colorScheme.primary,
                    finalColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(
                    imageVector = if (checkedProgress > 0.5f) Icons.Outlined.Close else Icons.Outlined.Add,
                    contentDescription = if (expanded) "Close actions" else "Open actions",
                    modifier = Modifier.animateIcon(
                        checkedProgress = { checkedProgress },
                        color = ToggleFloatingActionButtonDefaults.iconColor(
                            initialColor = MaterialTheme.colorScheme.onPrimary,
                            finalColor = MaterialTheme.colorScheme.onPrimary,
                        ),
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
    placeholderDescription: String = "Scaffolded as part of the initial project setup so we can replace placeholders feature by feature.",
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { heading() } // Accessibility
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
                shape = MaterialTheme.shapes.extraLarge, // or RoundedCornerShape(20.dp) if brand-specific
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                // border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Remove unless required
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium // M3 default for titleMedium
                    )
                    Text(
                        text = placeholderDescription,
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
