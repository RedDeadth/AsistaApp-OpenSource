from datetime import date, time
from typing import List, Dict, Any

from api.domain.entities.entities import AttendanceEntity
from api.domain.ports.repository_ports import AttendanceRepositoryPort, UserRepositoryPort


class RegisterAttendanceUseCase:
    def __init__(self, attendance_repo: AttendanceRepositoryPort, user_repo: UserRepositoryPort):
        self._attendance_repo = attendance_repo
        self._user_repo = user_repo

    def execute(self, username: str, fecha_registro: str, hora_registro: str) -> Dict[str, Any]:
        if not all([username, fecha_registro, hora_registro]):
            raise ValueError("All fields are required")

        if not self._user_repo.exists_by_username(username):
            raise LookupError("User not found")

        entity = AttendanceEntity(
            username=username,
            fecha_registro=date.fromisoformat(fecha_registro),
            hora_registro=hora_registro,
        )
        saved = self._attendance_repo.create(entity)
        return {
            "id": saved.id,
            "username": saved.username,
            "fecha_registro": str(saved.fecha_registro),
            "hora_registro": str(saved.hora_registro),
            "created_at": str(saved.created_at),
        }


class ListAttendanceUseCase:
    def __init__(self, repository: AttendanceRepositoryPort):
        self._repository = repository

    def execute(self, username: str = None) -> List[Dict[str, Any]]:
        records = self._repository.find_by_username(username) if username else self._repository.find_all()
        return [
            {
                "id": r.id,
                "username": r.username,
                "fecha_registro": str(r.fecha_registro),
                "hora_registro": str(r.hora_registro),
                "created_at": str(r.created_at),
            }
            for r in records
        ]
