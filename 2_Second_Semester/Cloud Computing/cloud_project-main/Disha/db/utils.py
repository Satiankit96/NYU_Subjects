import sqlite3
from config import ApplicationConfig


class DatabaseHelper:
    @classmethod
    def get_connection(cls, path_to_db=ApplicationConfig.DB_PATH):
        return sqlite3.connect(path_to_db)
