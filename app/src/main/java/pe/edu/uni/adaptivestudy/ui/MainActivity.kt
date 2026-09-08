package pe.edu.uni.adaptivestudy.ui

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import pe.edu.uni.adaptivestudy.config.AdaptationRules
import pe.edu.uni.adaptivestudy.context.ContextSnapshot
import pe.edu.uni.adaptivestudy.decision.AdaptationEngine
import pe.edu.uni.adaptivestudy.decision.AdaptationState
import pe.edu.uni.adaptivestudy.decision.LayoutMode
import pe.edu.uni.adaptivestudy.decision.VisualMode
import pe.edu.uni.adaptivestudy.processing.ContextManager
import pe.edu.uni.adaptivestudy.quiz.Difficulty
import pe.edu.uni.adaptivestudy.quiz.QuizEngine

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var contextTitle: TextView
    private lateinit var contextText: TextView
    private lateinit var adaptationText: TextView
    private lateinit var difficultyText: TextView
    private lateinit var progressText: TextView
    private lateinit var questionText: TextView
    private lateinit var feedbackText: TextView
    private lateinit var tipText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var quizContainer: LinearLayout
    private lateinit var questionColumn: LinearLayout
    private lateinit var optionsColumn: LinearLayout
    private lateinit var nextButton: Button

    private val optionButtons = mutableListOf<Button>()
    private val adaptationEngine = AdaptationEngine()
    private val quizEngine = QuizEngine()
    private lateinit var contextManager: ContextManager

    private var currentSnapshot = ContextSnapshot()
    private var currentAdaptation = AdaptationState(
        visualMode = VisualMode.NORMAL,
        ecoMode = false,
        layoutMode = LayoutMode.STACKED,
        reason = "Esperando contexto..."
    )

    private var answeredCurrent = false
    private var lastSelectedIndex = -1
    private var lastCorrectIndex = -1
    private var lastAnswerCorrect = false
    private var lastExplanation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreQuizState(savedInstanceState)
        buildUi()

        contextManager = ContextManager(this) { snapshot ->
            runOnUiThread { updateContext(snapshot) }
        }

        renderQuiz()
    }

    override fun onResume() {
        super.onResume()
        contextManager.start()
    }

    override fun onPause() {
        contextManager.stop()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_DIFFICULTY, quizEngine.difficulty.name)
        outState.putInt(KEY_ANSWERED, quizEngine.answeredCount)
        outState.putInt(KEY_CORRECT, quizEngine.correctCount)
        outState.putInt(KEY_CORRECT_STREAK, quizEngine.correctStreak)
        outState.putInt(KEY_WRONG_STREAK, quizEngine.wrongStreak)
        outState.putString(KEY_ACTIVE_QUESTION, quizEngine.activeQuestionId())
        outState.putBoolean(KEY_ANSWERED_CURRENT, answeredCurrent)
        outState.putInt(KEY_SELECTED_INDEX, lastSelectedIndex)
        outState.putInt(KEY_CORRECT_INDEX, lastCorrectIndex)
        outState.putBoolean(KEY_LAST_CORRECT, lastAnswerCorrect)
        outState.putString(KEY_LAST_EXPLANATION, lastExplanation)
    }

    private fun restoreQuizState(state: Bundle?) {
        if (state == null) return

        val difficulty = runCatching {
            Difficulty.valueOf(state.getString(KEY_DIFFICULTY, Difficulty.EASY.name))
        }.getOrDefault(Difficulty.EASY)

        quizEngine.restore(
            difficulty = difficulty,
            answeredCount = state.getInt(KEY_ANSWERED, 0),
            correctCount = state.getInt(KEY_CORRECT, 0),
            correctStreak = state.getInt(KEY_CORRECT_STREAK, 0),
            wrongStreak = state.getInt(KEY_WRONG_STREAK, 0),
            activeQuestionId = state.getString(KEY_ACTIVE_QUESTION)
        )

        answeredCurrent = state.getBoolean(KEY_ANSWERED_CURRENT, false)
        lastSelectedIndex = state.getInt(KEY_SELECTED_INDEX, -1)
        lastCorrectIndex = state.getInt(KEY_CORRECT_INDEX, -1)
        lastAnswerCorrect = state.getBoolean(KEY_LAST_CORRECT, false)
        lastExplanation = state.getString(KEY_LAST_EXPLANATION, "")
    }

    private fun buildUi() {
        val scroll = ScrollView(this).apply {
            isFillViewport = true
        }

        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(20), dp(24), dp(20), dp(28))
        }

        title = text("Adaptive Study 2.0", 28f, true)
        subtitle = text("Quiz que adapta interfaz, consumo y dificultad en tiempo real", 14f)
        contextTitle = text("Contexto detectado", 18f, true)
        contextText = text("Leyendo sensores...", 15f)
        adaptationText = text("Esperando adaptación...", 14f)

        progressText = text("", 14f, true)
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = AdaptationRules.SESSION_QUESTION_COUNT
            progress = 0
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(12)
            ).apply {
                topMargin = dp(6)
                bottomMargin = dp(14)
            }
        }

        quizContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        questionColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(4), 0, dp(4))
        }
        difficultyText = text("", 14f, true)
        questionText = text("", 21f, true).apply {
            gravity = Gravity.START
        }
        feedbackText = text("", 15f).apply {
            gravity = Gravity.START
        }
        questionColumn.addView(difficultyText)
        questionColumn.addView(spacer(6))
        questionColumn.addView(questionText)
        questionColumn.addView(spacer(8))
        questionColumn.addView(feedbackText)

        optionsColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(4), 0, dp(4))
        }

        repeat(4) { index ->
            val button = Button(this).apply {
                setAllCaps(false)
                gravity = Gravity.START or Gravity.CENTER_VERTICAL
                textSize = 15f
                minHeight = dp(52)
                setPadding(dp(14), dp(8), dp(14), dp(8))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(8)
                }
                setOnClickListener { submitAnswer(index) }
            }
            optionButtons += button
            optionsColumn.addView(button)
        }

        quizContainer.addView(questionColumn)
        quizContainer.addView(optionsColumn)

        nextButton = Button(this).apply {
            setAllCaps(false)
            visibility = View.GONE
            text = "Siguiente"
            setOnClickListener {
                when {
                    answeredCurrent -> {
                        quizEngine.nextQuestion()
                        clearAnswerState()
                        renderQuiz()
                    }
                    quizEngine.isFinished() -> {
                        quizEngine.reset()
                        clearAnswerState()
                        renderQuiz()
                    }
                }
            }
        }

        tipText = text(
            "Prueba rápida: tapa el sensor de luz, gira el celular y responde varias preguntas seguidas.",
            13f
        )

        listOf(
            title,
            subtitle,
            spacer(18),
            contextTitle,
            contextText,
            adaptationText,
            spacer(18),
            progressText,
            progressBar,
            quizContainer,
            spacer(10),
            nextButton,
            spacer(16),
            tipText
        ).forEach(root::addView)

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun updateContext(snapshot: ContextSnapshot) {
        currentSnapshot = snapshot
        currentAdaptation = adaptationEngine.decide(snapshot)

        val lux = snapshot.ambientLux?.let { "%.1f lux".format(it) } ?: "no disponible"
        val orientation = if (snapshot.isLandscape) "horizontal" else "vertical"
        val charging = if (snapshot.isCharging) " · cargando" else ""

        contextText.text = "Batería ${snapshot.batteryPercent}%$charging · Luz $lux · Orientación $orientation"
        adaptationText.text = "Adaptación: ${currentAdaptation.reason} · Nivel de quiz: ${quizEngine.difficulty.label}"

        applyAdaptation(currentAdaptation)
    }

    private fun renderQuiz() {
        progressBar.progress = quizEngine.answeredCount
        progressText.text = "Progreso ${quizEngine.answeredCount}/${AdaptationRules.SESSION_QUESTION_COUNT} · " +
            "Aciertos ${quizEngine.correctCount} · ${quizEngine.accuracyPercent()}%"

        if (quizEngine.isFinished() && !answeredCurrent) {
            renderSummary()
            applyAdaptation(currentAdaptation)
            return
        }

        optionsColumn.visibility = View.VISIBLE
        val question = quizEngine.currentQuestion()
        difficultyText.text = "Dificultad: ${question.difficulty.label}"
        questionText.text = question.prompt

        question.options.forEachIndexed { index, option ->
            optionButtons[index].apply {
                text = "${letter(index)}. $option"
                isEnabled = !answeredCurrent
                alpha = if (answeredCurrent) 0.78f else 1f
            }
        }

        if (answeredCurrent) {
            renderAnsweredState()
        } else {
            feedbackText.text = ""
            nextButton.visibility = View.GONE
        }

        adaptationText.text = "Adaptación: ${currentAdaptation.reason} · Nivel de quiz: ${quizEngine.difficulty.label}"
        applyAdaptation(currentAdaptation)
    }

    private fun submitAnswer(selectedIndex: Int) {
        if (answeredCurrent || quizEngine.isFinished()) return

        val result = quizEngine.answer(selectedIndex)
        answeredCurrent = true
        lastSelectedIndex = selectedIndex
        lastCorrectIndex = result.correctIndex
        lastAnswerCorrect = result.isCorrect

        val difficultyChange = if (result.previousDifficulty != result.newDifficulty) {
            " Dificultad adaptada automáticamente: ${result.previousDifficulty.label} → ${result.newDifficulty.label}."
        } else ""

        lastExplanation = result.explanation + difficultyChange

        if (quizEngine.isFinished()) saveSessionResult()
        renderQuiz()
    }

    private fun renderAnsweredState() {
        val question = quizEngine.currentQuestion()

        optionButtons.forEachIndexed { index, button ->
            val base = "${letter(index)}. ${question.options[index]}"
            button.text = when {
                index == lastCorrectIndex -> "✓ $base"
                index == lastSelectedIndex && !lastAnswerCorrect -> "✗ $base"
                else -> base
            }
            button.isEnabled = false
            button.alpha = if (index == lastCorrectIndex || index == lastSelectedIndex) 1f else 0.62f
        }

        feedbackText.text = if (lastAnswerCorrect) {
            "Correcto. $lastExplanation"
        } else {
            "Incorrecto. $lastExplanation"
        }

        difficultyText.text = "Nivel actual: ${quizEngine.difficulty.label}"
        nextButton.visibility = View.VISIBLE
        nextButton.text = if (quizEngine.isFinished()) "Ver resultado" else "Siguiente pregunta"
    }

    private fun renderSummary() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val best = prefs.getInt(PREF_BEST, 0)
        val last = prefs.getInt(PREF_LAST, quizEngine.accuracyPercent())
        val sessions = prefs.getInt(PREF_SESSIONS, 0)

        difficultyText.text = "Sesión terminada"
        questionText.text = "Resultado: ${quizEngine.correctCount}/${AdaptationRules.SESSION_QUESTION_COUNT} (${quizEngine.accuracyPercent()}%)"
        feedbackText.text = "Último resultado: $last% · Mejor resultado: $best% · Sesiones guardadas: $sessions. " +
            "La dificultad fue cambiando según tus rachas de aciertos y errores."
        optionsColumn.visibility = View.GONE
        nextButton.visibility = View.VISIBLE
        nextButton.text = "Nueva sesión"
        tipText.text = "Tu progreso se guarda localmente en el celular; no necesita Internet."
    }

    private fun saveSessionResult() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val accuracy = quizEngine.accuracyPercent()
        val best = maxOf(prefs.getInt(PREF_BEST, 0), accuracy)
        val sessions = prefs.getInt(PREF_SESSIONS, 0) + 1

        prefs.edit()
            .putInt(PREF_BEST, best)
            .putInt(PREF_LAST, accuracy)
            .putInt(PREF_SESSIONS, sessions)
            .apply()
    }

    private fun clearAnswerState() {
        answeredCurrent = false
        lastSelectedIndex = -1
        lastCorrectIndex = -1
        lastAnswerCorrect = false
        lastExplanation = ""
    }

    private fun applyAdaptation(state: AdaptationState) {
        val background: Int
        val foreground: Int
        val secondary: Int
        val buttonBackground: Int
        val buttonForeground: Int

        when (state.visualMode) {
            VisualMode.NIGHT -> {
                background = Color.rgb(15, 23, 42)
                foreground = Color.WHITE
                secondary = Color.rgb(203, 213, 225)
                buttonBackground = Color.rgb(30, 41, 59)
                buttonForeground = Color.WHITE
            }
            VisualMode.HIGH_CONTRAST -> {
                background = Color.WHITE
                foreground = Color.BLACK
                secondary = Color.BLACK
                buttonBackground = Color.BLACK
                buttonForeground = Color.WHITE
            }
            VisualMode.NORMAL -> {
                background = Color.rgb(248, 250, 252)
                foreground = Color.rgb(15, 23, 42)
                secondary = Color.rgb(71, 85, 105)
                buttonBackground = Color.rgb(226, 232, 240)
                buttonForeground = Color.rgb(15, 23, 42)
            }
        }

        root.setBackgroundColor(background)

        listOf(title, contextTitle, progressText, difficultyText, questionText).forEach {
            it.setTextColor(foreground)
        }
        listOf(subtitle, contextText, adaptationText, feedbackText, tipText).forEach {
            it.setTextColor(secondary)
        }

        (optionButtons + nextButton).forEach { button ->
            button.backgroundTintList = ColorStateList.valueOf(buttonBackground)
            button.setTextColor(buttonForeground)
        }

        subtitle.visibility = if (state.ecoMode) View.GONE else View.VISIBLE
        tipText.visibility = if (state.ecoMode) View.GONE else View.VISIBLE
        progressBar.alpha = if (state.ecoMode) 0.75f else 1f

        applyLayoutMode(state.layoutMode)
    }

    private fun applyLayoutMode(layoutMode: LayoutMode) {
        val showSummary = quizEngine.isFinished() && !answeredCurrent
        if (showSummary || layoutMode == LayoutMode.STACKED) {
            quizContainer.orientation = LinearLayout.VERTICAL
            questionColumn.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            optionsColumn.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        } else {
            quizContainer.orientation = LinearLayout.HORIZONTAL
            questionColumn.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginEnd = dp(10)
            }
            optionsColumn.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginStart = dp(10)
            }
        }
    }

    private fun text(value: String, sizeSp: Float, bold: Boolean = false): TextView = TextView(this).apply {
        text = value
        textSize = sizeSp
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(0, dp(5), 0, dp(5))
        if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
    }

    private fun spacer(heightDp: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(heightDp))
    }

    private fun letter(index: Int): Char = ('A'.code + index).toChar()

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val PREFS_NAME = "adaptive_study_progress"
        private const val PREF_BEST = "best_score"
        private const val PREF_LAST = "last_score"
        private const val PREF_SESSIONS = "sessions"

        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_ANSWERED = "answered"
        private const val KEY_CORRECT = "correct"
        private const val KEY_CORRECT_STREAK = "correct_streak"
        private const val KEY_WRONG_STREAK = "wrong_streak"
        private const val KEY_ACTIVE_QUESTION = "active_question"
        private const val KEY_ANSWERED_CURRENT = "answered_current"
        private const val KEY_SELECTED_INDEX = "selected_index"
        private const val KEY_CORRECT_INDEX = "correct_index"
        private const val KEY_LAST_CORRECT = "last_correct"
        private const val KEY_LAST_EXPLANATION = "last_explanation"
    }
}
