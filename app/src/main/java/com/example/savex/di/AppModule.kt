package com.example.savex.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.savex.core.database.CollectionDao
import com.example.savex.core.database.DefaultItemTypes
import com.example.savex.core.database.ItemTypeDao
import com.example.savex.core.database.SaveXDatabase
import com.example.savex.core.database.SavedItemDao
import com.example.savex.core.database.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val SESSION_DATASTORE = "session.preferences_pb"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(SESSION_DATASTORE) },
    )

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): SaveXDatabase {
        return Room.databaseBuilder(
            context,
            SaveXDatabase::class.java,
            "savex.db",
        ).addCallback(
            object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    seedDefaultItemTypes(db)
                }
            },
        ).build()
    }

    @Provides
    fun provideUserDao(database: SaveXDatabase): UserDao = database.userDao()

    @Provides
    fun provideCollectionDao(database: SaveXDatabase): CollectionDao = database.collectionDao()

    @Provides
    fun provideItemTypeDao(database: SaveXDatabase): ItemTypeDao = database.itemTypeDao()

    @Provides
    fun provideSavedItemDao(database: SaveXDatabase): SavedItemDao = database.savedItemDao()

    private fun seedDefaultItemTypes(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()
        DefaultItemTypes.all.forEachIndexed { index, itemType ->
            db.execSQL(
                """
                INSERT INTO item_types (
                    id,
                    userId,
                    name,
                    icon,
                    isSystem,
                    isActive,
                    createdAt,
                    updatedAt,
                    syncStatus
                ) VALUES (?, NULL, ?, ?, 1, 1, ?, ?, 'SYNCED')
                """.trimIndent(),
                arrayOf(
                    "system-${index + 1}",
                    itemType.name,
                    itemType.icon,
                    now,
                    now,
                ),
            )
        }
    }
}
