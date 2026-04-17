package com.example.savex.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Podcasts
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private data class ItemTypeUi(
    val title: String,
    val icon: ImageVector,
)

private val defaultItemTypes = listOf(
    ItemTypeUi("Video", Icons.Outlined.VideoLibrary),
    ItemTypeUi("Article", Icons.Outlined.Article),
    ItemTypeUi("Podcast", Icons.Outlined.Podcasts),
    ItemTypeUi("Social", Icons.Outlined.Tag),
)

@Composable
fun SaveScreen(
    initialSharedText: String?,
    onCreateCollection: () -> Unit,
) {
    var url by rememberSaveable(initialSharedText) { mutableStateOf(initialSharedText.orEmpty()) }
    var title by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var tags by rememberSaveable { mutableStateOf(listOf("android")) }
    var tagInput by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf(defaultItemTypes.first().title) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Save item", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "This screen is ready for manual entry and Android share-intent prefill.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("URL") },
                leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                minLines = 2,
            )
        }

        item {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notes") },
                minLines = 5,
            )
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(text = "Content type", style = MaterialTheme.typography.titleMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        defaultItemTypes.forEach { type ->
                            FilterChip(
                                selected = selectedType == type.title,
                                onClick = { selectedType = type.title },
                                label = { Text(type.title) },
                                leadingIcon = { Icon(type.icon, contentDescription = null) },
                            )
                        }
                        AssistChip(
                            onClick = { },
                            label = { Text("Custom") },
                            leadingIcon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                        )
                    }
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(text = "Tags", style = MaterialTheme.typography.titleMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        tags.forEach { tag ->
                            AssistChip(
                                onClick = { tags = tags - tag },
                                label = { Text("#$tag") },
                            )
                        }
                    }
                    OutlinedTextField(
                        value = tagInput,
                        onValueChange = { tagInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Add tag") },
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                            val normalized = tagInput.trim().removePrefix("#")
                            if (normalized.isNotEmpty() && normalized !in tags) {
                                tags = tags + normalized
                            }
                            tagInput = ""
                        },
                        enabled = tagInput.isNotBlank(),
                    ) {
                        Text("Add tag")
                    }
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(text = "Organization", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Collection picker, reminder rules, and item actions are scaffolded next.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(onClick = onCreateCollection) {
                        Text("Create collection")
                    }
                }
            }
        }
    }
}
