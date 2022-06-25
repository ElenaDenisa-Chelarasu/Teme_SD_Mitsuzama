from datetime import datetime
from time import sleep
from abstractions.AbstractExpiredSessionController import AbstractExpiredSessionController


class ExpiredSessionCollector(AbstractExpiredSessionController):
    def __init__(self, expiration_time, sessions, lock):
        super().__init__(expiration_time, sessions, lock)

    def run(self) -> None:
        until_next_expiration = 0.0  # secunde
        while (True):
            sleep(until_next_expiration)
            until_next_expiration = self._expiration_time.total_seconds()
            with self._lock:
                now = datetime.now()
                for data in self._sessions:
                    age = now - data.last_seen
                    print(f'{data.session_id}, {data.person_id}, {age.total_seconds()}')
                    if age > self._expiration_time:
                        self._sessions.remove(data)
                    elif until_next_expiration > self._expiration_time.total_seconds() - age.total_seconds():
                        until_next_expiration = self._expiration_time.total_seconds() - age.total_seconds()
