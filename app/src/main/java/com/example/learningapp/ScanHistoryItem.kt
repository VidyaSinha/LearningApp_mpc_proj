package com.example.learningapp

// Data class to represent a single saved scan
data class ScanHistoryItem(
    val id: String,
    val imagePath: String,
    val summary: String,
    val timestamp: Long,
    var isFavorite: Boolean = false // New field for favorites
)