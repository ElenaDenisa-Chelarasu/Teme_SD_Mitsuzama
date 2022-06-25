from threading import Thread
from abc import ABCMeta, abstractmethod
from typing import List

from data_classes.SessionData import SessionData


class AbstractExpiredSessionController(Thread, metaclass=ABCMeta):
    def __init__(self, expiration_time, sessions, lock):
        super().__init__()
        self._sessions:List[SessionData] = sessions
        self._lock = lock
        self._expiration_time=expiration_time

    @abstractmethod
    def run(self) -> None:
        pass
