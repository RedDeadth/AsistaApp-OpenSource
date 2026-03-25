# AsistaApp - Android Attendance System

Sistema de asistencia biométrica para entornos educativos — los usuarios se autentican, registran su ubicación y verifican su identidad mediante reconocimiento facial con TensorFlow Lite.

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)

## Software Architecture

This project follows the **MVVM + Hexagonal** pattern:

- **Domain Layer** — Pure business models and repository port interfaces (no Android dependencies)
- **Data Layer** — Retrofit adapters implementing the domain ports (`AuthRepositoryImpl`)
- **Presentation Layer** — ViewModels with `StateFlow`, Compose screens consuming UI state
- **Core** — Retrofit provider, Session management, Navigation graph

```text
app/src/main/java/com/example/asistaapp/
├── domain/
│   ├── model/                # AuthToken.kt, User.kt
│   └── port/                 # AuthRepository.kt (interface)
├── data/
│   ├── remote/
│   │   ├── dto/              # Request/Response DTOs
│   │   └── service/          # AuthApiService (Retrofit)
│   └── repository/           # AuthRepositoryImpl (port adapter)
├── core/
│   ├── network/              # RetrofitProvider (reads BuildConfig.BASE_URL)
│   ├── session/              # SessionManager (SharedPreferences)
│   └── navigation/           # AppNavigation, Routes
└── presentation/
    ├── login/                # LoginViewModel, LoginScreen
    ├── register/             # RegisterViewModel, RegisterScreen
    ├── home/                 # HomeScreen
    ├── face/                 # FaceCaptureViewModel, FaceNet screens
    ├── location/             # LocationCatchScreen
    └── theme/                # Color, Typography, Theme
```

## Environment Configuration

**The API base URL is NOT hardcoded.** Configure it via `local.properties` before building:

```bash
cp local.properties.example local.properties
```

Then open `local.properties` and set:
```properties
BASE_URL=http://YOUR_SERVER_IP:8000/api/
```

> `local.properties` is listed in `.gitignore` and will never be committed to the repository.

## Requirements
- Android Studio Hedgehog or higher
- Android SDK 35
- Backend Django server running (see `/myproject/`)

## Running the App
1. Clone the repository
2. Configure `local.properties` as described above
3. Open `AsistaApp/Asistapp02-master` in Android Studio
4. Run on emulator or physical device (Android 7.0+)
