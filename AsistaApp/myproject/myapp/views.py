from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import User, Attendance
from django.contrib.auth.hashers import make_password, check_password
from django.utils import timezone
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import User
from django.contrib.auth.hashers import check_password
import jwt
from datetime import datetime, timedelta

class RegisterView(APIView):
    def post(self, request):
        try:
            username = request.data.get('username')
            email = request.data.get('email')
            password = request.data.get('password')

            if not all([username, email, password]):
                return Response({
                    'error': 'Todos los campos son requeridos'
                }, status=status.HTTP_400_BAD_REQUEST)

            if User.objects.filter(username=username).exists():
                return Response({
                    'error': 'El usuario ya existe'
                }, status=status.HTTP_400_BAD_REQUEST)

            user = User.objects.create(
                username=username,
                email=email,
                password=make_password(password),
                usertype='user'
            )

            return Response({
                'message': 'Usuario registrado exitosamente',
                'data': {
                    'id': user.id,
                    'username': user.username,
                    'email': user.email,
                    'usertype': user.usertype
                }
            }, status=status.HTTP_201_CREATED)

        except Exception as e:
            return Response({
                'error': str(e)
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)



class LoginView(APIView):
    def post(self, request):
        username = request.data.get('username')
        password = request.data.get('password')

        try:
            user = User.objects.get(username=username)
            if check_password(password, user.password):
                # Generar token JWT
                token = jwt.encode({
                    'username': user.username,
                    'email': user.email,
                    'usertype': user.usertype,
                    'exp': datetime.utcnow() + timedelta(days=1)  # Token expira en 1 día
                }, 'tu_clave_secreta', algorithm='HS256')

                return Response({
                    'status': 'success',
                    'data': {
                        'id': user.id,
                        'username': user.username,
                        'email': user.email,
                        'usertype': user.usertype
                    },
                    'access': token  # Añadimos el token a la respuesta
                })
            return Response({
                'status': 'error',
                'error': 'Contraseña incorrecta'
            }, status=status.HTTP_401_UNAUTHORIZED)
        except User.DoesNotExist:
            return Response({
                'status': 'error',
                'error': 'Usuario no encontrado'
            }, status=status.HTTP_404_NOT_FOUND)
        
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import User, Attendance
from django.utils import timezone
from datetime import datetime

class AttendanceView(APIView):
    def post(self, request):
        try:
            username = request.data.get('username')
            fecha_registro = request.data.get('fecha_registro')
            hora_registro = request.data.get('hora_registro')

            # Validaciones básicas
            if not all([username, fecha_registro, hora_registro]):
                return Response({
                    'status': 'error',
                    'message': 'Todos los campos son requeridos'
                }, status=status.HTTP_400_BAD_REQUEST)

            # Verificar si el usuario existe
            try:
                user = User.objects.get(username=username)
            except User.DoesNotExist:
                return Response({
                    'status': 'error',
                    'message': 'Usuario no encontrado'
                }, status=status.HTTP_404_NOT_FOUND)

            # Crear el registro de asistencia
            attendance = Attendance.objects.create(
                username=user,
                fecha_registro=fecha_registro,
                hora_registro=hora_registro,
                created_at=timezone.now()
            )

            return Response({
                'status': 'success',
                'message': 'Asistencia registrada correctamente',
                'data': {
                    'id': attendance.id,
                    'username': username,
                    'fecha_registro': fecha_registro,
                    'hora_registro': hora_registro,
                    'created_at': attendance.created_at
                }
            }, status=status.HTTP_201_CREATED)

        except Exception as e:
            return Response({
                'status': 'error',
                'message': str(e)
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

    def get(self, request):
        try:
            username = request.query_params.get('username')
            
            if username:
                attendances = Attendance.objects.filter(username__username=username)
            else:
                attendances = Attendance.objects.all()

            attendance_data = [{
                'id': attendance.id,
                'username': attendance.username.username,
                'fecha_registro': attendance.fecha_registro,
                'hora_registro': attendance.hora_registro,
                'created_at': attendance.created_at
            } for attendance in attendances]

            return Response({
                'status': 'success',
                'message': 'Registros obtenidos correctamente',
                'data': attendance_data
            })

        except Exception as e:
            return Response({
                'status': 'error',
                'message': str(e)
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)