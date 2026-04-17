package com.example.savex.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class ItemStatus {
    ACTIVE,
    ARCHIVED,
    DELETED,
}

enum class SyncStatus {
    SYNCED,
    PENDING_UPDATE,
    PENDING_DELETE,
}

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)],
)
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val email: String,
    val displayName: String? = null,
    val profilePic: String? = null,
    val googleId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "auth_credentials",
    primaryKeys = ["userId"],
)
data class AuthCredentialEntity(
    val userId: String,
    val passwordHash: String,
    val lastPasswordReset: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "collections",
    indices = [Index(value = ["userId"]), Index(value = ["status"])],
)
data class CollectionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val isStarred: Boolean = false,
    val status: ItemStatus = ItemStatus.ACTIVE,
    val reminderSettingsJson: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPDATE,
)

@Entity(
    tableName = "item_types",
    indices = [Index(value = ["userId"]), Index(value = ["name", "userId"], unique = true)],
)
data class ItemTypeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String? = null,
    val name: String,
    val icon: String,
    val isSystem: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
)

@Entity(
    tableName = "saved_items",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["collectionId"]),
        Index(value = ["itemTypeId"]),
        Index(value = ["status"]),
        Index(value = ["isStarred"]),
    ],
)
data class SavedItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val collectionId: String? = null,
    val itemTypeId: String,
    val title: String,
    val description: String? = null,
    val url: String? = null,
    val tags: List<String> = emptyList(),
    val isStarred: Boolean = false,
    val status: ItemStatus = ItemStatus.ACTIVE,
    val reminderCount: Int = 0,
    val nextReminderAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPDATE,
)

object DefaultItemTypes {
    val all = listOf(
        ItemTypeSeed(name = "Article", icon = "article"),
        ItemTypeSeed(name = "Video", icon = "play_circle"),
        ItemTypeSeed(name = "Image", icon = "image"),
        ItemTypeSeed(name = "Podcast", icon = "podcasts"),
        ItemTypeSeed(name = "Social", icon = "tag"),
    )
}

data class ItemTypeSeed(
    val name: String,
    val icon: String,
)
