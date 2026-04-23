package com.example.savex.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Podcasts
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private data class ItemTypeUi(
    val title: String,
    val icon: ImageVector,
)

private val defaultItemTypes = listOf(
    ItemTypeUi("Video", Icons.Outlined.VideoLibrary),
    ItemTypeUi("Article", Icons.AutoMirrored.Outlined.Article),
    ItemTypeUi("Podcast", Icons.Outlined.Podcasts),
    ItemTypeUi("Social", Icons.Outlined.Tag),
)

@Composable
fun SaveScreen(
    initialSharedText: String?,
    onClose: () -> Unit,
    onPrimaryActionClick: () -> Unit,
    onCreateCollection: () -> Unit,
    onReviewScheduleClick: () -> Unit,
) {
    var url by rememberSaveable(initialSharedText) { mutableStateOf(initialSharedText.orEmpty()) }
    var title by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var tags by rememberSaveable { mutableStateOf(listOf("#android")) }
    var tagInput by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable(initialSharedText) {
        mutableStateOf(detectInitialItemType(initialSharedText))
    }

    val addTag = remember(tagInput, tags) {
        {
            val normalizedTag = normalizeTag(tagInput)
            if (normalizedTag != null && tags.none { it.equals(normalizedTag, ignoreCase = true) }) {
                tags = tags + normalizedTag
            }
            tagInput = ""
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SaveHeader(
                onClose = onClose,
                onPrimaryActionClick = onPrimaryActionClick,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                item {
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://") },
                        leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextField(
                            value = title,
                            onValueChange = { title = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Title",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                )
                            },
                            textStyle = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            colors = borderlessFieldColors(),
                        )

                        TextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = "Add notes, highlights, or your thoughts...",
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyLarge,
                            colors = borderlessFieldColors(),
                            minLines = 6,
                        )
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SaveSectionLabel(text = "Content type")
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            defaultItemTypes.forEach { type ->
                                val isSelected = selectedType == type.title
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedType = type.title },
                                    label = { Text(type.title) },
                                    leadingIcon = { Icon(type.icon, contentDescription = null) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                                        iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                                        borderWidth = 1.dp,
                                        selectedBorderWidth = 1.dp,
                                    ),
                                )
                            }

                            DashedCustomTypeChip(onClick = {})
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SaveSectionLabel(text = "Tags")
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)),
                            shadowElevation = 1.dp,
                        ) {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                itemVerticalAlignment = Alignment.CenterVertically,
                            ) {
                                tags.forEach { tag ->
                                    InputChip(
                                        selected = false,
                                        onClick = {
                                            tags = tags.filterNot { existing ->
                                                existing.equals(tag, ignoreCase = true)
                                            }
                                        },
                                        label = { Text(tag) },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Outlined.Close,
                                                contentDescription = "Remove $tag",
                                                modifier = Modifier.size(InputChipDefaults.AvatarSize),
                                            )
                                        },
                                        colors = InputChipDefaults.inputChipColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        ),
                                    )
                                }

                                BasicTextField(
                                    value = tagInput,
                                    onValueChange = { newValue ->
                                        val sanitizedValue = newValue.replace("\r", "")
                                        if ("\n" in sanitizedValue) {
                                            tagInput = sanitizedValue.substringBefore('\n').removePrefix("#")
                                            addTag()
                                        } else {
                                            tagInput = sanitizedValue.removePrefix("#")
                                        }
                                    },
                                    modifier = Modifier
                                        .widthIn(min = 96.dp)
                                        .padding(horizontal = 4.dp, vertical = 8.dp),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done,
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { addTag() }),
                                    decorationBox = { innerTextField ->
                                        if (tagInput.isBlank()) {
                                            Text(
                                                text = "Add tag...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.outlineVariant,
                                            )
                                        }
                                        innerTextField()
                                    },
                                )
                            }
                        }
                    }
                }

                item {
                    SaveOrganizationGroup(
                        collectionLabel = "Dev Resources",
                        reviewScheduleLabel = "Smart Spaced",
                        onCollectionClick = onCreateCollection,
                        onReviewScheduleClick = onReviewScheduleClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveHeader(
    onClose: () -> Unit,
    onPrimaryActionClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Close save screen",
            )
        }

        Button(
            onClick = onPrimaryActionClick,
            shape = RoundedCornerShape(999.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = "Create",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun SaveSectionLabel(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SaveOrganizationGroup(
    collectionLabel: String,
    reviewScheduleLabel: String,
    onCollectionClick: () -> Unit,
    onReviewScheduleClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)),
        shadowElevation = 1.dp,
    ) {
        Column {
            SaveOrganizationRow(
                title = "Collection",
                value = collectionLabel,
                icon = Icons.Outlined.Folder,
                valueColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onCollectionClick,
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
            )

            SaveOrganizationRow(
                title = "Review Schedule",
                value = reviewScheduleLabel,
                icon = Icons.Outlined.Schedule,
                valueColor = MaterialTheme.colorScheme.primary,
                onClick = onReviewScheduleClick,
            )
        }
    }
}

@Composable
private fun SaveOrganizationRow(
    title: String,
    value: String,
    icon: ImageVector,
    valueColor: Color,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = valueColor,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = valueColor,
                )
            }
        },
    )
}

@Composable
private fun DashedCustomTypeChip(
    onClick: () -> Unit,
) {
    val chipShape = RoundedCornerShape(12.dp)
    val chipBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)

    Row(
        modifier = Modifier
            .clip(chipShape)
            .clickable(onClick = onClick)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val corner = 12.dp.toPx()
                drawRoundRect(
                    color = chipBorderColor,
                    size = size,
                    cornerRadius = CornerRadius(corner, corner),
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(8.dp.toPx(), 6.dp.toPx()),
                            phase = 0f,
                        ),
                    ),
                )
            }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = "Custom",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun borderlessFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    cursorColor = MaterialTheme.colorScheme.primary,
)

private fun detectInitialItemType(initialSharedText: String?): String {
    val value = initialSharedText.orEmpty().lowercase()
    return when {
        "youtu" in value || "vimeo" in value || "video" in value -> "Video"
        "podcast" in value || "spotify" in value || "apple" in value -> "Podcast"
        "instagram" in value || "x.com" in value || "twitter.com" in value || "linkedin" in value -> "Social"
        value.isNotBlank() -> "Article"
        else -> defaultItemTypes.first().title
    }
}

private fun normalizeTag(rawValue: String): String? {
    val trimmedValue = rawValue.trim().removePrefix("#")
    if (trimmedValue.isEmpty()) return null
    return "#$trimmedValue"
}
