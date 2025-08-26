package com.example.learningapp

import Detector
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.LinearLayout
import android.widget.ScrollView
import android.view.View
import android.view.ViewGroup
import android.util.TypedValue
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var btnHistory: Button
    private lateinit var btnQuickRecent: Button
    private lateinit var tvRecentsTitle: TextView
    private lateinit var recentsScroll: ScrollView
    private lateinit var recentList: LinearLayout

    private lateinit var detector: Detector

    private val captureImage = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) showAndDetect(bmp) else {
            Toast.makeText(this, "No image", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = res.data?.data
            if (uri != null) {
                val bmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                showAndDetect(bmp)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 👇 updated IDs to match your XML
        btnCamera = findViewById(R.id.button)            // camera button
        btnGallery = findViewById(R.id.learningButton)   // gallery button
        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)
        btnHistory = findViewById(R.id.btnHistory)
        btnQuickRecent = findViewById(R.id.btnQuickRecent)
        tvRecentsTitle = findViewById(R.id.tvRecentsTitle)
        recentsScroll = findViewById(R.id.recentsScroll)
        recentList = findViewById(R.id.recentList)

        detector = Detector(this)

        btnCamera.setOnClickListener {
            captureImage.launch(null)
        }

        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        btnQuickRecent.setOnClickListener {
            toggleAndLoadRecents()
        }
    }

    private fun showAndDetect(bitmap: Bitmap) {
        imageView.visibility = ImageView.VISIBLE
        resultTextView.visibility = TextView.VISIBLE
        imageView.setImageBitmap(bitmap)
        resultTextView.text = "Detecting..."

        detector.detect(bitmap,
            onSuccess = { results, _ ->
                val sb = StringBuilder()
                results.forEach { res ->
                    sb.append("${res.label} (${(res.confidence * 100).toInt()}%)\n")
                    sb.append("Color: ${res.color}, Size: ${res.size}, Quality: ${res.quality}\n\n")
                }
                resultTextView.text = sb.toString()
                val boxed = drawBoxes(bitmap, results)
                imageView.setImageBitmap(boxed)
                // save to history
                ScanHistoryStore.saveScan(this, boxed, sb.toString())
            },
            onError = {
                resultTextView.text = "❌ Detection failed: ${it.message}"
            }
        )
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
            c.drawRect(it.box, p)
            c.drawText("${it.label} ${(it.confidence * 100).toInt()}%", it.box.left, it.box.top - 8f, p)
        }
        return out
    }

    private fun toggleAndLoadRecents() {
        if (recentsScroll.visibility == View.VISIBLE) {
            recentsScroll.visibility = View.GONE
            tvRecentsTitle.visibility = View.GONE
            return
        }

        // Load top 5 recents
        val items = ScanHistoryStore.loadAll(this).take(5)
        recentList.removeAllViews()
        val marginPx = dp(8f)
        items.forEach { e ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(marginPx, marginPx, marginPx, marginPx)
                background = resources.getDrawable(R.drawable.bg_rounded_card, theme)
            }
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, marginPx, 0, 0)
            }
            row.layoutParams = lp

            val iv = ImageView(this).apply {
                val size = dp(56f)
                layoutParams = LinearLayout.LayoutParams(size, size)
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageBitmap(android.graphics.BitmapFactory.decodeFile(e.imagePath))
            }
            val tv = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                    setMargins(dp(12f), 0, 0, 0)
                }
                text = e.summary
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            }
            row.addView(iv)
            row.addView(tv)
            recentList.addView(row)
        }

        tvRecentsTitle.visibility = View.VISIBLE
        recentsScroll.visibility = View.VISIBLE
    }

    private fun dp(value: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics
    ).toInt()
}
