package pe.edu.uni.adaptivestudy.quiz

import pe.edu.uni.adaptivestudy.config.AdaptationRules

class QuizEngine {
    private val questions = listOf(
        QuizQuestion(
            id = "e1",
            difficulty = Difficulty.EASY,
            prompt = "¿Qué comando crea una nueva rama en Git?",
            options = listOf("git branch nueva", "git push nueva", "git tag nueva", "git clone nueva"),
            correctIndex = 0,
            explanation = "git branch <nombre> crea una rama local nueva."
        ),
        QuizQuestion(
            id = "e2",
            difficulty = Difficulty.EASY,
            prompt = "¿Qué representa una clave primaria en una tabla?",
            options = listOf("Un valor repetido", "Un identificador único", "Una copia de seguridad", "Un índice temporal"),
            correctIndex = 1,
            explanation = "La clave primaria identifica de forma única cada fila de una tabla."
        ),
        QuizQuestion(
            id = "e3",
            difficulty = Difficulty.EASY,
            prompt = "¿Qué significa HTTP en una aplicación web?",
            options = listOf("Un lenguaje de programación", "Un protocolo de transferencia", "Una base de datos", "Un sistema operativo"),
            correctIndex = 1,
            explanation = "HTTP es un protocolo usado para intercambiar recursos entre cliente y servidor."
        ),
        QuizQuestion(
            id = "e4",
            difficulty = Difficulty.EASY,
            prompt = "¿Qué estructura permite repetir instrucciones mientras se cumpla una condición?",
            options = listOf("if", "class", "while", "return"),
            correctIndex = 2,
            explanation = "while repite un bloque mientras su condición sea verdadera."
        ),
        QuizQuestion(
            id = "e5",
            difficulty = Difficulty.EASY,
            prompt = "¿Cuál es la función principal de un README en GitHub?",
            options = listOf("Compilar el proyecto", "Documentar el proyecto", "Crear ramas", "Borrar commits"),
            correctIndex = 1,
            explanation = "El README explica qué hace el proyecto, cómo usarlo y otra información importante."
        ),
        QuizQuestion(
            id = "m1",
            difficulty = Difficulty.MEDIUM,
            prompt = "¿Qué ventaja principal ofrece una transacción en una base de datos?",
            options = listOf("Evita usar SQL", "Agrupa operaciones como una unidad", "Elimina claves foráneas", "Convierte tablas en archivos"),
            correctIndex = 1,
            explanation = "Una transacción permite confirmar o revertir un conjunto de operaciones como una sola unidad lógica."
        ),
        QuizQuestion(
            id = "m2",
            difficulty = Difficulty.MEDIUM,
            prompt = "¿Qué ocurre normalmente al hacer merge de una rama sin conflictos?",
            options = listOf("Se combinan los cambios", "Se elimina el repositorio", "Se borra main", "Se crea un tag automáticamente"),
            correctIndex = 0,
            explanation = "Git integra los cambios de ambas ramas cuando no hay conflictos pendientes."
        ),
        QuizQuestion(
            id = "m3",
            difficulty = Difficulty.MEDIUM,
            prompt = "¿Para qué sirve una API REST en una arquitectura cliente-servidor?",
            options = listOf("Para reemplazar la red", "Para definir una forma de comunicación", "Para compilar Kotlin", "Para crear sensores físicos"),
            correctIndex = 1,
            explanation = "Una API REST expone recursos y operaciones mediante una interfaz HTTP bien definida."
        ),
        QuizQuestion(
            id = "m4",
            difficulty = Difficulty.MEDIUM,
            prompt = "¿Qué busca reducir la normalización en bases de datos?",
            options = listOf("La redundancia innecesaria", "La cantidad de usuarios", "El número de consultas", "La seguridad"),
            correctIndex = 0,
            explanation = "La normalización organiza los datos para reducir redundancia y anomalías de actualización."
        ),
        QuizQuestion(
            id = "m5",
            difficulty = Difficulty.MEDIUM,
            prompt = "¿Qué componente de una arquitectura MVC procesa normalmente la lógica de interacción?",
            options = listOf("Modelo", "Vista", "Controlador", "Base de datos"),
            correctIndex = 2,
            explanation = "El controlador recibe acciones y coordina la interacción entre vista y modelo."
        ),
        QuizQuestion(
            id = "h1",
            difficulty = Difficulty.HARD,
            prompt = "¿Qué propiedad ACID garantiza que una transacción se complete totalmente o no se aplique?",
            options = listOf("Consistencia", "Aislamiento", "Atomicidad", "Durabilidad"),
            correctIndex = 2,
            explanation = "Atomicidad significa todo o nada: si una parte falla, la transacción puede revertirse."
        ),
        QuizQuestion(
            id = "h2",
            difficulty = Difficulty.HARD,
            prompt = "¿Cuál es una ventaja típica de separar captura de contexto y lógica de decisión?",
            options = listOf("Aumenta el acoplamiento", "Facilita mantenimiento y pruebas", "Obliga a usar Internet", "Evita usar sensores"),
            correctIndex = 1,
            explanation = "Separar responsabilidades reduce acoplamiento y permite probar cada componente de forma independiente."
        ),
        QuizQuestion(
            id = "h3",
            difficulty = Difficulty.HARD,
            prompt = "En Git, ¿qué hace git merge --abort durante un merge conflictivo aún no confirmado?",
            options = listOf("Publica el merge", "Cancela el merge en curso", "Borra el repositorio remoto", "Crea un release"),
            correctIndex = 1,
            explanation = "git merge --abort intenta volver al estado previo al merge que estaba en curso."
        ),
        QuizQuestion(
            id = "h4",
            difficulty = Difficulty.HARD,
            prompt = "¿Qué problema ayuda a evitar la histéresis en una regla basada en sensores?",
            options = listOf("Cambios repetidos cerca del umbral", "Pérdida de Internet", "Errores de sintaxis", "Duplicación de tablas"),
            correctIndex = 0,
            explanation = "La histéresis evita que el sistema cambie de estado constantemente cuando el valor oscila cerca de un límite."
        ),
        QuizQuestion(
            id = "h5",
            difficulty = Difficulty.HARD,
            prompt = "¿Qué beneficio tiene usar una capa de decisión separada de la interfaz?",
            options = listOf("La UI decide todo", "La lógica puede reutilizarse y probarse", "El código deja de compilar", "Se elimina el contexto"),
            correctIndex = 1,
            explanation = "La lógica de decisión queda independiente de cómo se muestre la interfaz, mejorando reutilización y pruebas."
        )
    )

