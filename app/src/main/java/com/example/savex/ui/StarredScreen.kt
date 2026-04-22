package com.example.savex.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.savex.ui.theme.PocketCoral

private val starredCollectionCardShape = RoundedCornerShape(20.dp)
private val starredItemCardShape = RoundedCornerShape(18.dp)
private val starredThumbnailShape = RoundedCornerShape(14.dp)
private val starredControlsIconShape = RoundedCornerShape(999.dp)
private val starredListContentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 116.dp)
private val StarredViewAnimationEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

private enum class StarredViewMode {
    LIST,
    GRID,
}

private enum class StarredSortOption(val label: String) {
    NAME("Name"),
    DATE_MODIFIED("Date modified"),
    A_TO_Z("A to Z"),
    Z_TO_A("Z to A"),
}

private enum class StarredShowOption(val label: String) {
    ALL("All"),
    COLLECTIONS("Collections"),
    ITEMS("Items"),
}

private enum class StarredItemType {
    ARTICLE,
    VIDEO,
    DOCUMENT,
}

private data class StarredCollectionUi(
    val title: String,
    val itemCount: Int,
    val modifiedRank: Int,
)

private data class StarredItemUi(
    val title: String,
    val source: String,
    val tag: String,
    val savedAt: String,
    val thumbnailUrl: String?,
    val thumbnailIcon: ImageVector,
    val itemType: StarredItemType,
    val modifiedRank: Int,
)

private data class StarredSheetTargetUi(
    val title: String,
    val icon: ImageVector,
)

private data class StarredActionUi(
    val title: String,
    val icon: ImageVector,
)

private sealed interface StarredBottomSheet {
    data object SortAndFilter : StarredBottomSheet
    data class CollectionActions(val target: StarredSheetTargetUi) : StarredBottomSheet
    data class ItemActions(val target: StarredSheetTargetUi) : StarredBottomSheet
}

private val starredCollections = listOf(
    StarredCollectionUi(
        title = "Dev Resources",
        itemCount = 14,
        modifiedRank = 1,
    ),
    StarredCollectionUi(
        title = "UI Inspiration",
        itemCount = 42,
        modifiedRank = 0,
    ),
    StarredCollectionUi(
        title = "Systems Design",
        itemCount = 9,
        modifiedRank = 2,
    ),
)

private val starredItems = listOf(
    StarredItemUi(
        title = "Architecture of a Modern Android App",
        source = "medium.com",
        tag = "#android",
        savedAt = "May 12",
        thumbnailUrl = "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=400&h=400&fit=crop",
        thumbnailIcon = Icons.AutoMirrored.Outlined.Article,
        itemType = StarredItemType.ARTICLE,
        modifiedRank = 0,
    ),
    StarredItemUi(
        title = "PostgreSQL JSONB Tutorial",
        source = "postgresqltutorial.com",
        tag = "#database",
        savedAt = "Apr 3",
        thumbnailUrl = null,
        thumbnailIcon = Icons.Outlined.Description,
        itemType = StarredItemType.DOCUMENT,
        modifiedRank = 1,
    ),
    StarredItemUi(
        title = "Spring Boot 3 + JWT Auth",
        source = "youtube.com",
        tag = "#backend",
        savedAt = "Mar 20",
        thumbnailUrl = null,
        thumbnailIcon = Icons.Outlined.VideoLibrary,
        itemType = StarredItemType.VIDEO,
        modifiedRank = 2,
    ),
)

private val starredCollectionActionGroups = listOf(
    listOf(
        StarredActionUi("Star collection", Icons.Outlined.StarOutline),
        StarredActionUi("Rename", Icons.Outlined.Edit),
        StarredActionUi("Review schedule", Icons.Outlined.Schedule),
    ),
    listOf(
        StarredActionUi("Archive", Icons.Outlined.Archive),
        StarredActionUi("Delete", Icons.Outlined.Delete),
    ),
)

