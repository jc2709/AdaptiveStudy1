package pe.edu.uni.adaptivestudy.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import pe.edu.uni.adaptivestudy.context.ContextSnapshot
import pe.edu.uni.adaptivestudy.decision.AdaptationEngine
import pe.edu.uni.adaptivestudy.decision.AdaptationState
import pe.edu.uni.adaptivestudy.decision.VisualMode
import pe.edu.uni.adaptivestudy.processing.ContextManager

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var batteryText: TextView
    private lateinit var lightText: TextView
    private lateinit var modeText: TextView
    private lateinit var reasonText: TextView
    private lateinit var questionText: TextView
    private lateinit var tipText: TextView
    private lateinit var nextButton: Button

    private val engine = AdaptationEngine()
    private lateinit var contextManager: ContextManager
    private var questionIndex = 0

    private val questions = listOf(
        "¿Qué diferencia existe entre contexto, procesamiento, decisión y adaptación?",
        "¿Por qué cambiar el tema con un botón NO sería comportamiento adaptativo?",
        "Si la luz baja de 20 lux, ¿qué decisión toma AdaptationEngine?",
        "¿Qué componente obtiene la batería y cuál decide el modo ahorro?"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        contextManager = ContextManager(this) { snapshot ->
            runOnUiThread { update(snapshot) }
        }
        showQuestion()
    }

    override fun onResume() {
        super.onResume()
        contextManager.start()
    }

    override fun onPause() {
        contextManager.stop()
        super.onPause()
    }

    private fun buildUi() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(22), dp(28), dp(22), dp(24))
        }

        title = text("Adaptive Study", 28f, true)
        subtitle = text("Aplicación adaptativa en tiempo real", 15f)
        batteryText = text("Batería: --", 18f, true)
        lightText = text("Luz: --", 18f, true)
        modeText = text("Modo: --", 20f, true)
        reasonText = text("Esperando contexto...", 15f)
        questionText = text("", 19f, true)
        tipText = text("Tip: cubre el sensor de luz con la mano y observa el cambio automático.", 14f)

        nextButton = Button(this).apply {
            text = "Otra pregunta"
            setOnClickListener {
                questionIndex = (questionIndex + 1) % questions.size
                showQuestion()
            }
        }

        listOf(
            title, subtitle, spacer(18), batteryText, lightText, modeText, spacer(12), reasonText,
            spacer(26), questionText, spacer(14), nextButton, spacer(18), tipText
        ).forEach(root::addView)

        setContentView(root)
    }

    private fun update(snapshot: ContextSnapshot) {
        val state = engine.decide(snapshot)
        batteryText.text = "Batería: ${snapshot.batteryPercent}%${if (snapshot.isCharging) " (cargando)" else ""}"
        lightText.text = snapshot.ambientLux?.let { "Luz ambiental: %.1f lux".format(it) }
            ?: "Luz ambiental: sensor no disponible"
        modeText.text = "Modo: ${modeLabel(state)}"
        reasonText.text = state.reason
        applyAdaptation(state)
    }

    private fun applyAdaptation(state: AdaptationState) {
        val background: Int
        val foreground: Int
        val secondary: Int

        when (state.visualMode) {
            VisualMode.NIGHT -> {
                background = Color.rgb(15, 23, 42)
                foreground = Color.WHITE
                secondary = Color.rgb(203, 213, 225)
            }
            VisualMode.HIGH_CONTRAST -> {
                background = Color.WHITE
                foreground = Color.BLACK
                secondary = Color.rgb(17, 24, 39)
            }
            VisualMode.NORMAL -> {
                background = Color.rgb(248, 250, 252)
                foreground = Color.rgb(15, 23, 42)
                secondary = Color.rgb(71, 85, 105)
            }
        }

        root.setBackgroundColor(background)
        listOf(title, batteryText, lightText, modeText, questionText).forEach { it.setTextColor(foreground) }
        listOf(subtitle, reasonText, tipText).forEach { it.setTextColor(secondary) }

        // Adaptación observable por batería: en modo ahorro se oculta contenido secundario.
        tipText.visibility = if (state.ecoMode) View.GONE else View.VISIBLE
        nextButton.alpha = if (state.ecoMode) 0.82f else 1f
    }

    private fun modeLabel(state: AdaptationState): String {
        val visual = when (state.visualMode) {
            VisualMode.NIGHT -> "Nocturno"
            VisualMode.NORMAL -> "Normal"
            VisualMode.HIGH_CONTRAST -> "Alto contraste"
        }
        return if (state.ecoMode) "$visual + Ahorro" else visual
    }

    private fun showQuestion() {
        questionText.text = questions[questionIndex]
    }

    private fun text(value: String, sizeSp: Float, bold: Boolean = false): TextView = TextView(this).apply {
        text = value
        textSize = sizeSp
        gravity = Gravity.CENTER
        setPadding(0, dp(6), 0, dp(6))
        if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
    }

    private fun spacer(heightDp: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(heightDp))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
