package pe.edu.uni.adaptivestudy.decision

import pe.edu.uni.adaptivestudy.context.ContextSnapshot

class AdaptationEngine {
    fun decide(context: ContextSnapshot): AdaptationState {
        val visualMode = when {
            context.ambientLux == null -> VisualMode.NORMAL
            context.ambientLux < 20f -> VisualMode.NIGHT
            context.ambientLux > 800f -> VisualMode.HIGH_CONTRAST
            else -> VisualMode.NORMAL
        }

        val ecoMode = context.batteryPercent <= 20 && !context.isCharging

        val reasons = mutableListOf<String>()
        when (visualMode) {
            VisualMode.NIGHT -> reasons += "Poca luz: se activa el modo nocturno"
            VisualMode.HIGH_CONTRAST -> reasons += "Mucha luz: se aumenta el contraste"
            VisualMode.NORMAL -> reasons += if (context.ambientLux == null)
                "Sensor de luz no disponible: modo normal" else "Iluminación normal"
        }
        if (ecoMode) reasons += "Batería baja: se activa modo ahorro"
        if (context.isCharging) reasons += "El equipo está cargando"

        return AdaptationState(
            visualMode = visualMode,
            ecoMode = ecoMode,
            reason = reasons.joinToString(" · ")
        )
    }
}
