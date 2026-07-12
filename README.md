# 🛡️ PokeBattle Simulator

A modern Android application that uses the official PokeAPI to build and simulate Pokémon battles. Built with Jetpack Compose and Clean Architecture, it includes complex state management and comprehensive unit testing.

The goal of this project was to practice modern Android development by building a complete application with a scalable architecture and realistic business logic.

## 🚀 Key Features

*   **Dynamic Team Management**: Browse a list of 251 Pokémon (Gen 1 & 2) and build your ultimate team of three.
*   **Reordering UX**: Features a smooth **"Live Swap" Drag-and-Drop** system for arranging team order before battle.
*   **Strategic Battle Hints**: Real-time **Type Effectiveness indicators** (Advantage/Disadvantage arrows) that update instantly as you reorder your team.
*   **Battle Engine**: A custom simulation logic including:
    *   Damage calculation based on Attack/Defense stats.
    *   Double-type effectiveness multipliers.
    *   Speed-based **Dodge mechanics**.
    *   Turn-based auto-battle with a detailed log.
*   **Offline Support**: Local caching using **Room Database** for seamless performance.

## 🏗️ Architecture

The application follows Clean Architecture with a clear separation of Presentation, Domain and Data layers:

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

The project includes unit tests covering the battle engine, ViewModels and business logic:

*   **Domain**: `TypeEffectivenessTest` verifies the accuracy of the type-advantage matrix.
*   **Use Cases**: `ExecuteTurnUseCaseTest` covers the complex battle logic, damage math, and fainted states.
*   **UI Layer**: `BattleViewModelTest` ensures reliable state transitions and asynchronous event handling using `MockK`.

## 🎨 UI/UX Highlights

*   **Custom Pokémon fonts**: Integrated brand-accurate fonts (Ketchum, Pokemon Solid).
*   **Smooth Animations**: Drag-and-drop animations using animateIntOffsetAsState and zIndex.
*   **Responsive Design**: Layouts optimized for different screen densities using `LocalDensity`.

## 📸 Screenshots

| Main Screen | PokeList | Team Selection | Battle |
| :---: | :---: | :---: | :---: |
| <img width="576" height="1280" alt="1" src="https://github.com/user-attachments/assets/7f1361cb-7a2d-4914-bd20-403beec26a36" /> | <img width="576" height="1280" alt="2" src="https://github.com/user-attachments/assets/d78d5a45-c96a-4a38-8a04-4d95406ca5f1" /> | <img width="576" height="1280" alt="3" src="https://github.com/user-attachments/assets/12d32081-3300-4514-8132-842e6148d667" /> | <img width="576" height="1280" alt="4" src="https://github.com/user-attachments/assets/591ec207-1dde-496a-9ab3-e682ff612337" /> |

## 🎮 Demo

| Smooth Drag & Drop & Live Hints |
| :---: |
| <video src="https://github.com/user-attachments/assets/8f1f2c8d-6a0e-484a-b1a8-6a3bc3aa9fcb" width="300" autoplay loop muted playsinline></video> |

---

## ⚙️ Installation

1. Clone the repository:
```bash
git clone https://github.com/MuneCat9/pokemon-autobattler.git
```
2. Open the project in Android Studio Ladybug or newer.
3. Sync Gradle and run the `app` module.

---
   
