package com.example.savex.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.ViewList
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
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.savex.ui.theme.PocketCoral

private const val HOME_TAB_SWITCH_ENTER_DURATION_MS = 180
private const val HOME_TAB_SWITCH_EXIT_DURATION_MS = 90
private const val HOME_TAB_SWITCH_ENTER_DELAY_MS = 35
private const val HOME_TAB_SWITCH_INITIAL_SCALE = 0.98f
private val HomeTabEmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
private val HomeCardShape = RoundedCornerShape(18.dp)
private val HomeThumbnailShape = RoundedCornerShape(14.dp)
private val HomeControlIconShape = RoundedCornerShape(999.dp)
private val HomeGridContentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 116.dp)
private val HomeListContentPadding = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 116.dp)

private enum class HomeContentTab(val title: String) {
    SAVED("Saved"),
    REVIEW("Review"),
}

private enum class HomeViewMode {
    LIST,
    GRID,
}

private enum class SavedSortOption(val label: String) {
    DATE_SAVED("Date saved"),
    NAME("Name"),
}

private enum class SavedFilterOption(val label: String) {
    ALL_TYPES("All types"),
    ARTICLES("Articles"),
    VIDEOS("Videos"),
    DOCUMENTS("Documents"),
}

private enum class ReviewSortOption(val label: String) {
    DUE_DATE("Due date"),
    DATE_SAVED("Date saved"),
}

private enum class ReviewFilterOption(val label: String) {
    ALL_TASKS("All tasks"),
    OVERDUE("Overdue"),
    DUE_TODAY("Due today"),
}

private enum class SavedItemType {
    ARTICLE,
    VIDEO,
    DOCUMENT,
}

private enum class ReviewStatus(val label: String) {
    OVERDUE("Overdue"),
    DUE_TODAY("Scheduled for today"),
}

private data class SavedItemUi(
    val title: String,
    val source: String,
    val tag: String,
    val savedAt: String,
    val thumbnailUrl: String?,
    val thumbnailIcon: ImageVector,
    val itemType: SavedItemType,
    val isStarred: Boolean,
    val sheetIcon: ImageVector,
    val sortRank: Int,
)

private data class ReviewItemUi(
    val title: String,
    val source: String,
    val thumbnailUrl: String?,
    val thumbnailIcon: ImageVector,
    val status: ReviewStatus,
    val sheetIcon: ImageVector,
    val dueRank: Int,
    val savedRank: Int,
)

private data class HomeSheetTargetUi(
    val title: String,
    val icon: ImageVector,
)

private data class HomeItemActionUi(
    val title: String,
    val icon: ImageVector,
)

private sealed interface HomeBottomSheet {
    data class ItemActions(val target: HomeSheetTargetUi) : HomeBottomSheet
    data object SavedSortAndFilter : HomeBottomSheet
    data object ReviewSortAndFilter : HomeBottomSheet
}

private val savedItems = listOf(
    SavedItemUi(
        title = "10 Best Jetpack Compose Tips",
        source = "androidweekly.net",
        tag = "#android",
        savedAt = "2h ago",
        thumbnailUrl = "https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?w=400&h=400&fit=crop",
        thumbnailIcon = Icons.AutoMirrored.Outlined.Article,
        itemType = SavedItemType.ARTICLE,
        isStarred = true,
        sheetIcon = Icons.AutoMirrored.Outlined.Article,
        sortRank = 0,
    ),
    SavedItemUi(
        title = "Spring Boot 3 + JWT Auth",
        source = "youtube.com",
        tag = "#backend",
        savedAt = "Yesterday",
        thumbnailUrl = null,
        thumbnailIcon = Icons.Outlined.VideoLibrary,
        itemType = SavedItemType.VIDEO,
        isStarred = true,
        sheetIcon = Icons.Outlined.VideoLibrary,
        sortRank = 1,
    ),
    SavedItemUi(
        title = "Data Structures PDF",
        source = "local document",
        tag = "#study",
        savedAt = "April 18",
        thumbnailUrl = null,
        thumbnailIcon = Icons.Outlined.Description,
        itemType = SavedItemType.DOCUMENT,
        isStarred = false,
        sheetIcon = Icons.Outlined.Description,
        sortRank = 2,
    ),
)

