package com.example.savex.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUser(userId: String): Flow<UserEntity?>

    @Upsert
    suspend fun upsert(user: UserEntity)
}

@Dao
interface CollectionDao {
    @Query(
        """
        SELECT * FROM collections
        WHERE userId = :userId AND status = :status
        ORDER BY isStarred DESC, createdAt DESC
        """,
    )
    fun observeCollections(
        userId: String,
        status: ItemStatus = ItemStatus.ACTIVE,
    ): Flow<List<CollectionEntity>>

    @Upsert
    suspend fun upsert(collection: CollectionEntity)
}

@Dao
interface ItemTypeDao {
    @Query(
        """
        SELECT * FROM item_types
        WHERE isActive = 1 AND (isSystem = 1 OR userId = :userId)
        ORDER BY isSystem DESC, name COLLATE NOCASE
        """,
    )
    fun observeAvailableTypes(userId: String?): Flow<List<ItemTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(itemTypes: List<ItemTypeEntity>)
}

@Dao
interface SavedItemDao {
    @Query(
        """
        SELECT * FROM saved_items
        WHERE userId = :userId AND status = :status
        ORDER BY createdAt DESC
        """,
    )
    fun observeItems(
        userId: String,
        status: ItemStatus = ItemStatus.ACTIVE,
    ): Flow<List<SavedItemEntity>>

    @Query(
        """
        SELECT * FROM saved_items
        WHERE userId = :userId AND status = :status AND isStarred = 1
        ORDER BY createdAt DESC
        """,
    )
    fun observeStarredItems(
        userId: String,
        status: ItemStatus = ItemStatus.ACTIVE,
    ): Flow<List<SavedItemEntity>>

    @Upsert
    suspend fun upsert(item: SavedItemEntity)
}
