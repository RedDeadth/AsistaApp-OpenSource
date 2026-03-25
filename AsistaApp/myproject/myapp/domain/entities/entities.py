from dataclasses import dataclass
from datetime import date, time, datetime
from typing import Optional


@dataclass
class UserEntity:
    username: str
    email: str
    usertype: str
    id: Optional[int] = None


@dataclass
class AttendanceEntity:
    username: str
    fecha_registro: date
    hora_registro: time
    id: Optional[int] = None
    created_at: Optional[datetime] = None
