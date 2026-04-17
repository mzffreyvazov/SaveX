package com.example.savex.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val sessionState: Flow<SessionState> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            SessionState(
                currentUserId = preferences[CURRENT_USER_ID],
                jwtToken = preferences[JWT_TOKEN],
                lastSyncAt = preferences[LAST_SYNC_AT],
            )
        }

    suspend fun updateSession(
        currentUserId: String?,
        jwtToken: String?,
    ) {
        dataStore.edit { preferences ->
            if (currentUserId == null) {
                preferences.remove(CURRENT_USER_ID)
            } else {
                preferences[CURRENT_USER_ID] = currentUserId
            }

            if (jwtToken == null) {
                preferences.remove(JWT_TOKEN)
            } else {
                preferences[JWT_TOKEN] = jwtToken
            }
        }
    }

    suspend fun updateLastSync(timestampMillis: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC_AT] = timestampMillis
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    data class SessionState(
        val currentUserId: String?,
        val jwtToken: String?,
        val lastSyncAt: Long?,
    )

    private companion object {
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val LAST_SYNC_AT = longPreferencesKey("last_sync_at")
    }
}
