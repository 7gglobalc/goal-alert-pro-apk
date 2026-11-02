package com.example.service

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("goalalert_favorites_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITE_IDS = "favorite_match_ids"
        val DEFAULT_FAVORITES = setOf("ucl_1", "pl_1", "laliga_1") // UEFA Champions League, Man City vs Arsenal & El Clásico default favorites
    }

    private val _favoriteIds = MutableStateFlow<Set<String>>(loadFavorites())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private fun loadFavorites(): Set<String> {
        val stored = prefs.getStringSet(KEY_FAVORITE_IDS, null)
        return stored ?: DEFAULT_FAVORITES
    }

    fun isFavorite(matchId: String): Boolean {
        return _favoriteIds.value.contains(matchId)
    }

    fun toggleFavorite(matchId: String): Boolean {
        val current = _favoriteIds.value.toMutableSet()
        val isNowFavorite = if (current.contains(matchId)) {
            current.remove(matchId)
            false
        } else {
            current.add(matchId)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITE_IDS, current).apply()
        _favoriteIds.value = current
        return isNowFavorite
    }

    fun setFavorite(matchId: String, favorite: Boolean) {
        val current = _favoriteIds.value.toMutableSet()
        if (favorite) {
            current.add(matchId)
        } else {
            current.remove(matchId)
        }
        prefs.edit().putStringSet(KEY_FAVORITE_IDS, current).apply()
        _favoriteIds.value = current
    }

    fun getFavoriteIds(): Set<String> {
        return _favoriteIds.value
    }
}
