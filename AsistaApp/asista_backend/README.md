# AsistaApp Backend - Django REST API

Attendance management API for the AsistaApp ecosystem — handles user authentication, session tokens, and biometric attendance registration.

[![Python](https://img.shields.io/badge/Python-3.11-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![Django](https://img.shields.io/badge/Django-5.x-092E20?style=for-the-badge&logo=django&logoColor=white)](https://www.djangoproject.com/)
[![DRF](https://img.shields.io/badge/Django_REST_Framework-red?style=for-the-badge)](https://www.django-rest-framework.org/)

## Software Architecture

This project follows the **MVC + Hexagonal** pattern:

- **Domain Layer** — Pure Python dataclass entities and abstract port interfaces (no Django imports)
- **Application Layer** — Use Cases orchestrating business logic (RegisterUser, LoginUser, RegisterAttendance, ListAttendance)
- **Infrastructure Layer** — Django ORM adapters implementing the domain ports
- **Presentation Layer** — DRF APIView controllers that are thin orchestrators (no business logic)

```text
myapp/
├── domain/
│   ├── entities/entities.py    # UserEntity, AttendanceEntity (dataclasses)
│   └── ports/repository_ports.py  # UserRepositoryPort, AttendanceRepositoryPort (ABC)
├── application/
│   └── use_cases/
│       ├── auth_use_cases.py       # RegisterUserUseCase, LoginUserUseCase
│       └── attendance_use_cases.py # RegisterAttendanceUseCase, ListAttendanceUseCase
├── infrastructure/
│   └── repositories/
│       ├── user_repository.py      # UserRepository (Django ORM adapter)
│       └── attendance_repository.py
├── presentation/
│   └── controllers/controllers.py  # RegisterController, LoginController, AttendanceController
├── models.py    # Django ORM models (User, Attendance)
└── urls.py      # URL dispatch to controllers
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register/` | Register new user |
| POST | `/api/users/login/` | Login and receive JWT token |
| POST | `/api/register_attendance/` | Register attendance record |
| GET | `/api/register_attendance/?username=X` | List attendance by user |

## Environment Configuration

**No credentials are hardcoded.** Configure environment variables before running:

```bash
cp .env.example .env
```

Set your values in `.env`:
```env
DJANGO_SECRET_KEY=your-long-random-secret
JWT_SECRET_KEY=your-jwt-secret
DB_NAME=AsistaApp
DB_USER=root
DB_PASSWORD=yourpassword
ALLOWED_HOSTS=127.0.0.1,localhost,YOUR_ANDROID_DEVICE_IP
```

## Running the Server

```bash
# Install dependencies
pip install -r requirements.txt

# Apply migrations
python manage.py migrate

# Start development server (accessible from Android device)
python manage.py runserver 0.0.0.0:8000
```
