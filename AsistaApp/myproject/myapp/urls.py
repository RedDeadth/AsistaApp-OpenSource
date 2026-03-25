from django.urls import path
from myapp.presentation.controllers.controllers import RegisterController, LoginController, AttendanceController

urlpatterns = [
    path("users/register/", RegisterController.as_view(), name="register"),
    path("users/login/", LoginController.as_view(), name="login"),
    path("register_attendance/", AttendanceController.as_view(), name="register-attendance"),
]