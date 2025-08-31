package com.example.learningapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var btnHistory: Button
    private lateinit var btnQuickRecent: Button
    private lateinit var btnFavorites: Button
    private lateinit var tvRecentsTitle: TextView
    private lateinit var recentsScroll: ScrollView
    private lateinit var recentList: LinearLayout
    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView

    private lateinit var detector: Detector

    // Camera preview (low-res) - keeps your existing behavior
    private val captureImage = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            try {
                imageView.setImageBitmap(bmp)
                imageView.visibility = View.VISIBLE
                showAndDetect(bmp, autoHideAfterCapture = true)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing camera bitmap", e)
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No image", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery pick: safer, uses GetContent, returns a Uri (no runtime permission required)
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        try {
            // load a memory-safe bitmap from the Uri (downsampled)
            val bmp = loadBitmapFromUri(uri, maxDimension = 1024)
            if (bmp != null) {
                imageView.setImageBitmap(bmp)
                imageView.visibility = View.VISIBLE
                showAndDetect(bmp, autoHideAfterCapture = true)
            } else {
                Toast.makeText(this, "Unable to decode selected image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load image from gallery", e)
            Toast.makeText(this, "Failed to load image: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCamera = findViewById(R.id.button)
        btnGallery = findViewById(R.id.learningButton)
        btnHistory = findViewById(R.id.btnHistory)
        btnQuickRecent = findViewById(R.id.btnQuickRecent)
        btnFavorites = findViewById(R.id.btnFavorites)
        tvRecentsTitle = findViewById(R.id.tvRecentsTitle)
        recentsScroll = findViewById(R.id.recentsScroll)
        recentList = findViewById(R.id.recentList)
        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)

        // initial state
        imageView.visibility = View.GONE
        resultTextView.visibility = View.GONE
        tvRecentsTitle.visibility = View.GONE
        recentsScroll.visibility = View.GONE

        detector = Detector(this)

        btnCamera.setOnClickListener { captureImage.launch(null) }
        btnGallery.setOnClickListener {
            // Use GetContent which opens a picker for images ("image/*")
            pickImage.launch("image/*")
        }
        btnHistory.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }
        btnQuickRecent.setOnClickListener { toggleAndLoadRecents() }
        btnFavorites.setOnClickListener { loadFavorites() }
    }

    /**
     * Loads a downsampled Bitmap from a content Uri safely to avoid OOM.
     * maxDimension is the max width or height in pixels the image will have after decoding.
     */
    private fun loadBitmapFromUri(uri: Uri, maxDimension: Int = 1024): Bitmap? {
        try {
            // 1) Decode bounds only
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            contentResolver.openInputStream(uri).use { stream ->
                if (stream == null) return null
                BitmapFactory.decodeStream(stream, null, options)
            }

            var (srcW, srcH) = options.outWidth to options.outHeight
            if (srcW <= 0 || srcH <= 0) {
                // fallback: try direct decode (rare)
                contentResolver.openInputStream(uri).use { stream ->
                    return BitmapFactory.decodeStream(stream)
                }
            }

            // 2) calculate inSampleSize (power of 2)
            var inSampleSize = 1
            while (srcW / inSampleSize > maxDimension || srcH / inSampleSize > maxDimension) {
                inSampleSize *= 2
            }

            // 3) decode with inSampleSize
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                this.inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            contentResolver.openInputStream(uri).use { stream ->
                return BitmapFactory.decodeStream(stream, null, decodeOptions)
            }
        } catch (oom: OutOfMemoryError) {
            Log.e(TAG, "OutOfMemoryError decoding image", oom)
            // try with smaller target if OOM
            try {
                val fallback = BitmapFactory.Options().apply {
                    inSampleSize = 4
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                contentResolver.openInputStream(uri).use { stream ->
                    return BitmapFactory.decodeStream(stream, null, fallback)
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Fallback decode failed", t)
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception decoding image", e)
            return null
        }
    }

    /**
     * Runs detection. If autoHideAfterCapture==true we will hide the main preview/result after a short time
     */
    private fun showAndDetect(bitmap: Bitmap, autoHideAfterCapture: Boolean) {
        resultTextView.visibility = View.VISIBLE
        resultTextView.text = "Detecting..."

        try {
            detector.detect(bitmap,
                onSuccess = { results, _ ->
                    val sb = StringBuilder()
                    results.forEach { res ->
                        sb.append("${res.label} (${(res.confidence * 100).toInt()}%)\n")
                        sb.append("Color: ${res.color}, Size: ${res.size}, Quality: ${res.quality}\n\n")
                    }

                    // Draw boxes, save annotated image & show
                    val boxed = drawBoxes(bitmap, results)
                    try {
                        ScanHistoryStore.saveScan(this, boxed, sb.toString())
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to save scan", e)
                    }

                    imageView.setImageBitmap(boxed)
                    imageView.visibility = View.VISIBLE
                    resultTextView.text = sb.toString()
                    resultTextView.visibility = View.VISIBLE

                    if (autoHideAfterCapture) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            imageView.visibility = View.GONE
                            resultTextView.visibility = View.GONE
                        }, 3500)
                    }
                },
                onError = { ex ->
                    Log.e(TAG, "Detection error", ex)
                    resultTextView.text = "❌ Detection failed: ${ex.message}"
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception running detector", e)
            resultTextView.text = "❌ Detection failed: ${e.message}"
        }
    }

    private fun drawBoxes(bmp: Bitmap, list: List<DetectionResult>): Bitmap {
        val out = bmp.copy(Bitmap.Config.ARGB_8888, true)
        val c = Canvas(out)
        val p = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
            textSize = 36f
        }
        list.forEach {
            val left = it.box.left.coerceAtLeast(0f)
            val top = it.box.top.coerceAtLeast(0f)
            val right = it.box.right.coerceAtMost(out.width.toFloat())
            val bottom = it.box.bottom.coerceAtMost(out.height.toFloat())
            c.drawRect(left, top, right, bottom, p)
            c.drawText("${it.label} ${(it.confidence * 100).toInt()}%", left, top - 8f, p)
        }
        return out
    }

    private fun toggleAndLoadRecents() {
        if (recentsScroll.visibility == View.VISIBLE) {
            recentsScroll.visibility = View.GONE
            tvRecentsTitle.visibility = View.GONE
            return
        }
        val items = ScanHistoryStore.loadAll(this).take(5)
        populateList(items, "Recent Scans")
    }

    private fun loadFavorites() {
        val items = ScanHistoryStore.loadFavorites(this)
        populateList(items, "Favorite Scans")
    }

    private fun populateList(items: List<ScanHistoryItem>, title: String) {
        recentList.removeAllViews()
        val marginPx = dp(8f)
        items.forEach { e ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(marginPx, marginPx, marginPx, marginPx)
                background = ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_rounded_card)
            }
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, marginPx, 0, 0) }
            row.layoutParams = lp

            val iv = ImageView(this).apply {
                val size = dp(56f)
                layoutParams = LinearLayout.LayoutParams(size, size)
                scaleType = ImageView.ScaleType.CENTER_CROP
                val bmp = try { BitmapFactory.decodeFile(e.imagePath) } catch (ex: Exception) { null }
                bmp?.let { setImageBitmap(it) }
            }
            val tv = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    .apply { setMargins(dp(12f), 0, 0, 0) }
                text = e.summary
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            }

            row.setOnClickListener {
                val bmp = BitmapFactory.decodeFile(e.imagePath)
                if (bmp != null) {
                    imageView.setImageBitmap(bmp)
                    imageView.visibility = View.VISIBLE
                    resultTextView.text = e.summary
                    resultTextView.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@MainActivity, "Unable to open image", Toast.LENGTH_SHORT).show()
                }
            }

            row.addView(iv)
            row.addView(tv)
            recentList.addView(row)
        }
        tvRecentsTitle.text = title
        tvRecentsTitle.visibility = View.VISIBLE
        recentsScroll.visibility = View.VISIBLE
    }

    private fun dp(value: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics
    ).toInt()
}
