package com.example.learningapp

import android.content.Context
import android.graphics.*
import com.example.learningapp.DetectionResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class Detector(private val context: Context) {

    private val detector by lazy {
        val opts = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableClassification() // returns coarse categories sometimes
            .build()
        ObjectDetection.getClient(opts)
    }

    private val labeler by lazy {
        ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    }

    /**
     * Detects objects, labels each object (crop) and enriches with color/size/quality.
     */
    fun detect(
        bitmap: Bitmap,
        onSuccess: (List<DetectionResult>, Bitmap?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val image = InputImage.fromBitmap(bitmap, 0)
        detector.process(image)
            .addOnSuccessListener { objects ->
                if (objects.isEmpty()) {
                    // Fallback: label whole image
                    labeler.process(image)
                        .addOnSuccessListener { labels ->
                            val guess = bestLabel(labels) ?: "Object"
                            val wholeBox = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
                            val enriched = listOf(enrich(bitmap, guess, 0.80f, wholeBox))
                            onSuccess(enriched, null)
                        }
                        .addOnFailureListener { onError(it) }
                } else {
                    // Label each detected object by cropping its box
                    val results = mutableListOf<DetectionResult>()
                    var processed = 0
                    objects.forEach { obj ->
                        val rect = obj.boundingBox
                        val rf = RectF(rect)
                        val crop = safeCrop(bitmap, rect)

                        if (crop == null || crop.width <= 1 || crop.height <= 1) {
                            results.add(enrich(bitmap, "Object", obj.labels.firstOrNull()?.confidence ?: 0.6f, rf))
                            processed++
                            if (processed == objects.size) onSuccess(results, null)
                        } else {
                            val objImage = InputImage.fromBitmap(crop, 0)
                            labeler.process(objImage)
                                .addOnSuccessListener { labels ->
                                    val label = bestLabel(labels) ?: coarseLabel(obj) ?: "Object"
                                    results.add(enrich(crop, label, obj.labels.firstOrNull()?.confidence ?: 0.75f, rf))
                                }
                                .addOnFailureListener {
                                    results.add(enrich(crop, coarseLabel(obj) ?: "Object", 0.65f, rf))
                                }
                                .addOnCompleteListener {
                                    processed++
                                    if (processed == objects.size) {
                                        onSuccess(results, null)
                                    }
                                }
                        }
                    }
                }
            }
            .addOnFailureListener { onError(it) }
    }

    private fun bestLabel(labels: List<ImageLabel>): String? {
        return labels.maxByOrNull { it.confidence }?.text
    }

    private fun coarseLabel(obj: DetectedObject): String? {
        // ML Kit may return coarse categories like "Food", "Home good", etc.
        return obj.labels.firstOrNull()?.text
    }

    private fun safeCrop(src: Bitmap, rect: Rect): Bitmap? {
        val left = max(0, rect.left)
        val top = max(0, rect.top)
        val right = min(src.width, rect.right)
        val bottom = min(src.height, rect.bottom)
        val w = right - left
        val h = bottom - top
        return if (w > 0 && h > 0) Bitmap.createBitmap(src, left, top, w, h) else null
    }

    private fun enrich(crop: Bitmap, rawLabel: String, confidence: Float, box: RectF): DetectionResult {
        val normalized = normalizeLabel(rawLabel)
        val color = dominantColorName(crop)
        val size = sizeFromArea(box.width() * box.height())
        val quality = estimateQuality(crop)

        return DetectionResult(
            label = normalized,
            confidence = confidence,
            box = box,
            color = color,
            size = size,
            quality = quality
        )
    }

    private fun normalizeLabel(label: String): String {
        val l = label.lowercase()
        return when {
            "apple" in l -> "Apple"
            "banana" in l -> "Banana"
            "orange" in l -> "Orange"
            "bottle" in l -> "Bottle"
            "cup" in l -> "Cup"
            "person" in l -> "Person"
            else -> label.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    // Super light HSV-based color naming for fruit-like objects
    private fun dominantColorName(bmp: Bitmap): String {
        val scaled = Bitmap.createScaledBitmap(bmp, 64, 64, true)
        val hsv = FloatArray(3)
        var reds = 0; var greens = 0; var yellows = 0; var others = 0
        for (y in 0 until scaled.height) {
            for (x in 0 until scaled.width) {
                val c = scaled.getPixel(x, y)
                Color.colorToHSV(c, hsv)
                val h = hsv[0]; val s = hsv[1]; val v = hsv[2]
                if (s < 0.2f || v < 0.2f) { others++; continue }
                when {
                    (h < 15f || h > 345f) -> reds++
                    (h in 15f..45f) -> yellows++
                    (h in 60f..160f) -> greens++
                    else -> others++
                }
            }
        }
        scaled.recycle()
        return when (maxOf(reds, greens, yellows, others)) {
            reds -> "Red"
            greens -> "Green"
            yellows -> "Yellow"
            else -> "Unknown"
        }
    }

    private fun sizeFromArea(area: Float): String {
        return when {
            area < 60_000f -> "Small"
            area < 200_000f -> "Medium"
            else -> "Large"
        }
    }

    // Very rough "quality" guess using brightness & saturation (proxy for freshness/sharpness)
    private fun estimateQuality(bmp: Bitmap): String {
        val small = Bitmap.createScaledBitmap(bmp, 32, 32, true)
        var totalSat = 0f
        var totalVal = 0f
        val hsv = FloatArray(3)
        for (y in 0 until small.height) {
            for (x in 0 until small.width) {
                Color.colorToHSV(small.getPixel(x, y), hsv)
                totalSat += hsv[1]
                totalVal += hsv[2]
            }
        }
        small.recycle()
        val avgSat = totalSat / (32f * 32f)
        val avgVal = totalVal / (32f * 32f)
        return when {
            avgSat > 0.5f && avgVal > 0.5f -> "Good"
            avgSat > 0.3f -> "Average"
            else -> "Bad"
        }
    }

    // removed price estimation per requirements
}