private val reviewItems = listOf(
    ReviewItemUi(
        title = "Architecture of a Modern Android App",
        source = "medium.com",
        thumbnailUrl = "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=400&h=400&fit=crop",
        thumbnailIcon = Icons.AutoMirrored.Outlined.Article,
        status = ReviewStatus.DUE_TODAY,
        sheetIcon = Icons.AutoMirrored.Outlined.Article,
        dueRank = 0,
        savedRank = 1,
    ),
    ReviewItemUi(
        title = "PostgreSQL JSONB Tutorial",
        source = "youtube.com",
        thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400&h=400&fit=crop",
        thumbnailIcon = Icons.Outlined.VideoLibrary,
        status = ReviewStatus.DUE_TODAY,
        sheetIcon = Icons.Outlined.VideoLibrary,
        dueRank = 1,
        savedRank = 0,
    ),
)

private val homeActionGroups = listOf(
    listOf(
        HomeItemActionUi("Add to starred", Icons.Outlined.StarOutline),
        HomeItemActionUi("Add to collection", Icons.Outlined.CreateNewFolder),
    ),
    listOf(
        HomeItemActionUi("Copy link", Icons.Outlined.Link),
        HomeItemActionUi("Share", Icons.Outlined.Share),
    ),
    listOf(
        HomeItemActionUi("Archive", Icons.Outlined.Archive),
        HomeItemActionUi("Snooze", Icons.Outlined.Bedtime),
        HomeItemActionUi("Delete", Icons.Outlined.Delete),
    ),
)

private fun tabSwitchEnterTransition() =
    fadeIn(
        animationSpec = tween(
            durationMillis = HOME_TAB_SWITCH_ENTER_DURATION_MS,
            delayMillis = HOME_TAB_SWITCH_ENTER_DELAY_MS,
            easing = HomeTabEmphasizedEasing,
        ),
    ) + scaleIn(
        initialScale = HOME_TAB_SWITCH_INITIAL_SCALE,
        animationSpec = tween(
            durationMillis = HOME_TAB_SWITCH_ENTER_DURATION_MS,
            delayMillis = HOME_TAB_SWITCH_ENTER_DELAY_MS,
            easing = HomeTabEmphasizedEasing,
        ),
    )

private fun tabSwitchExitTransition() =
    fadeOut(
        animationSpec = tween(
            durationMillis = HOME_TAB_SWITCH_EXIT_DURATION_MS,
            easing = FastOutSlowInEasing,
        ),
    ) + scaleOut(
        targetScale = HOME_TAB_SWITCH_INITIAL_SCALE,
        animationSpec = tween(
            durationMillis = HOME_TAB_SWITCH_EXIT_DURATION_MS,
            easing = FastOutSlowInEasing,
        ),
    )

private fun viewSwitchEnterTransition() =
    fadeIn(
        animationSpec = tween(
            durationMillis = 180,
            easing = HomeTabEmphasizedEasing,
        ),
    )

private fun viewSwitchExitTransition() =
    fadeOut(
        animationSpec = tween(
            durationMillis = 90,
            easing = FastOutSlowInEasing,
        ),
    )

private fun List<SavedItemUi>.applySavedFilters(
    sortOption: SavedSortOption,
    filterOption: SavedFilterOption,
): List<SavedItemUi> {
    val filteredItems = filter { item ->
        when (filterOption) {
            SavedFilterOption.ALL_TYPES -> true
            SavedFilterOption.ARTICLES -> item.itemType == SavedItemType.ARTICLE
            SavedFilterOption.VIDEOS -> item.itemType == SavedItemType.VIDEO
            SavedFilterOption.DOCUMENTS -> item.itemType == SavedItemType.DOCUMENT
        }
    }

    return when (sortOption) {
        SavedSortOption.DATE_SAVED -> filteredItems.sortedBy { it.sortRank }
        SavedSortOption.NAME -> filteredItems.sortedBy { it.title.lowercase() }
    }
}

