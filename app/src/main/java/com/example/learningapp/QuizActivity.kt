package com.example.learningapp

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private lateinit var btnSubmit: Button

    private var currentIndex = 0
    private lateinit var imagePaths: List<String>
    private lateinit var labels: List<String>

    private val userAnswers = mutableMapOf<Int, String>() // questionIndex -> chosen answer
    private val questions = mutableListOf<Question>()

    data class Question(
        val imagePath: String,
        val correctAnswer: String,
        val options: List<String>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        imageView = findViewById(R.id.quizImage)
        questionText = findViewById(R.id.quizQuestion)
        optionsGroup = findViewById(R.id.optionsGroup)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        btnSubmit = findViewById(R.id.btnSubmit)

        imagePaths = intent.getStringArrayListExtra("imagePaths") ?: emptyList()
        labels = intent.getStringArrayListExtra("labels") ?: emptyList()

        // ✅ Extract only object names (first word capitalized)
        labels = labels.map { label ->
            label.split(" ")[0].replaceFirstChar { it.uppercaseChar() }
        }

        // Build questions
        buildQuestions()
        showQuestion()

        btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                saveAnswer()
                currentIndex--
                showQuestion()
            }
        }

        btnNext.setOnClickListener {
            if (currentIndex < questions.size - 1) {
                saveAnswer()
                currentIndex++
                showQuestion()
            }
        }

        btnSubmit.setOnClickListener {
            saveAnswer()
            confirmSubmission()
        }
    }

    private fun buildQuestions() {
        val allLabels = labels.toMutableList()
        for (i in labels.indices) {
            val correct = labels[i]

            // Pick 3 random wrong answers
            val wrongs = allLabels.filter { it != correct }.shuffled().take(3)

            // Merge + shuffle
            val options = (wrongs + correct).shuffled(Random(System.currentTimeMillis()))

            questions.add(
                Question(
                    imagePath = imagePaths[i],
                    correctAnswer = correct,
                    options = options
                )
            )
        }
    }

    private fun showQuestion() {
        val q = questions[currentIndex]

        // Show image
        val bmp = BitmapFactory.decodeFile(q.imagePath)
        imageView.setImageBitmap(bmp)

        // Show question text
        questionText.text = "Question ${currentIndex + 1}: What is this object?"

        // Show options
        optionsGroup.removeAllViews()
        for (option in q.options) {
            val rb = RadioButton(this)
            rb.text = option
            optionsGroup.addView(rb)

            if (userAnswers[currentIndex] == option) {
                rb.isChecked = true
            }
        }

        // Buttons enable/disable
        btnPrev.isEnabled = currentIndex > 0
        btnNext.isEnabled = currentIndex < questions.size - 1
    }

    private fun saveAnswer() {
        val selectedId = optionsGroup.checkedRadioButtonId
        if (selectedId != -1) {
            val selected = findViewById<RadioButton>(selectedId).text.toString()
            userAnswers[currentIndex] = selected
        }
    }

    private fun confirmSubmission() {
        AlertDialog.Builder(this)
            .setTitle("Submit Quiz")
            .setMessage("Are you sure you want to submit?")
            .setPositiveButton("Yes") { _, _ -> calculateScore() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun calculateScore() {
        var score = 0
        for (i in questions.indices) {
            if (userAnswers[i] == questions[i].correctAnswer) {
                score++
            }
        }
        AlertDialog.Builder(this)
            .setTitle("Result")
            .setMessage("You scored $score out of ${questions.size}")
            .setPositiveButton("OK", null)
            .show()
    }
}
