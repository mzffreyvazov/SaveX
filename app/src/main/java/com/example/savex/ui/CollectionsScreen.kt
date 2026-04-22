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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.StarOutline
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val collectionsCardShape = RoundedCornerShape(20.dp)
private val collectionsControlIconShape = RoundedCornerShape(999.dp)
private val collectionsContentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 116.dp)
private val collectionsAnimationEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

private enum class CollectionsViewMode {
    LIST,
    GRID,
}

private enum class CollectionsSortOption(val label: String) {
    NAME("Name"),
    DATE_MODIFIED("Date modified"),
    A_TO_Z("A to Z"),
    Z_TO_A("Z to A"),
}

private data class CollectionUi(
    val title: String,
    val itemCount: Int,
    val modifiedRank: Int,
    val isStarred: Boolean,
)

private data class CollectionActionUi(
    val title: String,
    val icon: ImageVector,
)

private sealed interface CollectionsBottomSheet {
    data object Sort : CollectionsBottomSheet
    data class Actions(val target: CollectionUi) : CollectionsBottomSheet
}

private val collectionsData = listOf(
    CollectionUi(
        title = "Books to Read",
        itemCount = 3,
        modifiedRank = 1,
        isStarred = false,
    ),
    CollectionUi(
        title = "Dev Resources",
        itemCount = 14,
        modifiedRank = 0,
        isStarred = true,
    ),
    CollectionUi(
        title = "Recipes",
        itemCount = 8,
        modifiedRank = 2,
        isStarred = false,
    ),
    CollectionUi(
        title = "Taxes 2024",
        itemCount = 1,
        modifiedRank = 4,
        isStarred = false,
    ),
    CollectionUi(
        title = "UI Inspiration",
        itemCount = 42,
        modifiedRank = 3,
        isStarred = true,
    ),
)

private fun List<CollectionUi>.sortedByCollectionOption(
    sortOption: CollectionsSortOption,
): List<CollectionUi> = when (sortOption) {
    CollectionsSortOption.NAME,
    CollectionsSortOption.A_TO_Z -> sortedBy { it.title.lowercase() }
    CollectionsSortOption.DATE_MODIFIED -> sortedBy { it.modifiedRank }
    CollectionsSortOption.Z_TO_A -> sortedByDescending { it.title.lowercase() }
}

private fun collectionActionsFor(collection: CollectionUi): List<List<CollectionActionUi>> = listOf(
    listOf(
        CollectionActionUi(
            title = if (collection.isStarred) "Remove from starred" else "Star collection",
            icon = if (collection.isStarred) Icons.Filled.Star else Icons.Outlined.StarOutline,
        ),
        CollectionActionUi("Rename", Icons.Outlined.Edit),
        CollectionActionUi("Review schedule", Icons.Outlined.Schedule),
    ),
    listOf(
        CollectionActionUi("Archive", Icons.Outlined.Archive),
        CollectionActionUi("Delete", Icons.Outlined.Delete),
    ),
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CollectionsScreen(
    modifier: Modifier = Modifier,
) {
    var viewMode by rememberSaveable { mutableStateOf(CollectionsViewMode.GRID) }
    var sortOption by rememberSaveable { mutableStateOf(CollectionsSortOption.NAME) }
    var currentSheet by remember { mutableStateOf<CollectionsBottomSheet?>(null) }

    val collections = remember(sortOption) {
        collectionsData.sortedByCollectionOption(sortOption)
    }

    if (currentSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { currentSheet = null },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            when (val sheet = currentSheet) {
                CollectionsBottomSheet.Sort -> {
                    Column {
                        CollectionsSheetSectionTitle(text = "Sort by")
                        CollectionsSheetDivider()
                        CollectionsSortOption.entries.forEach { option ->
                            CollectionsSelectableSheetRow(
                                label = option.label,
                                selected = option == sortOption,
                                onClick = {
                                    sortOption = option
                                    currentSheet = null
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                is CollectionsBottomSheet.Actions -> {
                    CollectionsActionsSheet(
                        target = sheet.target,
                        actionGroups = collectionActionsFor(sheet.target),
                        onActionClick = { currentSheet = null },
                    )
                }

                null -> Unit
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = collectionsContentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            CollectionsControlRow(
                sortLabel = sortOption.label,
                selectedViewMode = viewMode,
                onSortClick = { currentSheet = CollectionsBottomSheet.Sort },
                onViewModeChange = { viewMode = it },
            )
        }

        item {
            CollectionsPane(
                viewMode = viewMode,
                collections = collections,
                onMoreClick = { collection ->
                    currentSheet = CollectionsBottomSheet.Actions(collection)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CollectionsControlRow(
    sortLabel: String,
    selectedViewMode: CollectionsViewMode,
    onSortClick: () -> Unit,
    onViewModeChange: (CollectionsViewMode) -> Unit,
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
                shape = collectionsControlIconShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = "Open collection sorting options",
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
            val viewModes = listOf(CollectionsViewMode.LIST, CollectionsViewMode.GRID)

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
                        imageVector = if (mode == CollectionsViewMode.LIST) {
                            Icons.AutoMirrored.Outlined.ViewList
                        } else {
                            Icons.Outlined.GridView
                        },
                        contentDescription = if (mode == CollectionsViewMode.LIST) "Collections list view" else "Collections grid view",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CollectionsPane(
    viewMode: CollectionsViewMode,
    collections: List<CollectionUi>,
    onMoreClick: (CollectionUi) -> Unit,
) {
    AnimatedContent(
        targetState = viewMode,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 180,
                    easing = collectionsAnimationEasing,
                ),
            ) togetherWith fadeOut(
                animationSpec = tween(
                    durationMillis = 90,
                    easing = FastOutSlowInEasing,
                ),
            )
        },
        label = "collectionsViewMode",
    ) { mode ->
        if (collections.isEmpty()) {
            EmptyCollectionsState(message = "No collections yet.")
        } else if (mode == CollectionsViewMode.GRID) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                collections.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowItems.forEach { collection ->
                            CollectionGridCard(
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
                    CollectionListCard(
                        collection = collection,
                        onMoreClick = { onMoreClick(collection) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CollectionGridCard(
    collection: CollectionUi,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = collectionsCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { })
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                CollectionLeadingIcon(isStarred = collection.isStarred, size = 34.dp)

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
private fun CollectionListCard(
    collection: CollectionUi,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = collectionsCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { })
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CollectionLeadingIcon(isStarred = collection.isStarred, size = 32.dp)

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
private fun CollectionLeadingIcon(
    isStarred: Boolean,
    size: androidx.compose.ui.unit.Dp,
) {
    Box {
        Icon(
            imageVector = Icons.Outlined.Folder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(size),
        )

        if (isStarred) {
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
    }
}

@Composable
private fun EmptyCollectionsState(
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
private fun CollectionsActionsSheet(
    target: CollectionUi,
    actionGroups: List<List<CollectionActionUi>>,
    onActionClick: () -> Unit,
) {
    Column {
        CollectionsSheetHeader(
            title = target.title,
            icon = Icons.Outlined.Folder,
        )
        CollectionsSheetDivider()

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
                CollectionsSheetDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CollectionsSheetHeader(
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
private fun CollectionsSheetSectionTitle(
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
private fun CollectionsSheetDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier.padding(start = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
    )
}

@Composable
private fun CollectionsSelectableSheetRow(
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
