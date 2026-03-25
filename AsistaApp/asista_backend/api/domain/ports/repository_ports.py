from abc import ABC, abstractmethod
from typing import Optional, List
from api.domain.entities.entities import UserEntity, AttendanceEntity


class UserRepositoryPort(ABC):

    @abstractmethod
    def find_by_username(self, username: str) -> Optional[UserEntity]:
        pass

    @abstractmethod
    def exists_by_username(self, username: str) -> bool:
        pass

    @abstractmethod
    def create(self, username: str, email: str, password: str) -> UserEntity:
        pass


class AttendanceRepositoryPort(ABC):

    @abstractmethod
    def create(self, entity: AttendanceEntity) -> AttendanceEntity:
        pass

    @abstractmethod
    def find_all(self) -> List[AttendanceEntity]:
        pass

    @abstractmethod
    def find_by_username(self, username: str) -> List[AttendanceEntity]:
        pass
