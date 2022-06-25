from datetime import datetime


class SessionData:
    def __init__(self, session_id: str, person_id: int):
        self.session_id = session_id
        self.person_id = person_id
        self.last_seen = datetime.now()
