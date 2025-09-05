# PromptIQ – Teleprompter Inteligente

Este repositorio contiene el código fuente del **Trabajo de Fin de Grado** realizado por  
**Francisco de Borja Gámiz Marcos**:  
**PromptIQ: Teleprompter Inteligente en Android**.

## 📌 Descripción
PromptIQ es una aplicación Android que funciona como teleprompter en dos modos:
- **Modo clásico** → desplazamiento fijo del texto.
- **Modo inteligente** → ajusta el scroll en tiempo real según la velocidad de dicción detectada y solo avanza cuando el usuario mira a la pantalla (detección de mirada con ML Kit).

Además, permite:
- Crear, editar e importar guiones en diferentes formatos (`.txt`, `.docx`, `.pdf`).
- Personalizar preferencias de lectura (fuente, color, velocidad).
- Gestionar múltiples usuarios con login y persistencia de datos mediante Room.

## 🏗️ Estructura del proyecto
- `/app` → código fuente en Kotlin.
- `/app/src/main/java/...` → clases de la aplicación.
- `/app/src/main/res` → recursos (layouts, colores, strings).
- `build.gradle` → configuración del proyecto.

## 🛠️ Tecnologías usadas
- **Lenguaje**: Kotlin
- **IDE**: Android Studio
- **Persistencia**: Room Database, SharedPreferences
- **Reconocimiento de voz**: SpeechRecognizer API
- **Detección de mirada**: ML Kit (Google Vision)
- **Diseño**: Material Design + Jetpack Compose (según pantallas)

## 📲 Requisitos
- Android Studio (versión recomendada: Giraffe o superior).
- Android SDK mínimo: **API 26 (Android 8.0 Oreo)**.
- Emulador o dispositivo físico con Android >= 8.0.

## ▶️ Ejecución
1. Clonar este repositorio.
2. Abrir el proyecto en Android Studio.
3. Sincronizar dependencias con Gradle.
4. Ejecutar en un emulador o dispositivo físico.

## ✅ Estado actual
- Todas las funcionalidades principales implementadas.
- Validación mediante pruebas funcionales y testeo con usuario experto.
- Plan de negocio complementario incluido en la memoria del TFG.

## 📄 Licencia
Proyecto desarrollado exclusivamente con fines académicos para la Universidad de Granada.
