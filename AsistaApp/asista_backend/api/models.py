from django.db import models

class User(models.Model):
    id = models.AutoField(primary_key=True)
    username = models.CharField(max_length=150, unique=True)
    email = models.EmailField(max_length=255, unique=True)
    password = models.CharField(max_length=128)
    usertype = models.CharField(max_length=20, default='user')

    class Meta:
        db_table = 'users'

    def __str__(self):
        return self.username

class Attendance(models.Model):
    username = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        to_field='username',
        db_column='username'
    )
    fecha_registro = models.DateField()
    hora_registro = models.TimeField()
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'attendance'