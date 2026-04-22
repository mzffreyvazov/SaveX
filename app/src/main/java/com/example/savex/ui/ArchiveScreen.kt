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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Unarchive
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
import androidx.compose.ui.draw.alpha
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

private val archiveCollectionCardShape = RoundedCornerShape(20.dp)
private val archiveItemCardShape = RoundedCornerShape(18.dp)
private val archiveThumbnailShape = RoundedCornerShape(14.dp)
private val archiveControlsIconShape = RoundedCornerShape(999.dp)
private val archiveListContentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 116.dp)
private val archiveAnimationEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

private enum class ArchiveViewMode {
    LIST,
    GRID,
}

private enum class ArchiveSortOption(val label: String) {
    NAME("Name"),
    DATE_MODIFIED("Date modified"),
    A_TO_Z("A to Z"),
    Z_TO_A("Z to A"),
}

private enum class ArchiveShowOption(val label: String) {
    ALL("All"),
    COLLECTIONS("Collections"),
    ITEMS("Items"),
}

private enum class ArchiveItemType {
    ARTICLE,
    VIDEO,
    DOCUMENT,
}

private data class ArchivedCollectionUi(
    val title: String,
    val itemCount: Int,
    val modifiedRank: Int,
)

private data class ArchivedItemUi(
    val title: String,
    val source: String,
    val tag: String,
    val archivedAt: String,
    val thumbnailUrl: String?,
    val thumbnailIcon: ImageVector,
    val itemType: ArchiveItemType,
    val modifiedRank: Int,
)

private data class ArchiveSheetTargetUi(
    val title: String,
    val icon: ImageVector,
)

private data class ArchiveActionUi(
    val title: String,
    val icon: ImageVector,
)

private sealed interface ArchiveBottomSheet {
    data object SortAndFilter : ArchiveBottomSheet
    data class CollectionActions(val target: ArchiveSheetTargetUi) : ArchiveBottomSheet
    data class ItemActions(val target: ArchiveSheetTargetUi) : ArchiveBottomSheet
}

private val archivedCollections = listOf(
    ArchivedCollectionUi(
        title = "Taxes 2022",
        itemCount = 8,
        modifiedRank = 0,
    ),
    ArchivedCollectionUi(
        title = "Old Portfolio",
        itemCount = 21,
        modifiedRank = 1,
    ),
)

private val archivedItems = listOf(
    ArchivedItemUi(
        title = "Android 10 Migration Guide",
        source = "developer.android.com",
        tag = "#legacy",
        archivedAt = "Oct 2021",
        thumbnailUrl = "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=400&h=400&fit=crop",
        thumbnailIcon = Icons.AutoMirrored.Outlined.Article,
        itemType = ArchiveItemType.ARTICLE,
        modifiedRank = 0,
    ),
    ArchivedItemUi(
        title = "Dead Startup Idea Notes",
        source = "docs.google.com",
        tag = "#ideas",
        archivedAt = "Jan 2023",
        thumbnailUrl = null,
        thumbnailIcon = Icons.Outlined.Description,
        itemType = ArchiveItemType.DOCUMENT,
        modifiedRank = 1,
    ),
)

private val archivedCollectionActionGroups = listOf(
    listOf(
        ArchiveActionUi("Restore collection", Icons.Outlined.Unarchive),
    ),
    listOf(
        ArchiveActionUi("Delete", Icons.Outlined.Delete),
    ),
)

private val archivedItemActionGroups = listOf(
    listOf(
        ArchiveActionUi("Restore item", Icons.Outlined.Unarchive),
    ),
    listOf(
        ArchiveActionUi("Delete", Icons.Outlined.Delete),
    ),
)

private fun List<ArchivedCollectionUi>.sortedCollectionsByOption(
    sortOption: ArchiveSortOption,
): List<ArchivedCollectionUi> = when (sortOption) {
    ArchiveSortOption.NAME,
    ArchiveSortOption.A_TO_Z -> sortedBy { it.title.lowercase() }
    ArchiveSortOption.DATE_MODIFIED -> sortedBy { it.modifiedRank }
    ArchiveSortOption.Z_TO_A -> sortedByDescending { it.title.lowercase() }
}

