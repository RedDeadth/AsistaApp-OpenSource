from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

from myapp.application.use_cases.auth_use_cases import RegisterUserUseCase, LoginUserUseCase
from myapp.application.use_cases.attendance_use_cases import RegisterAttendanceUseCase, ListAttendanceUseCase
from myapp.infrastructure.repositories.user_repository import UserRepository
from myapp.infrastructure.repositories.attendance_repository import AttendanceRepository


def _build_user_repo():
    return UserRepository()


def _build_attendance_repo():
    return AttendanceRepository()


class RegisterController(APIView):
    def post(self, request):
        try:
            use_case = RegisterUserUseCase(_build_user_repo())
            data = use_case.execute(
                username=request.data.get("username"),
                email=request.data.get("email"),
                password=request.data.get("password"),
            )
            return Response({"message": "User registered successfully", "data": data}, status=status.HTTP_201_CREATED)
        except ValueError as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class LoginController(APIView):
    def post(self, request):
        try:
            use_case = LoginUserUseCase(_build_user_repo())
            data = use_case.execute(
                username=request.data.get("username"),
                password=request.data.get("password"),
            )
            return Response({"status": "success", **data})
        except LookupError as e:
            return Response({"error": str(e)}, status=status.HTTP_404_NOT_FOUND)
        except PermissionError as e:
            return Response({"error": str(e)}, status=status.HTTP_401_UNAUTHORIZED)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class AttendanceController(APIView):
    def post(self, request):
        try:
            use_case = RegisterAttendanceUseCase(_build_attendance_repo(), _build_user_repo())
            data = use_case.execute(
                username=request.data.get("username"),
                fecha_registro=request.data.get("fecha_registro"),
                hora_registro=request.data.get("hora_registro"),
            )
            return Response({"status": "success", "data": data}, status=status.HTTP_201_CREATED)
        except ValueError as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)
        except LookupError as e:
            return Response({"error": str(e)}, status=status.HTTP_404_NOT_FOUND)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

    def get(self, request):
        try:
            use_case = ListAttendanceUseCase(_build_attendance_repo())
            data = use_case.execute(username=request.query_params.get("username"))
            return Response({"status": "success", "data": data})
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
