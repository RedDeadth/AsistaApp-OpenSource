"""
Admin registrations for the AsistaApp API.

Register your Django admin model views here if needed.
"""
from django.contrib import admin
from api.infrastructure.models.models import User, Attendance

admin.site.register(User)
admin.site.register(Attendance)