private fun List<ArchivedItemUi>.sortedItemsByOption(
    sortOption: ArchiveSortOption,
): List<ArchivedItemUi> = when (sortOption) {
    ArchiveSortOption.NAME,
    ArchiveSortOption.A_TO_Z -> sortedBy { it.title.lowercase() }
    ArchiveSortOption.DATE_MODIFIED -> sortedBy { it.modifiedRank }
    ArchiveSortOption.Z_TO_A -> sortedByDescending { it.title.lowercase() }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArchiveScreen(
    modifier: Modifier = Modifier,
) {
    var viewMode by rememberSaveable { mutableStateOf(ArchiveViewMode.GRID) }
    var sortOption by rememberSaveable { mutableStateOf(ArchiveSortOption.NAME) }
    var showOption by rememberSaveable { mutableStateOf(ArchiveShowOption.ALL) }
    var currentSheet by remember { mutableStateOf<ArchiveBottomSheet?>(null) }

    val visibleCollections = remember(sortOption) {
        archivedCollections.sortedCollectionsByOption(sortOption)
    }
    val visibleItems = remember(sortOption) {
        archivedItems.sortedItemsByOption(sortOption)
    }

    if (currentSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { currentSheet = null },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            when (val sheet = currentSheet) {
                ArchiveBottomSheet.SortAndFilter -> {
                    Column {
                        ArchiveSheetSectionTitle(text = "Sort by")
                        ArchiveSheetDivider()
                        ArchiveSortOption.entries.forEach { option ->
                            ArchiveSelectableSheetRow(
                                label = option.label,
                                selected = option == sortOption,
                                onClick = {
                                    sortOption = option
                                    currentSheet = null
                                },
                            )
                        }
                        ArchiveSheetDivider(modifier = Modifier.padding(vertical = 4.dp))
                        ArchiveSheetSectionTitle(text = "Show")
                        ArchiveShowOption.entries.forEach { option ->
                            ArchiveSelectableSheetRow(
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

                is ArchiveBottomSheet.CollectionActions -> {
                    ArchiveActionsSheet(
                        target = sheet.target,
                        actionGroups = archivedCollectionActionGroups,
                        onActionClick = { currentSheet = null },
                    )
                }

                is ArchiveBottomSheet.ItemActions -> {
                    ArchiveActionsSheet(
                        target = sheet.target,
                        actionGroups = archivedItemActionGroups,
                        onActionClick = { currentSheet = null },
                    )
                }

                null -> Unit
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = archiveListContentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ArchiveControlRow(
                sortLabel = sortOption.label,
                selectedViewMode = viewMode,
                onSortClick = { currentSheet = ArchiveBottomSheet.SortAndFilter },
                onViewModeChange = { viewMode = it },
            )
        }

        if (showOption != ArchiveShowOption.ITEMS) {
            item {
                ArchiveSectionTitle(text = "Collections")
            }
            item {
                ArchiveCollectionsPane(
                    viewMode = viewMode,
                    collections = visibleCollections,
                    onMoreClick = { collection ->
                        currentSheet = ArchiveBottomSheet.CollectionActions(
                            target = ArchiveSheetTargetUi(
                                title = collection.title,
                                icon = Icons.Outlined.Folder,
                            ),
                        )
                    },
                )
            }
        }

        if (showOption != ArchiveShowOption.COLLECTIONS) {
            item {
                ArchiveSectionTitle(text = "Items")
            }
            item {
                ArchiveItemsPane(
                    viewMode = viewMode,
                    items = visibleItems,
                    onMoreClick = { item ->
                        currentSheet = ArchiveBottomSheet.ItemActions(
                            target = ArchiveSheetTargetUi(
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
private fun ArchiveControlRow(
    sortLabel: String,
    selectedViewMode: ArchiveViewMode,
    onSortClick: () -> Unit,
    onViewModeChange: (ArchiveViewMode) -> Unit,
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
                shape = archiveControlsIconShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = "Open archive sorting options",
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
            val viewModes = listOf(ArchiveViewMode.LIST, ArchiveViewMode.GRID)

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
                        imageVector = if (mode == ArchiveViewMode.LIST) {
                            Icons.AutoMirrored.Outlined.ViewList
                        } else {
                            Icons.Outlined.GridView
                        },
                        contentDescription = if (mode == ArchiveViewMode.LIST) "Archive list view" else "Archive grid view",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchiveCollectionsPane(
    viewMode: ArchiveViewMode,
    collections: List<ArchivedCollectionUi>,
    onMoreClick: (ArchivedCollectionUi) -> Unit,
) {
    AnimatedContent(
        targetState = viewMode,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 180,
                    easing = archiveAnimationEasing,
                ),
            ) togetherWith fadeOut(
                animationSpec = tween(
                    durationMillis = 90,
                    easing = FastOutSlowInEasing,
                ),
            )
        },
        label = "archiveCollectionsViewMode",
    ) { mode ->
        if (collections.isEmpty()) {
            EmptyArchiveState(message = "No archived collections match this filter.")
        } else if (mode == ArchiveViewMode.GRID) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                collections.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowItems.forEach { collection ->
                            ArchiveCollectionGridCard(
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
                    ArchiveCollectionListCard(
                        collection = collection,
                        onMoreClick = { onMoreClick(collection) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchiveItemsPane(
    viewMode: ArchiveViewMode,
    items: List<ArchivedItemUi>,
    onMoreClick: (ArchivedItemUi) -> Unit,
) {
    AnimatedContent(
        targetState = viewMode,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 180,
                    easing = archiveAnimationEasing,
                ),
            ) togetherWith fadeOut(
                animationSpec = tween(
                    durationMillis = 90,
                    easing = FastOutSlowInEasing,
                ),
            )
        },
        label = "archiveItemsViewMode",
    ) { mode ->
        if (items.isEmpty()) {
            EmptyArchiveState(message = "No archived items match this filter.")
        } else if (mode == ArchiveViewMode.GRID) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowItems.forEach { item ->
                            ArchiveItemGridCard(
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
                    ArchiveItemListCard(
                        item = item,
                        onMoreClick = { onMoreClick(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchiveCollectionGridCard(
    collection: ArchivedCollectionUi,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = modifier.alpha(0.9f),
        shape = archiveCollectionCardShape,
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
                ArchiveCollectionLeadingIcon(size = 34.dp)

                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Open archived collection actions",
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
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
private fun ArchiveCollectionListCard(
    collection: ArchivedCollectionUi,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.9f),
        shape = archiveCollectionCardShape,
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
            ArchiveCollectionLeadingIcon(size = 32.dp)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
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
                    contentDescription = "Open archived collection actions",
                )
            }
        }
    }
}

@Composable
private fun ArchiveCollectionLeadingIcon(
    size: androidx.compose.ui.unit.Dp,
) {
    Box {
        Icon(
            imageVector = Icons.Outlined.Folder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
            modifier = Modifier.size(size),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(999.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(2.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Archive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun ArchiveItemListCard(
    item: ArchivedItemUi,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.9f),
        shape = archiveItemCardShape,
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
            ArchiveThumbnail(
                title = item.title,
                thumbnailUrl = item.thumbnailUrl,
                thumbnailIcon = item.thumbnailIcon,
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
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = item.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    IconButton(onClick = onMoreClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Open archived item actions")
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
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                            modifier = Modifier.size(16.dp),
                        )
                        ArchiveMetaChip(text = item.tag)
                    }

                    Text(
                        text = item.archivedAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchiveItemGridCard(
    item: ArchivedItemUi,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = modifier.alpha(0.9f),
        shape = archiveItemCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            ArchiveThumbnail(
                title = item.title,
                thumbnailUrl = item.thumbnailUrl,
                thumbnailIcon = item.thumbnailIcon,
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
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = item.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    IconButton(onClick = onMoreClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Open archived item actions")
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
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                            modifier = Modifier.size(16.dp),
                        )
                        ArchiveMetaChip(text = item.tag)
                    }

                    Text(
                        text = item.archivedAt,
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
private fun ArchiveThumbnail(
    title: String,
    thumbnailUrl: String?,
    thumbnailIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(archiveThumbnailShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.72f),
            )
        } else {
            Icon(
                imageVector = thumbnailIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
private fun ArchiveMetaChip(
    text: String,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun ArchiveSectionTitle(
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
private fun EmptyArchiveState(
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
private fun ArchiveActionsSheet(
    target: ArchiveSheetTargetUi,
    actionGroups: List<List<ArchiveActionUi>>,
    onActionClick: () -> Unit,
) {
    Column {
        ArchiveSheetHeader(
            title = target.title,
            icon = target.icon,
        )
        ArchiveSheetDivider()

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
                ArchiveSheetDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ArchiveSheetHeader(
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
private fun ArchiveSheetSectionTitle(
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
private fun ArchiveSheetDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier.padding(start = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
    )
}

@Composable
private fun ArchiveSelectableSheetRow(
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