private val starredItemActionGroups = listOf(
    listOf(
        StarredActionUi("Remove from starred", Icons.Outlined.StarOutline),
        StarredActionUi("Add to collection", Icons.Outlined.CreateNewFolder),
    ),
    listOf(
        StarredActionUi("Copy link", Icons.Outlined.Link),
        StarredActionUi("Share", Icons.Outlined.Share),
    ),
    listOf(
        StarredActionUi("Archive", Icons.Outlined.Archive),
        StarredActionUi("Snooze", Icons.Outlined.Bedtime),
        StarredActionUi("Delete", Icons.Outlined.Delete),
    ),
)

private fun List<StarredCollectionUi>.sortedCollectionsByOption(
    sortOption: StarredSortOption,
): List<StarredCollectionUi> = when (sortOption) {
    StarredSortOption.NAME,
    StarredSortOption.A_TO_Z -> sortedBy { it.title.lowercase() }
    StarredSortOption.DATE_MODIFIED -> sortedBy { it.modifiedRank }
    StarredSortOption.Z_TO_A -> sortedByDescending { it.title.lowercase() }
}

private fun List<StarredItemUi>.sortedItemsByOption(
    sortOption: StarredSortOption,
): List<StarredItemUi> = when (sortOption) {
    StarredSortOption.NAME,
    StarredSortOption.A_TO_Z -> sortedBy { it.title.lowercase() }
    StarredSortOption.DATE_MODIFIED -> sortedBy { it.modifiedRank }
    StarredSortOption.Z_TO_A -> sortedByDescending { it.title.lowercase() }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StarredScreen(
    modifier: Modifier = Modifier,
) {
    var viewMode by rememberSaveable { mutableStateOf(StarredViewMode.GRID) }
    var sortOption by rememberSaveable { mutableStateOf(StarredSortOption.NAME) }
    var showOption by rememberSaveable { mutableStateOf(StarredShowOption.ALL) }
    var currentSheet by remember { mutableStateOf<StarredBottomSheet?>(null) }

    val visibleCollections = remember(sortOption) {
        starredCollections.sortedCollectionsByOption(sortOption)
    }
    val visibleItems = remember(sortOption) {
        starredItems.sortedItemsByOption(sortOption)
    }

    if (currentSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { currentSheet = null },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            when (val sheet = currentSheet) {
                StarredBottomSheet.SortAndFilter -> {
                    Column {
                        StarredSheetSectionTitle(text = "Sort by")
                        StarredSheetDivider()
                        StarredSortOption.entries.forEach { option ->
                            StarredSelectableSheetRow(
                                label = option.label,
                                selected = option == sortOption,
                                onClick = {
                                    sortOption = option
                                    currentSheet = null
                                },
                            )
                        }
                        StarredSheetDivider(modifier = Modifier.padding(vertical = 4.dp))
                        StarredSheetSectionTitle(text = "Show")
                        StarredShowOption.entries.forEach { option ->
                            StarredSelectableSheetRow(
                                label = option.label,
                                selected = option == showOption,
                                onClick = {
                                    showOption = option
                                    currentSheet = null
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                is StarredBottomSheet.CollectionActions -> {
                    StarredActionsSheet(
                        target = sheet.target,
                        actionGroups = starredCollectionActionGroups,
                        onActionClick = { currentSheet = null },
                    )
                }

                is StarredBottomSheet.ItemActions -> {
                    StarredActionsSheet(
                        target = sheet.target,
                        actionGroups = starredItemActionGroups,
                        onActionClick = { currentSheet = null },
                    )
                }

                null -> Unit
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = starredListContentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            StarredControlRow(
                sortLabel = sortOption.label,
                selectedViewMode = viewMode,
                onSortClick = { currentSheet = StarredBottomSheet.SortAndFilter },
                onViewModeChange = { viewMode = it },
            )
        }

        if (showOption != StarredShowOption.ITEMS) {
            item {
                StarredSectionTitle(text = "Collections")
            }
            item {
                StarredCollectionsPane(
                    viewMode = viewMode,
                    collections = visibleCollections,
                    onMoreClick = { collection ->
                        currentSheet = StarredBottomSheet.CollectionActions(
                            target = StarredSheetTargetUi(
                                title = collection.title,
                                icon = Icons.Outlined.Folder,
                            ),
                        )
                    },
                )
            }
        }

        if (showOption != StarredShowOption.COLLECTIONS) {
            item {
                StarredSectionTitle(text = "Items")
            }
            item {
                StarredItemsPane(
                    viewMode = viewMode,
                    items = visibleItems,
                    onMoreClick = { item ->
                        currentSheet = StarredBottomSheet.ItemActions(
                            target = StarredSheetTargetUi(
                                title = item.title,
                                icon = item.thumbnailIcon,
                            ),
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StarredControlRow(
    sortLabel: String,
    selectedViewMode: StarredViewMode,
    onSortClick: () -> Unit,
    onViewModeChange: (StarredViewMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .clickable(onClick = onSortClick)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = sortLabel,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Surface(
                shape = starredControlsIconShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = "Open starred sorting options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val viewModes = listOf(StarredViewMode.LIST, StarredViewMode.GRID)

            viewModes.forEachIndexed { index, mode ->
                ToggleButton(
                    checked = selectedViewMode == mode,
                    onCheckedChange = { onViewModeChange(mode) },
                    modifier = Modifier
                        .widthIn(min = 48.dp)
                        .height(40.dp)
                        .semantics { role = Role.RadioButton },
                    colors = ToggleButtonDefaults.toggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkedContainerColor = MaterialTheme.colorScheme.primary,
                        checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    shapes = when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        viewModes.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                ) {
                    Icon(
                        imageVector = if (mode == StarredViewMode.LIST) {
                            Icons.AutoMirrored.Outlined.ViewList
                        } else {
                            Icons.Outlined.GridView
                        },
                        contentDescription = if (mode == StarredViewMode.LIST) "Collections list view" else "Collections grid view",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StarredCollectionsPane(
    viewMode: StarredViewMode,
    collections: List<StarredCollectionUi>,
    onMoreClick: (StarredCollectionUi) -> Unit,
) {
    AnimatedContent(
        targetState = viewMode,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 180,
                    easing = StarredViewAnimationEasing,
                ),
            ) togetherWith fadeOut(
                animationSpec = tween(
                    durationMillis = 90,
                    easing = FastOutSlowInEasing,
                ),
            )
        },
        label = "starredCollectionsViewMode",
    ) { mode ->
        if (collections.isEmpty()) {
            EmptyStarredState(message = "No starred collections match this filter.")
        } else if (mode == StarredViewMode.GRID) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                collections.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowItems.forEach { collection ->
                            StarredCollectionGridCard(
                                collection = collection,
                                modifier = Modifier.weight(1f),
                                onMoreClick = { onMoreClick(collection) },
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                collections.forEach { collection ->
                    StarredCollectionListCard(
                        collection = collection,
                        onMoreClick = { onMoreClick(collection) },
                    )
                }
            }
        }
    }
}

@Composable
private fun StarredItemsPane(
    viewMode: StarredViewMode,
    items: List<StarredItemUi>,
    onMoreClick: (StarredItemUi) -> Unit,
) {
    AnimatedContent(
        targetState = viewMode,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 180,
                    easing = StarredViewAnimationEasing,
                ),
            ) togetherWith fadeOut(
                animationSpec = tween(
                    durationMillis = 90,
                    easing = FastOutSlowInEasing,
                ),
            )
        },
        label = "starredItemsViewMode",
    ) { mode ->
        if (items.isEmpty()) {
            EmptyStarredState(message = "No starred items match this filter.")
        } else if (mode == StarredViewMode.GRID) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowItems.forEach { item ->
                            StarredItemGridCard(
                                item = item,
                                modifier = Modifier.weight(1f),
                                onMoreClick = { onMoreClick(item) },
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.forEach { item ->
                    StarredItemListCard(
                        item = item,
                        onMoreClick = { onMoreClick(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun StarredCollectionGridCard(
    collection: StarredCollectionUi,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = starredCollectionCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Outlined.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(34.dp),
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clip(RoundedCornerShape(999.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(2.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }

                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Open collection actions",
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${collection.itemCount} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StarredCollectionListCard(
    collection: StarredCollectionUi,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = starredCollectionCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(2.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${collection.itemCount} items",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(
                onClick = onMoreClick,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Open collection actions",
                )
            }
        }
    }
}

@Composable
private fun StarredItemListCard(
    item: StarredItemUi,
    onMoreClick: () -> Unit,
) {
    val thumbnailContainerColor = when {
        item.thumbnailUrl != null -> MaterialTheme.colorScheme.surfaceVariant
        item.itemType == StarredItemType.VIDEO -> PocketCoral.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val thumbnailIconTint = if (item.itemType == StarredItemType.VIDEO) {
        PocketCoral
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = starredItemCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StarredThumbnail(
                title = item.title,
                thumbnailUrl = item.thumbnailUrl,
                thumbnailIcon = item.thumbnailIcon,
                containerColor = thumbnailContainerColor,
                iconTint = thumbnailIconTint,
                modifier = Modifier.size(64.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = item.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    IconButton(onClick = onMoreClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Open item actions")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp),
                        )
                        StarredMetaChip(text = item.tag)
                    }

                    Text(
                        text = item.savedAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun StarredItemGridCard(
    item: StarredItemUi,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit,
) {
    val thumbnailContainerColor = when {
        item.thumbnailUrl != null -> MaterialTheme.colorScheme.surfaceVariant
        item.itemType == StarredItemType.VIDEO -> PocketCoral.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val thumbnailIconTint = if (item.itemType == StarredItemType.VIDEO) {
        PocketCoral
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier,
        shape = starredItemCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            StarredThumbnail(
                title = item.title,
                thumbnailUrl = item.thumbnailUrl,
                thumbnailIcon = item.thumbnailIcon,
                containerColor = thumbnailContainerColor,
                iconTint = thumbnailIconTint,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.18f),
            )

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = item.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    IconButton(onClick = onMoreClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Open item actions")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp),
                        )
                        StarredMetaChip(text = item.tag)
                    }

                    Text(
                        text = item.savedAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun StarredThumbnail(
    title: String,
    thumbnailUrl: String?,
    thumbnailIcon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(starredThumbnailShape)
            .background(containerColor),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = thumbnailIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
private fun StarredMetaChip(
    text: String,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun StarredSectionTitle(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

@Composable
private fun EmptyStarredState(
    message: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
        )
    }
}

@Composable
private fun StarredActionsSheet(
    target: StarredSheetTargetUi,
    actionGroups: List<List<StarredActionUi>>,
    onActionClick: () -> Unit,
) {
    Column {
        StarredSheetHeader(
            title = target.title,
            icon = target.icon,
        )
        StarredSheetDivider()

        actionGroups.forEachIndexed { groupIndex, group ->
            group.forEach { action ->
                val isDeleteAction = action.title == "Delete"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onActionClick)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        tint = if (isDeleteAction) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isDeleteAction) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }

            if (groupIndex < actionGroups.lastIndex) {
                StarredSheetDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StarredSheetHeader(
    title: String,
    icon: ImageVector,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StarredSheetSectionTitle(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )
}

@Composable
private fun StarredSheetDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier.padding(start = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
    )
}

@Composable
private fun StarredSelectableSheetRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                } else {
                    Color.Transparent
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            tint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Transparent
            },
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}
