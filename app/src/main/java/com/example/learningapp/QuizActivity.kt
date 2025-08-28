package com.example.learningapp

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {
    private lateinit var imagePaths: List<String>
    private lateinit var labels: List<String>
    private lateinit var answers: Array<String?>

    private var currentIndex = 0

    private lateinit var quizImage: ImageView
    private lateinit var quizQuestion: TextView
    private lateinit var quizAnswer: EditText
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        imagePaths = intent.getStringArrayListExtra("imagePaths") ?: emptyList()
        labels = intent.getStringArrayListExtra("labels") ?: emptyList()
        answers = arrayOfNulls(labels.size)

        quizImage = findViewById(R.id.quizImage)
        quizQuestion = findViewById(R.id.quizQuestion)
        quizAnswer = findViewById(R.id.quizAnswer)
        btnPrev = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        btnSubmit = findViewById(R.id.btnSubmit)

        showQuestion()

        btnPrev.setOnClickListener {
            saveAnswer()
            if (currentIndex > 0) {
                currentIndex--
                showQuestion()
            }
        }

        btnNext.setOnClickListener {
            saveAnswer()
            if (currentIndex < labels.size - 1) {
                currentIndex++
                showQuestion()
            }
        }

        btnSubmit.setOnClickListener {
            saveAnswer()
            AlertDialog.Builder(this)
                .setTitle("Confirm Submission")
                .setMessage("Are you sure you want to submit?")
                .setPositiveButton("Yes") { _, _ -> calculateScore() }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun showQuestion() {
        val bmp = BitmapFactory.decodeFile(imagePaths[currentIndex])
        quizImage.setImageBitmap(bmp)
        quizQuestion.text = "What is this image?"
        quizAnswer.setText(answers[currentIndex] ?: "")

        btnPrev.isEnabled = currentIndex > 0
        btnNext.isEnabled = currentIndex < labels.size - 1
    }

    private fun saveAnswer() {
        answers[currentIndex] = quizAnswer.text.toString()
    }

    private fun calculateScore() {
        var score = 0
        for (i in labels.indices) {
            if (answers[i]?.trim()?.equals(labels[i], ignoreCase = true) == true) {
                score++
            }
        }
        AlertDialog.Builder(this)
            .setTitle("Quiz Result")
            .setMessage("You scored $score out of ${labels.size}")
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }
}
