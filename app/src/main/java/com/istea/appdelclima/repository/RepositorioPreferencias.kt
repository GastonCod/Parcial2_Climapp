package com.istea.appdelclima.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RepositorioPreferencias(private val context: Context) {

    private val CIUDAD_KEY = stringPreferencesKey("ciudad")

    fun getCiudadGuardada(): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                preferences[CIUDAD_KEY]
            }
    }

    suspend fun guardarCiudad(nombreCiudad: String) {
        context.dataStore.edit { settings ->
            settings[CIUDAD_KEY] = nombreCiudad
        }
    }
}
