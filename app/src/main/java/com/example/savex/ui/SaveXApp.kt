package com.example.savex.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.InsertChartOutlined
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveXApp(
    sharedText: String?,
    onSharedTextConsumed: () -> Unit,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = androidx.compose.material3.rememberDrawerState(initialValue = DrawerValue.Closed)
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

    LaunchedEffect(currentDestination?.route) {
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
                if (isHomeDestination) {
                    HomeTopBar(
                        query = homeSearchQuery,
                        onQueryChange = { homeSearchQuery = it },
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onProfileClick = { },
                    )
                } else {
                    TopAppBar(
                        title = { Text(currentTitle) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Outlined.Menu, contentDescription = "Open navigation drawer")
                            }
                        },
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(Icons.Outlined.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = { }) {
                                Icon(Icons.Outlined.PersonOutline, contentDescription = "Profile")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.height(72.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    topLevelDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                isFabMenuExpanded = false
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                            icon = { Icon(destination.icon, contentDescription = destination.title) },
                            label = {
                                Text(
                                    text = destination.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                )
                            },
                        )
                    }
                }
            },
            floatingActionButton = {
                HomeFabMenu(
                    expanded = isFabMenuExpanded,
                    onToggleExpanded = { isFabMenuExpanded = !isFabMenuExpanded },
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
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
                            .clickable(
                                interactionSource = overlayInteractionSource,
                                indication = null,
                            ) { isFabMenuExpanded = false },
                    )
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

@Composable
private fun HomeTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenDrawer: () -> Unit,
    onProfileClick: () -> Unit,
) {
    var isSearchFocused by remember { mutableStateOf(false) }
    val searchContainerColor by animateColorAsState(
        targetValue = if (isSearchFocused) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        label = "searchContainerColor",
    )
    val searchBorderColor by animateColorAsState(
        targetValue = if (isSearchFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        label = "searchBorderColor",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onOpenDrawer, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Outlined.Menu, contentDescription = "Open navigation drawer")
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = CircleShape,
            color = searchContainerColor,
            border = BorderStroke(1.dp, searchBorderColor),
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.merge(
                    TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    ),
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .onFocusChanged { isSearchFocused = it.isFocused }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (query.isBlank()) {
                            Text(
                                text = "Search",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }

        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun HomeFabMenu(
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onCreateCollectionClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val iconRotation by animateFloatAsState(
        targetValue = if (expanded) 135f else 0f,
        label = "fabIconRotation",
    )

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FabMenuActionButton(
                    title = "Collection",
                    icon = Icons.Outlined.CreateNewFolder,
                    onClick = onCreateCollectionClick,
                )
                FabMenuActionButton(
                    title = "Save",
                    icon = Icons.Outlined.Link,
                    onClick = onSaveClick,
                )
            }
        }

        FloatingActionButton(
            onClick = onToggleExpanded,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = if (expanded) CircleShape else RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Create",
                modifier = Modifier.rotate(iconRotation),
            )
        }
    }
}

@Composable
private fun FabMenuActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .widthIn(min = 144.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        tonalElevation = 0.dp,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = title, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun LibraryPlaceholderScreen(
    title: String,
    subtitle: String,
    items: List<String>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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
