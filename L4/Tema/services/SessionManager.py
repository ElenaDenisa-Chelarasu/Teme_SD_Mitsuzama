import string
from datetime import datetime, timedelta
from typing import List
from abstractions.ISessionManager import ISessionManager
from data_classes.SessionData import SessionData
from threading import RLock
from random import choice
from services.ExpiredSessionCollector import ExpiredSessionCollector

class SessionManager(ISessionManager):
    def __init__(self):
        self.__sessions: List[SessionData] = []
        self.__lock = RLock()
        self.__expired_session_collector = ExpiredSessionCollector(timedelta(seconds=60), self.__sessions, self.__lock)
        self.__expired_session_collector.start()

    def check_session(self, session_id: str) -> int:
        with self.__lock:
            for data in self.__sessions:
                if data.session_id == session_id:
                    self.refresh_session(session_id)
                    return data.person_id
            return -1

    def create_session(self, id_persoana: int) -> str:
        with self.__lock:
            chars = ''.join(string.digits + string.ascii_letters)
            session_id = ''.join(choice(chars) for _ in range(20))
            self.__sessions.append(SessionData(session_id, id_persoana))
            return session_id

    def refresh_session(self, session_id: str):
        with self.__lock:
            for data in self.__sessions:
                if data.session_id == session_id:
                    data.last_seen = datetime.now()


SessionManagerInstance = SessionManager()
