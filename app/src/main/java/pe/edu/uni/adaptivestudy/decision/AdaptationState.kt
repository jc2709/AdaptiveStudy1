package pe.edu.uni.adaptivestudy.decision

enum class VisualMode { NIGHT, NORMAL, HIGH_CONTRAST }
enum class LayoutMode { STACKED, SPLIT }

data class AdaptationState(
    val visualMode: VisualMode,
    val ecoMode: Boolean,
    val layoutMode: LayoutMode,
    val reason: String
)