    var difficulty: Difficulty = Difficulty.EASY
        private set
    var answeredCount: Int = 0
        private set
    var correctCount: Int = 0
        private set
    var correctStreak: Int = 0
        private set
    var wrongStreak: Int = 0
        private set

    private var activeQuestionId: String? = null

    fun currentQuestion(): QuizQuestion {
        activeQuestionId?.let { id ->
            questions.firstOrNull { it.id == id }?.let { return it }
        }

        val pool = questions.filter { it.difficulty == difficulty }
        val question = pool[answeredCount % pool.size]
        activeQuestionId = question.id
        return question
    }

    fun answer(selectedIndex: Int): AnswerResult {
        val question = currentQuestion()
        val previousDifficulty = difficulty
        val isCorrect = selectedIndex == question.correctIndex

        answeredCount++
        if (isCorrect) {
            correctCount++
            correctStreak++
            wrongStreak = 0
        } else {
            wrongStreak++
            correctStreak = 0
        }

        if (correctStreak >= AdaptationRules.STREAK_FOR_LEVEL_CHANGE) {
            difficulty = harder(difficulty)
            correctStreak = 0
            wrongStreak = 0
        } else if (wrongStreak >= AdaptationRules.STREAK_FOR_LEVEL_CHANGE) {
            difficulty = easier(difficulty)
            correctStreak = 0
            wrongStreak = 0
        }

        return AnswerResult(
            isCorrect = isCorrect,
            correctIndex = question.correctIndex,
            explanation = question.explanation,
            previousDifficulty = previousDifficulty,
            newDifficulty = difficulty
        )
    }

    fun nextQuestion() {
        activeQuestionId = null
    }

    fun isFinished(): Boolean = answeredCount >= AdaptationRules.SESSION_QUESTION_COUNT

    fun accuracyPercent(): Int = if (answeredCount == 0) 0 else (correctCount * 100) / answeredCount

    fun activeQuestionId(): String? = activeQuestionId

    fun reset() {
        difficulty = Difficulty.EASY
        answeredCount = 0
        correctCount = 0
        correctStreak = 0
        wrongStreak = 0
        activeQuestionId = null
    }

    fun restore(
        difficulty: Difficulty,
        answeredCount: Int,
        correctCount: Int,
        correctStreak: Int,
        wrongStreak: Int,
        activeQuestionId: String?
    ) {
        this.difficulty = difficulty
        this.answeredCount = answeredCount.coerceIn(0, AdaptationRules.SESSION_QUESTION_COUNT)
        this.correctCount = correctCount.coerceIn(0, this.answeredCount)
        this.correctStreak = correctStreak.coerceAtLeast(0)
        this.wrongStreak = wrongStreak.coerceAtLeast(0)
        this.activeQuestionId = activeQuestionId
    }

    private fun harder(value: Difficulty): Difficulty = when (value) {
        Difficulty.EASY -> Difficulty.MEDIUM
        Difficulty.MEDIUM -> Difficulty.HARD
        Difficulty.HARD -> Difficulty.HARD
    }

    private fun easier(value: Difficulty): Difficulty = when (value) {
        Difficulty.EASY -> Difficulty.EASY
        Difficulty.MEDIUM -> Difficulty.EASY
        Difficulty.HARD -> Difficulty.MEDIUM
    }
}
