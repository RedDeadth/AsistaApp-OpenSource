from django.urls import path
from .views import RegisterView, LoginView, AttendanceView

urlpatterns = [
    path('users/register/', RegisterView.as_view(), name='register'),
    path('users/login/', LoginView.as_view(), name='login'),
    path('register_attendance/', AttendanceView.as_view(), name='register-attendance'),
]