package com.example.learningapp

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ScanHistoryStore {

    private const val PREFS_NAME = "ScanHistoryPrefs"
    private const val PREFS_KEY = "scan_history"
    private val gson = Gson()

    fun saveScan(context: Context, bitmap: Bitmap, summary: String) {
        val id = UUID.randomUUID().toString()
        val fileName = "$id.png"
        val file = File(context.filesDir, fileName)

        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        val allItems = loadAll(context).toMutableList()
        val newItem = ScanHistoryItem(
            id = id,
            imagePath = file.absolutePath,
            summary = summary,
            timestamp = System.currentTimeMillis(),
            isFavorite = false
        )
        allItems.add(0, newItem)
        saveAll(context, allItems)
    }

    fun loadAll(context: Context): List<ScanHistoryItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(PREFS_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<ScanHistoryItem>>() {}.type
        val items: List<ScanHistoryItem> = gson.fromJson(json, type)
        return items.sortedByDescending { it.timestamp }
    }

    fun loadFavorites(context: Context): List<ScanHistoryItem> {
        return loadAll(context).filter { it.isFavorite }
    }



    fun deleteScan(context: Context, itemId: String) {
        val allItems = loadAll(context).toMutableList()
        val itemToRemove = allItems.find { it.id == itemId }
        if (itemToRemove != null) {
            File(itemToRemove.imagePath).delete()
            allItems.removeAll { it.id == itemId }
            saveAll(context, allItems)
        }
    }

    fun toggleFavoriteStatus(context: Context, itemId: String) {
        val allItems = loadAll(context).toMutableList()
        val item = allItems.find { it.id == itemId }
        item?.let {
            it.isFavorite = !it.isFavorite
            saveAll(context, allItems)
        }
    }

    private fun saveAll(context: Context, items: List<ScanHistoryItem>) {
        val json = gson.toJson(items)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(PREFS_KEY, json)
            .apply()
    }
}