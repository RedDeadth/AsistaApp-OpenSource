# AsistaApp Backend API

> Sistema centralizado y robusto de autenticación y control de asistencias para el ecosistema AsistaApp.

![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white) 
![Django](https://img.shields.io/badge/Django-092E20?style=for-the-badge&logo=django&logoColor=white) 
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white) 
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

## Arquitectura del Sistema

Implementación estricta de **Arquitectura Hexagonal (Ports and Adapters)** diseñada para facilitar el mantenimiento y la escalabilidad de la lógica de negocio sin depender directamente de los frameworks base.

```text
backend/
├── config/                  
│   └── settings.py         (Configuración de Entorno & WSGI)
└── api/                    
    ├── domain/             (Capa Interna: Puertos, Interfaces y Entidades abstractas)
    ├── application/        (Reglas: Casos de Uso del negocio como auth y asistencia)
    ├── infrastructure/     (Modelos de Django, Adapters ORM y Repositorios Concretos)
    └── presentation/       (Controladores: Vistas y API Endpoints HTTP REST)
```

## Instalación y Despliegue

El entorno base opera gracias a XAMPP MySQL. Se encuentra configurado con parches activos de retro-compatibilidad para conectar exitosamente con el motor de base de datos MaríaDB.

```bash
# 1. Crear entorno virtual
python -m venv venv
source venv/bin/activate

# 2. Re-generar cache de dependencias (si es necesario)
pip install -r requirements.txt

# 3. Aplicar esquemas de Base de Datos
DATABASE_URL="" python manage.py migrate

# 4. Lanzar servidor de desarrollo WSGI
DATABASE_URL="" python manage.py runserver
```
