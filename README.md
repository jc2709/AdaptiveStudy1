# Adaptive Study — Taller 1

Aplicación Android nativa desarrollada en Kotlin para demostrar comportamiento adaptativo en tiempo real.

## Qué detecta

1. **Luminosidad ambiental (sensor de luz)**
   - Menos de 20 lux → modo nocturno.
   - Entre 20 y 800 lux → modo normal.
   - Más de 800 lux → alto contraste.

2. **Batería**
   - 20% o menos y sin cargar → modo ahorro.
   - En modo ahorro se oculta contenido secundario y se reduce el énfasis visual.

## Pipeline

`CONTEXTO → PROCESAMIENTO → DECISIÓN → ADAPTACIÓN`

- Contexto: `BatteryContextProvider.kt`, `LightContextProvider.kt`
- Procesamiento: `ContextManager.kt`
- Decisión: `AdaptationEngine.kt`
- Adaptación/interfaz: `MainActivity.kt`

## Ejecutar en Android Studio

1. Abrir la carpeta del proyecto `AdaptiveStudy`.
2. Esperar a que Gradle sincronice.
3. Conectar un Android físico con depuración USB o usar un emulador.
4. Ejecutar `app`.
5. Para generar APK: **Build → Build APK(s)**.

> Recomendación para la demo: usar un celular físico porque el sensor de luz se demuestra mejor cubriéndolo con la mano o acercándolo a una fuente de luz.

## Compilar con GitHub Actions

El repositorio incluye `.github/workflows/build-apk.yml`. Al subir el proyecto a GitHub, el workflow compila un APK debug y lo publica como artifact llamado `AdaptiveStudy-debug-apk`.

## Requisitos

- Android Studio reciente
- Android SDK 35
- JDK 17

## Reto técnico rápido

Los umbrales están centralizados en `AdaptationEngine.kt`. Ejemplos de cambios sencillos durante la sustentación:

- Cambiar batería baja de 20% a 30%.
- Cambiar modo nocturno de 20 lux a 50 lux.
- Añadir una nueva regla para luz intensa.
