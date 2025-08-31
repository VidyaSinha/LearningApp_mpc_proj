package com.example.learningapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var btnStartQuiz: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.historyRecycler)
        btnStartQuiz = findViewById(R.id.btnStartQuiz)

        adapter = HistoryAdapter(
            onDeleteClick = { item ->
                ScanHistoryStore.deleteScan(this, item.id)
                adapter.submitList(ScanHistoryStore.loadAll(this))
            },
            onFavoriteClick = { item ->
                ScanHistoryStore.toggleFavoriteStatus(this, item.id)
                adapter.submitList(ScanHistoryStore.loadAll(this))
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load data initially
        adapter.submitList(ScanHistoryStore.loadAll(this))

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
