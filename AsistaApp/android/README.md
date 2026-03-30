# AsistaApp Android

Frontend movil para el ecosistema AsistaApp. Desarrollado nativamente para Android utilizando Kotlin y el motor declarativo Jetpack Compose.

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![Jetpack Compose](https://img.shields.io/badge/Compose-4285F4?style=for-the-badge&logo=android&logoColor=white) ![OSMDroid](https://img.shields.io/badge/OSMs-7CBB00?style=for-the-badge&logo=openstreetmap&logoColor=white)

## Estructura de Proyecto

La arquitectura se fundamenta en un esquema Clean Architecture (Domain-Driven) acoplado con patron MVVM en la capa visual.

```text
android/app/src/main/java/com/example/asistaapp/
├── core/                  # Configuracion global, Navegacion, Retrofit y Session (SharedPrefs)
├── data/                  # Implementacion de repositorios concretos y APIs
├── domain/                # Modelos de dominio y puertos de interfaz
└── presentation/          # Capa UI (Jetpack Compose ViewModels y Screens)
    ├── home/
    ├── location/          # Mapas OSMDroid y geocercas
    ├── login/
    └── register/
```

## Dependencias Principales
* **Jetpack Compose 1.5+** (Compiler 1.5.4)
* **Kotlin 1.9.20** 
* **OSMDroid 6.x** (Renderizado libre de mapas y geocercas)
* **TensorFlow Lite Support** (Para modulos de reconocimiento facial nativo)

## Compilacion

El proyecto se encuentra independizado de claves privativas, basta con ensamblar el binario en modo Debug usando el gradle wrapper interno.

```bash
./gradlew assembleDebug --no-daemon
```
