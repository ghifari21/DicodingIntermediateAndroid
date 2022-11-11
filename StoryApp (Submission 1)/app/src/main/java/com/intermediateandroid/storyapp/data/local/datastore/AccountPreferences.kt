package com.intermediateandroid.storyapp.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.intermediateandroid.storyapp.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    fun getAccount(): Flow<User> {
        return dataStore.data.map {
            User(
                it[ID_KEY] ?: "",
                it[NAME_KEY] ?: "",
                it[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun saveAccount(user: User) {
        dataStore.edit {
            it[ID_KEY] = user.userId
            it[NAME_KEY] = user.name
            it[TOKEN_KEY] = user.token
        }
    }

    suspend fun deleteAccount() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        private val ID_KEY = stringPreferencesKey("account_id")
        private val NAME_KEY = stringPreferencesKey("account_name")
        private val TOKEN_KEY = stringPreferencesKey("account_token")

        @Volatile
        private var INSTANCE: AccountPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): AccountPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AccountPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}