private fun List<ReviewItemUi>.applyReviewFilters(
    sortOption: ReviewSortOption,
    filterOption: ReviewFilterOption,
): List<ReviewItemUi> {
    val filteredItems = filter { item ->
        when (filterOption) {
            ReviewFilterOption.ALL_TASKS -> true
            ReviewFilterOption.OVERDUE -> item.status == ReviewStatus.OVERDUE
            ReviewFilterOption.DUE_TODAY -> item.status == ReviewStatus.DUE_TODAY
        }
    }

    return when (sortOption) {
        ReviewSortOption.DUE_DATE -> filteredItems.sortedBy { it.dueRank }
        ReviewSortOption.DATE_SAVED -> filteredItems.sortedBy { it.savedRank }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(HomeContentTab.SAVED) }
    var savedViewMode by rememberSaveable { mutableStateOf(HomeViewMode.LIST) }
    var reviewViewMode by rememberSaveable { mutableStateOf(HomeViewMode.LIST) }
    var savedSortOption by rememberSaveable { mutableStateOf(SavedSortOption.DATE_SAVED) }
    var savedFilterOption by rememberSaveable { mutableStateOf(SavedFilterOption.ALL_TYPES) }
    var reviewSortOption by rememberSaveable { mutableStateOf(ReviewSortOption.DUE_DATE) }
    var reviewFilterOption by rememberSaveable { mutableStateOf(ReviewFilterOption.ALL_TASKS) }
    var currentSheet by remember { mutableStateOf<HomeBottomSheet?>(null) }

    val filteredSavedItems = remember(savedSortOption, savedFilterOption) {
        savedItems.applySavedFilters(savedSortOption, savedFilterOption)
    }
    val filteredReviewItems = remember(reviewSortOption, reviewFilterOption) {
        reviewItems.applyReviewFilters(reviewSortOption, reviewFilterOption)
    }

    if (currentSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { currentSheet = null },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            when (val sheet = currentSheet) {
                is HomeBottomSheet.ItemActions -> {
                    HomeActionsSheet(
                        target = sheet.target,
                        onActionClick = { currentSheet = null },
                    )
                }

                HomeBottomSheet.SavedSortAndFilter -> {
                    SavedSortAndFilterSheet(
                        selectedSort = savedSortOption,
                        selectedFilter = savedFilterOption,
                        onSortSelected = { savedSortOption = it },
                        onFilterSelected = { savedFilterOption = it },
                    )
                }

                HomeBottomSheet.ReviewSortAndFilter -> {
                    ReviewSortAndFilterSheet(
                        selectedSort = reviewSortOption,
                        selectedFilter = reviewFilterOption,
                        onSortSelected = { reviewSortOption = it },
                        onFilterSelected = { reviewFilterOption = it },
                    )
                }

                null -> Unit
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        HomeTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )

        HomeControlRow(
            sortLabel = if (selectedTab == HomeContentTab.SAVED) {
                savedSortOption.label
            } else {
                reviewSortOption.label
            },
            selectedViewMode = if (selectedTab == HomeContentTab.SAVED) savedViewMode else reviewViewMode,
            onSortClick = {
                currentSheet = if (selectedTab == HomeContentTab.SAVED) {
                    HomeBottomSheet.SavedSortAndFilter
                } else {
                    HomeBottomSheet.ReviewSortAndFilter
                }
            },
            onViewModeChange = { mode ->
                if (selectedTab == HomeContentTab.SAVED) {
                    savedViewMode = mode
                } else {
                    reviewViewMode = mode
                }
            },
        )

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                tabSwitchEnterTransition() togetherWith tabSwitchExitTransition()
            },
            modifier = Modifier.fillMaxSize(),
            label = "homeTabContent",
        ) { tab ->
            when (tab) {
                HomeContentTab.SAVED -> SavedItemsPane(
                    viewMode = savedViewMode,
                    items = filteredSavedItems,
                    onMoreClick = { item ->
                        currentSheet = HomeBottomSheet.ItemActions(
                            HomeSheetTargetUi(
                                title = item.title,
                                icon = item.sheetIcon,
                            ),
                        )
                    },
                )

                HomeContentTab.REVIEW -> ReviewItemsPane(
                    viewMode = reviewViewMode,
                    items = filteredReviewItems,
                    onMoreClick = { item ->
                        currentSheet = HomeBottomSheet.ItemActions(
                            HomeSheetTargetUi(
                                title = item.title,
                                icon = item.sheetIcon,
                            ),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun HomeTabs(
    selectedTab: HomeContentTab,
    onTabSelected: (HomeContentTab) -> Unit,
) {
    SecondaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTab.ordinal, matchContentSize = true),
                height = 3.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        divider = {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
        },
    ) {
        HomeContentTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            Tab(
                selected = selected,
                onClick = { onTabSelected(tab) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.sp,
                        ),
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeControlRow(
    sortLabel: String,
    selectedViewMode: HomeViewMode,
    onSortClick: () -> Unit,
    onViewModeChange: (HomeViewMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
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
                shape = HomeControlIconShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = "Open sort and filter options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        ConnectedViewModeButtonGroup(
            selectedViewMode = selectedViewMode,
            onViewModeChange = onViewModeChange,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ConnectedViewModeButtonGroup(
    selectedViewMode: HomeViewMode,
    onViewModeChange: (HomeViewMode) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val viewModes = listOf(HomeViewMode.LIST, HomeViewMode.GRID)

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
                    imageVector = if (mode == HomeViewMode.LIST) {
                        Icons.Outlined.ViewList
                    } else {
                        Icons.Outlined.GridView
                    },
                    contentDescription = if (mode == HomeViewMode.LIST) "List view" else "Grid view",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun SavedItemsPane(
    viewMode: HomeViewMode,
    items: List<SavedItemUi>,
    onMoreClick: (SavedItemUi) -> Unit,
) {
    AnimatedContent(
        targetState = viewMode,
        transitionSpec = {
            viewSwitchEnterTransition() togetherWith viewSwitchExitTransition()
        },
        modifier = Modifier.fillMaxSize(),
        label = "savedViewMode",
    ) { mode ->
        when (mode) {
            HomeViewMode.LIST -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = HomeListContentPadding,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (items.isEmpty()) {
                        item {
                            EmptyHomeState("No saved items match this filter.")
                        }
                    } else {
                        items(items, key = { it.title }) { item ->
                            SavedListItemCard(
                                item = item,
                                onMoreClick = { onMoreClick(item) },
                            )
                        }
                    }
                }
            }

            HomeViewMode.GRID -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = HomeGridContentPadding,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (items.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyHomeState("No saved items match this filter.")
                        }
                    } else {
                        items(items, key = { it.title }) { item ->
                            SavedGridItemCard(
                                item = item,
                                onMoreClick = { onMoreClick(item) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewItemsPane(
    viewMode: HomeViewMode,
    items: List<ReviewItemUi>,
    onMoreClick: (ReviewItemUi) -> Unit,
) {
    AnimatedContent(
        targetState = viewMode,
        transitionSpec = {
            viewSwitchEnterTransition() togetherWith viewSwitchExitTransition()
        },
        modifier = Modifier.fillMaxSize(),
        label = "reviewViewMode",
    ) { mode ->
        when (mode) {
            HomeViewMode.LIST -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = HomeListContentPadding,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (items.isEmpty()) {
                        item {
                            EmptyHomeState("No review tasks match this filter.")
                        }
                    } else {
                        items(items, key = { it.title }) { item ->
                            ReviewListItemCard(
                                item = item,
                                onMoreClick = { onMoreClick(item) },
                            )
                        }
                    }
                }
            }

            HomeViewMode.GRID -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = HomeGridContentPadding,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (items.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyHomeState("No review tasks match this filter.")
                        }
                    } else {
                        items(items, key = { it.title }) { item ->
                            ReviewGridItemCard(
                                item = item,
                                onMoreClick = { onMoreClick(item) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedListItemCard(
    item: SavedItemUi,
    onMoreClick: () -> Unit,
) {
    val thumbnailContainerColor = when {
        item.thumbnailUrl != null -> MaterialTheme.colorScheme.surfaceVariant
        item.itemType == SavedItemType.VIDEO -> PocketCoral.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val thumbnailIconTint = if (item.itemType == SavedItemType.VIDEO) {
        PocketCoral
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = HomeCardShape,
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
            ItemThumbnail(
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
                        if (item.isStarred) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        MetaChip(text = item.tag)
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
private fun SavedGridItemCard(
    item: SavedItemUi,
    onMoreClick: () -> Unit,
) {
    val thumbnailContainerColor = when {
        item.thumbnailUrl != null -> MaterialTheme.colorScheme.surfaceVariant
        item.itemType == SavedItemType.VIDEO -> PocketCoral.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val thumbnailIconTint = if (item.itemType == SavedItemType.VIDEO) {
        PocketCoral
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = HomeCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            ItemThumbnail(
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
                        if (item.isStarred) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        MetaChip(text = item.tag)
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
private fun ReviewListItemCard(
    item: ReviewItemUi,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = HomeCardShape,
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
            ItemThumbnail(
                title = item.title,
                thumbnailUrl = item.thumbnailUrl,
                thumbnailIcon = item.thumbnailIcon,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
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

                ReviewStatusChip(status = item.status)
            }
        }
    }
}

@Composable
private fun ReviewGridItemCard(
    item: ReviewItemUi,
    onMoreClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = HomeCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            ItemThumbnail(
                title = item.title,
                thumbnailUrl = item.thumbnailUrl,
                thumbnailIcon = item.thumbnailIcon,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
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

                ReviewStatusChip(status = item.status)
            }
        }
    }
}

@Composable
private fun ItemThumbnail(
    title: String,
    thumbnailUrl: String?,
    thumbnailIcon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(HomeThumbnailShape)
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
private fun MetaChip(
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
private fun ReviewStatusChip(
    status: ReviewStatus,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun EmptyHomeState(
    message: String,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
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
private fun HomeActionsSheet(
    target: HomeSheetTargetUi,
    onActionClick: () -> Unit,
) {
    Column {
        HomeSheetHeader(
            title = target.title,
            icon = target.icon,
        )
        HorizontalDivider()

        homeActionGroups.forEachIndexed { groupIndex, group ->
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

            if (groupIndex < homeActionGroups.lastIndex) {
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SavedSortAndFilterSheet(
    selectedSort: SavedSortOption,
    selectedFilter: SavedFilterOption,
    onSortSelected: (SavedSortOption) -> Unit,
    onFilterSelected: (SavedFilterOption) -> Unit,
) {
    Column {
        HomeSheetSectionTitle(text = "Sort by")
        HorizontalDivider()
        SavedSortOption.entries.forEach { option ->
            SelectableSheetRow(
                label = option.label,
                selected = option == selectedSort,
                onClick = { onSortSelected(option) },
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        HomeSheetSectionTitle(text = "Show")
        SavedFilterOption.entries.forEach { option ->
            SelectableSheetRow(
                label = option.label,
                selected = option == selectedFilter,
                onClick = { onFilterSelected(option) },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ReviewSortAndFilterSheet(
    selectedSort: ReviewSortOption,
    selectedFilter: ReviewFilterOption,
    onSortSelected: (ReviewSortOption) -> Unit,
    onFilterSelected: (ReviewFilterOption) -> Unit,
) {
    Column {
        HomeSheetSectionTitle(text = "Sort by")
        HorizontalDivider()
        ReviewSortOption.entries.forEach { option ->
            SelectableSheetRow(
                label = option.label,
                selected = option == selectedSort,
                onClick = { onSortSelected(option) },
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        HomeSheetSectionTitle(text = "Show")
        ReviewFilterOption.entries.forEach { option ->
            SelectableSheetRow(
                label = option.label,
                selected = option == selectedFilter,
                onClick = { onFilterSelected(option) },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HomeSheetHeader(
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
private fun HomeSheetSectionTitle(
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
private fun SelectableSheetRow(
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
