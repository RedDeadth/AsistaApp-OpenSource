from typing import List
from django.utils import timezone
from api.domain.entities.entities import AttendanceEntity
from api.domain.ports.repository_ports import AttendanceRepositoryPort
from api.infrastructure.models.models import Attendance, User


class AttendanceRepository(AttendanceRepositoryPort):

    def create(self, entity: AttendanceEntity) -> AttendanceEntity:
        user = User.objects.get(username=entity.username)
        record = Attendance.objects.create(
            username=user,
            fecha_registro=entity.fecha_registro,
            hora_registro=entity.hora_registro,
            created_at=timezone.now(),
        )
        return AttendanceEntity(
            id=record.id,
            username=entity.username,
            fecha_registro=record.fecha_registro,
            hora_registro=record.hora_registro,
            created_at=record.created_at,
        )

    def find_all(self) -> List[AttendanceEntity]:
        return [
            AttendanceEntity(
                id=r.id,
                username=r.username.username,
                fecha_registro=r.fecha_registro,
                hora_registro=r.hora_registro,
                created_at=r.created_at,
            )
            for r in Attendance.objects.select_related("username").all()
        ]

    def find_by_username(self, username: str) -> List[AttendanceEntity]:
        return [
            AttendanceEntity(
                id=r.id,
                username=r.username.username,
                fecha_registro=r.fecha_registro,
                hora_registro=r.hora_registro,
                created_at=r.created_at,
            )
            for r in Attendance.objects.filter(username__username=username).select_related("username")
        ]
