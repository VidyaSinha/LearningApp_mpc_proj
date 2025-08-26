package com.example.learningapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

data class ScanEntry(
    val imagePath: String,
    val summary: String,
    val timestamp: Long
)

object ScanHistoryStore {

    private const val PREFS = "ScanHistoryPrefs"
    private const val KEY_LIST = "scans"

    fun saveScan(context: Context, bitmap: Bitmap, summary: String) {
        val dir = File(context.filesDir, "scans")
        if (!dir.exists()) dir.mkdirs()
        val ts = System.currentTimeMillis()
        val file = File(dir, "scan_$ts.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        val entry = ScanEntry(file.absolutePath, summary, ts)
        val list = loadAll(context).toMutableList()
        list.add(0, entry)
        persist(context, list)
    }

    fun loadAll(context: Context): List<ScanEntry> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LIST, "[]") ?: "[]"
        val arr = JSONArray(json)
        val out = mutableListOf<ScanEntry>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(
                ScanEntry(
                    imagePath = o.getString("imagePath"),
                    summary = o.getString("summary"),
                    timestamp = o.getLong("timestamp")
                )
            )
        }
        return out
    }

    fun deleteScan(context: Context, entry: ScanEntry) {
        // delete image file if present
        try {
            val f = File(entry.imagePath)
            if (f.exists()) f.delete()
        } catch (_: Exception) { }

        val remaining = loadAll(context).filterNot { it.imagePath == entry.imagePath && it.timestamp == entry.timestamp }
        persist(context, remaining)
    }

    fun clearAll(context: Context) {
        val all = loadAll(context)
        all.forEach { e ->
            try {
                val f = File(e.imagePath)
                if (f.exists()) f.delete()
            } catch (_: Exception) { }
        }
        persist(context, emptyList())
    }

    private fun persist(context: Context, list: List<ScanEntry>) {
        val arr = JSONArray()
        list.forEach { e ->
            val o = JSONObject()
            o.put("imagePath", e.imagePath)
            o.put("summary", e.summary)
            o.put("timestamp", e.timestamp)
            arr.put(o)
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LIST, arr.toString())
            .apply()
    }
}


