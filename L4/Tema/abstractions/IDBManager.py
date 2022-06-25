from abc import ABCMeta, abstractmethod

from typing import List

from data_classes.PersonalInformation import PersonalInformation


class IDBManager(metaclass=ABCMeta):
    @abstractmethod
    def log_in(self, user_id: str, password: str) -> int:
        pass

    @abstractmethod
    def get_family_informations(self, id_persoana: int) -> List[PersonalInformation]:
        pass

    @abstractmethod
    def update_person_informations(self, informations: PersonalInformation):
        pass

    @abstractmethod
    def get_person_informations(self, id_persoana: int):
        pass
