# ✈️ Jetpack Compose Flight Search App

This project is part of the **Android Basics with Compose** training series,  
specifically from **Unit 6: Data Persistence** in  
**Pathway 3 – Project: Create a Flight Search App**.  

The application allows users to search for airports, mark favorite flight routes, and store flight information.

---

## 📂 Project Structure

```bash
app/src/main/
│── ui/                   # User Interface (Jetpack Compose components)
│── repository/           # Data management (Room Database & DataStore operations)
│── data/                 # Database management (Entities, DAO, Room)
│── FlightSearchApplication.kt  # Application class for dependency management
│── MainActivity.kt       # Main screen managed with Jetpack Compose
```
```

---

## 🚀 Features
- ** Airport Search**: Users can search for airports by IATA code or name.
- ** Favorite Routes**: Users can mark frequently used routes as favorites.
- ** Room Database**: Stores flight information using Room.
- ** DataStore**: Stores user data persistently.
- ** Jetpack Compose**: Built with a modern, declarative UI.
- ** StateFlow & ViewModel**: Enables reactive data management.
- ** Navigation Component**: Uses Compose Navigation for seamless screen transitions.
- ** Dependency Injection**: Centralized dependency management.

---

## 🛠 Setup

### 1️⃣ Requirements
- 📌 **Android Studio Flamingo or later**
- 📌 **Java 11 or later**
- 📌 **Gradle 8.0+**

### 2️⃣ Clone the Repository
```bash
git clone https://github.com/SezerUzunca/JetpackCompose-FlightSearch.git
cd JetpackCompose-FlightSearch
```

### 3️⃣ Install Dependencies
```bash
./gradlew build
```

### 4️⃣ Run the App
- Open the project in **Android Studio** and click **Run ▶️**.
- You can run the app on an **Android Emulator** or a **physical device**.

---

## 📚 Technologies & Dependencies

### **🔹 Android Jetpack Libraries**
- **Jetpack Compose** - Modern UI components (`androidx.compose.ui`)
- **Material 3** - Modern design components (`androidx.compose.material3`)
- **Lifecycle & ViewModel** - UI state management (`androidx.lifecycle.viewmodel.compose`)
- **Navigation Component** - Screen navigation (`androidx.navigation.compose`)
- **DataStore** - Persistent data storage (`androidx.datastore.preferences`)
- **Room Database** - SQLite-based data management (`androidx.room.runtime`)

### **🔹 Other Libraries**
- **Gradle Kotlin DSL** - Project configuration (`libs.androidx.core.ktx`)
- **Material Icons** - Material Design icons (`androidx.compose.material:material-icons-extended`)

---

## 📜 License
MIT License © 2025 [Sezer](https://github.com/SezerUzunca)

