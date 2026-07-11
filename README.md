# 🛡️ PokeBattle Simulator

A modern, high-performance Android application that simulates Pokémon battles using the official PokeAPI. Built with **Jetpack Compose** and **Clean Architecture**, this project demonstrates advanced UI/UX patterns, complex state management, and robust testing.

## 🚀 Key Features

*   **Dynamic Team Management**: Browse a list of 251 Pokémon (Gen 1 & 2) and build your ultimate team of three.
*   **Advanced Reordering UX**: Features a smooth **"Live Swap" Drag-and-Drop** system for arranging team order before battle.
*   **Strategic Battle Hints**: Real-time **Type Effectiveness indicators** (Advantage/Disadvantage arrows) that update instantly as you reorder your team.
*   **Battle Engine**: A custom simulation logic including:
    *   Damage calculation based on Attack/Defense stats.
    *   Double-type effectiveness multipliers.
    *   Speed-based **Dodge mechanics**.
    *   Turn-based auto-battle with a detailed log.
*   **Offline Support**: Local caching using **Room Database** for seamless performance.

## 🏗️ Architecture

The project follows **Clean Architecture** principles to ensure scalability and maintainability:

*   **Domain Layer**: Pure business logic, including Use Cases, Repository interfaces, and the core Battle Engine.
*   **Data Layer**: Data sources (Retrofit for PokeAPI, Room for local storage) and Repository implementations.
*   **Presentation Layer**: MVVM pattern with **Unidirectional Data Flow (UDF)** using StateFlow and Jetpack Compose.

## 🛠️ Tech Stack

*   **Language**: Kotlin
*   **UI**: Jetpack Compose (Material 3)
*   **DI**: Hilt
*   **Async**: Coroutines & Flow
*   **Networking**: Retrofit & Kotlinx Serialization
*   **Local DB**: Room
*   **Image Loading**: Coil 3
*   **Preferences**: DataStore
*   **Testing**: JUnit 4, MockK, Coroutines-Test

## 🧪 Testing

Quality is a core focus of this project. It includes a comprehensive test suite covering different layers:

*   **Domain**: `TypeEffectivenessTest` verifies the accuracy of the type-advantage matrix.
*   **Use Cases**: `ExecuteTurnUseCaseTest` covers the complex battle logic, damage math, and fainted states.
*   **UI Layer**: `BattleViewModelTest` ensures reliable state transitions and asynchronous event handling using `MockK`.

## 🎨 UI/UX Highlights

*   **Custom Theming**: Integrated brand-accurate fonts (Ketchum, Pokemon Solid).
*   **Smooth Animations**: Used `animateIntOffsetAsState` and `zIndex` for a premium feel during team selection.
*   **Responsive Design**: Layouts optimized for different screen densities using `LocalDensity`.

## 📸 Screenshots

| Main Screen | PokeList | Team Selection | Battle |
| :---: | :---: | :---: | :---: |
| [Placeholder] | [Placeholder] | [Placeholder] | [Placeholder] |

*(Note: Add your actual screenshots or GIFs of the drag-and-drop animation here!)*

## ⚙️ Installation

1. Clone the repository:
2. Open the project in **Android Studio Ladybug** or newer.
3. Sync Gradle and run the `app` module.

---
   
