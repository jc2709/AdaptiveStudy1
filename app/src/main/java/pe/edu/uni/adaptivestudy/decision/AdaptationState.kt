package pe.edu.uni.adaptivestudy.decision

enum class VisualMode { NIGHT, NORMAL, HIGH_CONTRAST }

data class AdaptationState(
    val visualMode: VisualMode,
    val ecoMode: Boolean,
    val reason: String
)
