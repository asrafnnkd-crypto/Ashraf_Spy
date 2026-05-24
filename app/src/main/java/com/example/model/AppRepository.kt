package com.example.model

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppRepository {
    private lateinit var prefs: SharedPreferences
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val matchType = Types.newParameterizedType(List::class.java, MatchEvent::class.java)
    private val channelType = Types.newParameterizedType(List::class.java, Channel::class.java)
    private val categoryType = Types.newParameterizedType(List::class.java, ChannelCategory::class.java)

    private val matchAdapter = moshi.adapter<List<MatchEvent>>(matchType)
    private val channelAdapter = moshi.adapter<List<Channel>>(channelType)
    private val categoryAdapter = moshi.adapter<List<ChannelCategory>>(categoryType)

    private val _matches = MutableStateFlow<List<MatchEvent>>(emptyList())
    val matches: StateFlow<List<MatchEvent>> = _matches.asStateFlow()

    private val _categories = MutableStateFlow<List<ChannelCategory>>(emptyList())
    val categories: StateFlow<List<ChannelCategory>> = _categories.asStateFlow()

    private val _streams = MutableStateFlow<List<Channel>>(emptyList())
    val streams: StateFlow<List<Channel>> = _streams.asStateFlow()

    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        prefs = context.getSharedPreferences("yacine_admin_prefs", Context.MODE_PRIVATE)

        // Matches
        val matchesJson = prefs.getString("matches_json", null)
        if (matchesJson != null) {
            try {
                _matches.value = matchAdapter.fromJson(matchesJson) ?: ChannelData.matches
            } catch (e: Exception) {
                _matches.value = ChannelData.matches
            }
        } else {
            _matches.value = ChannelData.matches
            saveMatches()
        }

        // Categories
        val categoriesJson = prefs.getString("categories_json", null)
        if (categoriesJson != null) {
            try {
                _categories.value = categoryAdapter.fromJson(categoriesJson) ?: ChannelData.categories
            } catch (e: Exception) {
                _categories.value = ChannelData.categories
            }
        } else {
            _categories.value = ChannelData.categories
            saveCategories()
        }

        // Streams / Channels
        val streamsJson = prefs.getString("streams_json", null)
        if (streamsJson != null) {
            try {
                _streams.value = channelAdapter.fromJson(streamsJson) ?: ChannelData.streams
            } catch (e: Exception) {
                _streams.value = ChannelData.streams
            }
        } else {
            _streams.value = ChannelData.streams
            saveStreams()
        }

        isInitialized = true
    }

    private fun saveMatches() {
        try {
            val json = matchAdapter.toJson(_matches.value)
            prefs.edit().putString("matches_json", json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveCategories() {
        try {
            val json = categoryAdapter.toJson(_categories.value)
            prefs.edit().putString("categories_json", json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveStreams() {
        try {
            val json = channelAdapter.toJson(_streams.value)
            prefs.edit().putString("streams_json", json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // MATCHES OPERATIONS
    fun addMatch(match: MatchEvent) {
        val current = _matches.value.toMutableList()
        current.add(0, match)
        _matches.value = current
        saveMatches()
    }

    fun updateMatch(updated: MatchEvent) {
        val current = _matches.value.toMutableList()
        val index = current.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            current[index] = updated
            _matches.value = current
            saveMatches()
        }
    }

    fun deleteMatch(matchId: String) {
        val current = _matches.value.toMutableList()
        current.removeAll { it.id == matchId }
        _matches.value = current
        saveMatches()
    }

    // CHANNELS OPERATIONS
    fun addChannel(channel: Channel) {
        val current = _streams.value.toMutableList()
        current.add(0, channel)
        _streams.value = current
        saveStreams()
    }

    fun updateChannel(updated: Channel) {
        val current = _streams.value.toMutableList()
        val index = current.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            current[index] = updated
            _streams.value = current
            saveStreams()
        }
    }

    fun deleteChannel(channelId: String) {
        val current = _streams.value.toMutableList()
        current.removeAll { it.id == channelId }
        _streams.value = current
        saveStreams()
    }

    // CATEGORY OPERATIONS
    fun addCategory(category: ChannelCategory) {
        val current = _categories.value.toMutableList()
        current.add(category)
        _categories.value = current
        saveCategories()
    }

    fun updateCategory(updated: ChannelCategory) {
        val current = _categories.value.toMutableList()
        val index = current.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            current[index] = updated
            _categories.value = current
            saveCategories()
        }
    }

    fun deleteCategory(categoryId: String) {
        // Delete category
        val currentCats = _categories.value.toMutableList()
        currentCats.removeAll { it.id == categoryId }
        _categories.value = currentCats
        saveCategories()

        // Optionally delete or filter out channels associated with this category style
        // we can leave channels or update them
    }
}
