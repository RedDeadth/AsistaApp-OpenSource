Aplicación de Asistencias
myproject = django server + api drf + MySQL
Asisitapp02-master = Android Applicaction + Retrofit

Para la configuración de Ips en django son necesarios relizar cambios 
En settings.py del proyecto Django

#CAMBIAR LA SEGUNDA IP PARA HACER FUNCIONAR LE PROGRAMA CON EL DISPOSITIVO ANDROID 1
ALLOWED_HOSTS = ['127.0.0.1','192.168.18.28']

#CAMBIAR LA SEGUNDA IP PARA HACER FUNCIONAR LE PROGRAMA CON EL DISPOSITIVO ANDROID 1
CORS_ALLOWED_ORIGINS = [
    'http://127.0.0.1:8000',
    'http://192.168.18.28:8000',
]

En Android Studio Kotlin

En RetrofitInstance
object RetrofitInstance {
    private const val BASE_URL = "http://192.168.28.18:8000/api/"

en network_security_config.xml
<domain includeSubdomains="true">192.168.18.28</domain>

crear la base base de datos de nombre: asistaapp

SENTENCIA SQL PARA EL MYSQL DE XAMPP 

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(150) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL,
    usertype VARCHAR(20) DEFAULT 'user'
) ENGINE=InnoDB;

CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(150),
    fecha_registro DATE NOT NULL,
    hora_registro TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
) ENGINE=InnoDB;

Ejecutar el siguiente comando en el servidor Django:
python manage.py runserver 192.168.18.28:8000 

Consideraciones:
Solo cambiar la dirección ip, no cambiar ni eliminar términos tales como: "http", "api", ":800", para el correcto funcionamiento de retrofit en e proyecto Android