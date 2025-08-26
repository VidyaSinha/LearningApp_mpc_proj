package com.example.learningapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val container: LinearLayout = findViewById(R.id.historyContainer)
        fun refresh() {
            container.removeAllViews()
            val items = ScanHistoryStore.loadAll(this)
            items.forEach { entry ->
                val item = layoutInflater.inflate(R.layout.item_history, container, false)
                val iv = item.findViewById<ImageView>(R.id.historyImage)
                val tv = item.findViewById<TextView>(R.id.historyText)
                val btnDel = item.findViewById<TextView>(R.id.btnDelete)
                val bmp = BitmapFactory.decodeFile(entry.imagePath)
                iv.setImageBitmap(bmp)
                tv.text = entry.summary
                btnDel.setOnClickListener {
                    ScanHistoryStore.deleteScan(this, entry)
                    refresh()
                }
                container.addView(item)
            }
        }
        refresh()
    }
}


