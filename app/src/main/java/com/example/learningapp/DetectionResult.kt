package com.example.learningapp

import android.graphics.RectF

data class DetectionResult(
    val label: String,
    val confidence: Float,
    val box: RectF,
    val color: String,
    val size: String,
    val quality: String
)
