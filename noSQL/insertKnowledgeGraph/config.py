from pydantic import BaseSettings

from pathlib import Path


class _Settings(BaseSettings):    # Stardog repository connection
    STARDOG_ENDPOINT = "http://localhost:5820"
    STARDOG_USERNAME = "admin"
    STARDOG_PASSWORD = "password"

    @property
    def rdf_connection_settings(self) -> dict:
        return {
            "endpoint": self.STARDOG_ENDPOINT,
            "username": self.STARDOG_USERNAME,
            "password": self.STARDOG_PASSWORD,
        }

    class Config:
        env_file = ".env"
        file_path = Path(env_file)
        if not file_path.is_file():
            print("⚠️ File setting not found")
            print("⚙️ Loading settings from environment")
        else:
            print(f"⚙️ Loading settings from file")


settings = _Settings()
