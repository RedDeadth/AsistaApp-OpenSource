from typing import Optional, List
from api.domain.entities.entities import UserEntity
from api.domain.ports.repository_ports import UserRepositoryPort
from api.models import User


class UserRepository(UserRepositoryPort):

    def find_by_username(self, username: str) -> Optional[UserEntity]:
        try:
            user = User.objects.get(username=username)
            return UserEntity(id=user.id, username=user.username, email=user.email, usertype=user.usertype)
        except User.DoesNotExist:
            return None

    def exists_by_username(self, username: str) -> bool:
        return User.objects.filter(username=username).exists()

    def create(self, username: str, email: str, password: str) -> UserEntity:
        user = User.objects.create(username=username, email=email, password=password, usertype="user")
        return UserEntity(id=user.id, username=user.username, email=user.email, usertype=user.usertype)

    @staticmethod
    def _get_raw(username: str) -> User:
        return User.objects.get(username=username)
