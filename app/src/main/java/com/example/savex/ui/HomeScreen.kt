package com.example.savex.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

private data class ReviewTodayItemUi(
    val title: String,
    val source: String,
    val imageUrl: String,
    val sheetIcon: ImageVector,
)

private data class RecentlySavedItemUi(
    val title: String,
    val source: String,
    val tag: String,
    val savedAt: String,
    val thumbnailUrl: String?,
    val thumbnailIcon: ImageVector,
)

private data class HomeSheetTargetUi(
    val title: String,
    val icon: ImageVector,
)

private data class HomeItemActionUi(
    val title: String,
    val icon: ImageVector,
)

private val reviewTodayItems = listOf(
    ReviewTodayItemUi(
        title = "The Architecture of a Modern Android App",
        source = "medium.com",
        imageUrl = "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=400&h=200&fit=crop",
        sheetIcon = Icons.Outlined.Article,
    ),
    ReviewTodayItemUi(
        title = "PostgreSQL JSONB Tutorial",
        source = "youtube.com",
        imageUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400&h=200&fit=crop",
        sheetIcon = Icons.Outlined.VideoLibrary,
    ),
)

private val recentlySavedItems = listOf(
    RecentlySavedItemUi(
        title = "10 Best Jetpack Compose Tips",
        source = "androidweekly.net",
        tag = "#android",
        savedAt = "2h ago",
        thumbnailUrl = "https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?w=100&h=100&fit=crop",
        thumbnailIcon = Icons.Outlined.Article,
    ),
    RecentlySavedItemUi(
        title = "Spring Boot 3 + JWT Auth",
        source = "youtube.com",
        tag = "#backend",
        savedAt = "Yesterday",
        thumbnailUrl = null,
        thumbnailIcon = Icons.Outlined.VideoLibrary,
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
    listOf(
        HomeItemActionUi("Edit", Icons.Outlined.Edit),
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    var selectedSheetTarget by remember { mutableStateOf<HomeSheetTargetUi?>(null) }

    if (selectedSheetTarget != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedSheetTarget = null },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        ) {
            HomeSheetHeader(
                title = selectedSheetTarget?.title.orEmpty(),
                icon = selectedSheetTarget?.icon ?: Icons.Outlined.Article,
            )
            HorizontalDivider()

            homeActionGroups.forEachIndexed { index, group ->
                group.forEach { action ->
                    val isDeleteAction = action.title == "Delete"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSheetTarget = null }
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
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isDeleteAction) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                }

                if (index < homeActionGroups.lastIndex) {
                    HorizontalDivider()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 116.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            SectionHeader(
                title = "Review Today",
                actionLabel = "See all >",
                onActionClick = { },
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(end = 4.dp),
            ) {
                items(reviewTodayItems, key = { it.title }) { item ->
                    ReviewTodayCard(
                        item = item,
                        onMoreClick = {
                            selectedSheetTarget = HomeSheetTargetUi(
                                title = item.title,
                                icon = item.sheetIcon,
                            )
                        },
                    )
                }
            }
        }

        item {
            Text(
                text = "Recently Saved",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        items(recentlySavedItems, key = { it.title }) { item ->
            RecentlySavedCard(
                item = item,
                onMoreClick = {
                    selectedSheetTarget = HomeSheetTargetUi(
                        title = item.title,
                        icon = item.thumbnailIcon,
                    )
                },
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onActionClick) {
            Text(actionLabel)
        }
    }
}

@Composable
private fun ReviewTodayCard(
    item: ReviewTodayItemUi,
    onMoreClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.width(240.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 12.dp, end = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.source,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    IconButton(onClick = onMoreClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Item actions")
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentlySavedCard(
    item: RecentlySavedItemUi,
    onMoreClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (item.thumbnailUrl != null) {
                    AsyncImage(
                        model = item.thumbnailUrl,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        imageVector = item.thumbnailIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }

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
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = item.source,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    IconButton(onClick = onMoreClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Item actions")
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
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Text(
                                text = item.tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }

                    Text(
                        text = item.savedAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
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
            .padding(horizontal = 20.dp, vertical = 10.dp),
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
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
