from django.contrib.auth.hashers import make_password, check_password
import jwt
import os
from datetime import datetime, timedelta
from typing import Optional, Dict, Any

from api.domain.ports.repository_ports import UserRepositoryPort


class RegisterUserUseCase:
    def __init__(self, repository: UserRepositoryPort):
        self._repository = repository

    def execute(self, username: str, email: str, password: str) -> Dict[str, Any]:
        if not all([username, email, password]):
            raise ValueError("All fields are required")

        if self._repository.exists_by_username(username):
            raise ValueError("Username already exists")

        user = self._repository.create(
            username=username,
            email=email,
            password=make_password(password)
        )
        return {"id": user.id, "username": user.username, "email": user.email, "usertype": user.usertype}


class LoginUserUseCase:
    def __init__(self, repository: UserRepositoryPort):
        self._repository = repository

    def execute(self, username: str, password: str) -> Dict[str, Any]:
        user = self._repository.find_by_username(username)
        if user is None:
            raise LookupError("User not found")

        from api.infrastructure.repositories.user_repository import UserRepository
        raw_user = UserRepository._get_raw(username)
        if not check_password(password, raw_user.password):
            raise PermissionError("Invalid credentials")

        secret_key = os.environ.get("JWT_SECRET_KEY", "change-me-in-env")
        token = jwt.encode(
            {
                "username": user.username,
                "email": user.email,
                "usertype": user.usertype,
                "exp": datetime.utcnow() + timedelta(days=1),
            },
            secret_key,
            algorithm="HS256",
        )
        return {"id": user.id, "username": user.username, "email": user.email, "usertype": user.usertype, "access": token}
