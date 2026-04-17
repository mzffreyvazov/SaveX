package com.example.savex.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        AuthCredentialEntity::class,
        CollectionEntity::class,
        ItemTypeEntity::class,
        SavedItemEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DatabaseConverters::class)
abstract class SaveXDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun collectionDao(): CollectionDao
    abstract fun itemTypeDao(): ItemTypeDao
    abstract fun savedItemDao(): SavedItemDao
}
