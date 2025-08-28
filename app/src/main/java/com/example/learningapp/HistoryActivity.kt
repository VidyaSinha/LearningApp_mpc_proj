package com.example.learningapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val container: LinearLayout = findViewById(R.id.historyContainer)
        val btnStartQuiz: Button = findViewById(R.id.btnStartQuiz)

        fun refresh() {
            container.removeAllViews()
            val items = ScanHistoryStore.loadAll(this)

            items.forEach { entry ->
                val item = layoutInflater.inflate(R.layout.item_history, container, false)
                val iv = item.findViewById<ImageView>(R.id.historyImage)
                val tv = item.findViewById<TextView>(R.id.historyText)
                val btnDel = item.findViewById<Button>(R.id.btnDelete)

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

        btnStartQuiz.setOnClickListener {
            val items = ScanHistoryStore.loadAll(this)
            if (items.isEmpty()) {
                Toast.makeText(this, "No history to quiz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val imagePaths = ArrayList<String>()
            val labels = ArrayList<String>()
            items.forEach {
                imagePaths.add(it.imagePath)
                labels.add(it.summary)
            }
            val intent = Intent(this, QuizActivity::class.java)
            intent.putStringArrayListExtra("imagePaths", imagePaths)
            intent.putStringArrayListExtra("labels", labels)
            startActivity(intent)
        }
    }
}
