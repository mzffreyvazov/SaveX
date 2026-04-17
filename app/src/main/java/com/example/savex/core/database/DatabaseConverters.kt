package com.example.savex.core.database

import androidx.room.TypeConverter

class DatabaseConverters {

    @TypeConverter
    fun fromTags(tags: List<String>): String = tags.joinToString(separator = "|")

    @TypeConverter
    fun toTags(value: String): List<String> =
        value.split("|")
            .map(String::trim)
            .filter(String::isNotEmpty)

    @TypeConverter
    fun fromItemStatus(status: ItemStatus): String = status.name

    @TypeConverter
    fun toItemStatus(value: String): ItemStatus = ItemStatus.valueOf(value)

    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}
