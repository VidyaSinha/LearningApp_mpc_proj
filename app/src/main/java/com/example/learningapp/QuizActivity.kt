package com.example.learningapp

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    private lateinit var questionImage: ImageView
    private lateinit var questionText: TextView
    private lateinit var optionsGroup: RadioGroup
    private lateinit var option1: RadioButton
    private lateinit var option2: RadioButton
    private lateinit var option3: RadioButton
    private lateinit var option4: RadioButton
    private lateinit var prevBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var submitBtn: Button

    private lateinit var questions: List<Question>
    private var currentIndex = 0
    private lateinit var userAnswers: MutableList<Int?> // store selected option index (0-3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Bind views
        questionImage = findViewById(R.id.questionImage)
        questionText = findViewById(R.id.questionText)
        optionsGroup = findViewById(R.id.optionsGroup)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)
        prevBtn = findViewById(R.id.prevBtn)
        nextBtn = findViewById(R.id.nextBtn)
        submitBtn = findViewById(R.id.submitBtn)

        // Load data from intent
        val imagePaths = intent.getStringArrayListExtra("imagePaths") ?: arrayListOf()
        val labels = intent.getStringArrayListExtra("labels") ?: arrayListOf()

        // Create questions with fake 4 options (you can replace logic with real options)
        questions = labels.mapIndexed { index, label ->
            Question(
                imagePath = imagePaths[index],
                text = "What is shown in this image?",
                options = listOf(label, "Option A", "Option B", "Option C"), // label is correct
                correctIndex = 0
            )
        }

        userAnswers = MutableList(questions.size) { null }

        loadQuestion()

        prevBtn.setOnClickListener {
            if (currentIndex > 0) {
                saveSelectedAnswer()
                currentIndex--
                loadQuestion()
            }
        }

        nextBtn.setOnClickListener {
            if (currentIndex < questions.size - 1) {
                saveSelectedAnswer()
                currentIndex++
                loadQuestion()
            }
        }

        submitBtn.setOnClickListener {
            saveSelectedAnswer()
            confirmSubmit()
        }
    }

    private fun loadQuestion() {
        val q = questions[currentIndex]
        val bmp = BitmapFactory.decodeFile(q.imagePath)
        questionImage.setImageBitmap(bmp)
        questionText.text = q.text

        option1.text = q.options[0]
        option2.text = q.options[1]
        option3.text = q.options[2]
        option4.text = q.options[3]

        // Restore previous answer if any
        optionsGroup.clearCheck()
        userAnswers[currentIndex]?.let {
            when (it) {
                0 -> option1.isChecked = true
                1 -> option2.isChecked = true
                2 -> option3.isChecked = true
                3 -> option4.isChecked = true
            }
        }

        prevBtn.isEnabled = currentIndex > 0
        nextBtn.isEnabled = currentIndex < questions.size - 1
        submitBtn.visibility = if (currentIndex == questions.size - 1) Button.VISIBLE else Button.GONE
    }

    private fun saveSelectedAnswer() {
        val selectedId = optionsGroup.checkedRadioButtonId
        val answerIndex = when (selectedId) {
            R.id.option1 -> 0
            R.id.option2 -> 1
            R.id.option3 -> 2
            R.id.option4 -> 3
            else -> null
        }
        userAnswers[currentIndex] = answerIndex
    }

    private fun confirmSubmit() {
        AlertDialog.Builder(this)
            .setTitle("Submit Quiz")
            .setMessage("Are you sure you want to submit?")
            .setPositiveButton("Yes") { _, _ ->
                calculateScore()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun calculateScore() {
        var score = 0
        for (i in questions.indices) {
            if (userAnswers[i] == questions[i].correctIndex) {
                score++
            }
        }
        AlertDialog.Builder(this)
            .setTitle("Quiz Finished")
            .setMessage("You scored $score out of ${questions.size}")
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }
}

data class Question(
    val imagePath: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)
