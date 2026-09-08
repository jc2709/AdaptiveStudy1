package pe.edu.uni.adaptivestudy.quiz

enum class Difficulty(val label: String) {
    EASY("Fácil"),
    MEDIUM("Intermedio"),
    HARD("Difícil")
}

data class QuizQuestion(
    val id: String,
    val difficulty: Difficulty,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class AnswerResult(
    val isCorrect: Boolean,
    val correctIndex: Int,
    val explanation: String,
    val previousDifficulty: Difficulty,
    val newDifficulty: Difficulty
)
