from abc import ABCMeta, abstractmethod


class ISessionManager(metaclass=ABCMeta):
    @abstractmethod
    def check_session(self, session_id: str) -> int:
        pass

    @abstractmethod
    def create_session(self, id_persoana: int) -> str:
        pass

    @abstractmethod
    def refresh_session(self, session_id: str):
        pass
