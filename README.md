# Adaptive Study 2.0 — Taller 1

Aplicación Android nativa en Kotlin que demuestra comportamiento adaptativo mediante sensores del dispositivo y la interacción del usuario.

## Función principal

La app ahora incluye un **quiz real de 10 preguntas** con cuatro alternativas, validación inmediata, explicación, puntaje, progreso y almacenamiento local del mejor resultado.

La dificultad también se adapta automáticamente:

- inicia en **Fácil**;
- 2 aciertos consecutivos → sube un nivel;
- 2 errores consecutivos → baja un nivel cuando sea posible.

## Contextos detectados y adaptaciones

### 1. Luminosidad ambiental

- menos de 20 lux → modo nocturno;
- entre 20 y 800 lux → modo normal;
- más de 800 lux → alto contraste.

### 2. Batería

- 20% o menos y sin cargar → modo ahorro;
- en ahorro se oculta contenido secundario y el refresco de contexto baja de 15 s a 45 s;
- al cargar el dispositivo, el modo ahorro se desactiva.

### 3. Orientación

- vertical → quiz apilado;
- horizontal → pregunta y alternativas se distribuyen en dos columnas.

### 4. Interacción del usuario

- racha de aciertos → aumenta dificultad;
- racha de errores → reduce dificultad.

## Pipeline

`CONTEXTO → PROCESAMIENTO → DECISIÓN → ADAPTACIÓN`

- Captura: `context/BatteryContextProvider.kt`, `context/LightContextProvider.kt`
- Procesamiento: `processing/ContextManager.kt`
- Reglas: `config/AdaptationRules.kt`
- Decisión ambiental: `decision/AdaptationEngine.kt`
- Decisión de aprendizaje: `quiz/QuizEngine.kt`
- Adaptación observable: `ui/MainActivity.kt`

## Demo recomendada

1. Abrir la app y responder preguntas.
2. Acertar dos seguidas para observar el cambio de dificultad.
3. Tapar el sensor de luz para activar el modo nocturno automáticamente.
4. Girar el celular para mostrar la reorganización automática de la interfaz.
5. Si la batería está baja, mostrar el modo ahorro; conectar el cargador para demostrar el cambio de contexto.

## Ejecutar

1. Abrir el proyecto en Android Studio.
2. Esperar la sincronización de Gradle.
3. Conectar un Android físico o usar emulador.
4. Ejecutar `app`.
5. Para APK: **Build → Build APK(s)**.

También existe `.github/workflows/build-apk.yml`, que compila automáticamente un APK debug y lo publica como artifact `AdaptiveStudy-debug-apk`.

## Requisitos

- Android SDK 35
- JDK 17
- minSdk 23

## Reto técnico rápido

Los parámetros de adaptación están centralizados en `AdaptationRules.kt`. Ejemplos:

- cambiar batería baja de 20% a 30%;
- cambiar modo nocturno de 20 lux a 50 lux;
- cambiar la racha requerida para modificar dificultad;
- modificar la frecuencia de actualización en modo ahorro.
