from typing import List

from data_classes.PersonalInformation import PersonalInformation
from services.DBManager import DBManager
from services.AESCipher import AESCipher
from abstractions.ICipher import ICipher

class EncryptedDBManager(DBManager):
    def __init__(self):
        super().__init__()

    def log_in(self, user_id: str, password: str) -> int:
        return super().log_in(user_id, password)

    def get_family_informations(self, id_persoana: int) -> List[PersonalInformation]:
        informations=super().get_family_informations(id_persoana)
        for i in informations:
            key = ''.join(super()._get_account_informations(i.id_persoana))
            cipher = AESCipher(key)
            aux=ICipher.parse_encoding(i.nume)
            i.nume=cipher.decrypt(aux)
            aux=ICipher.parse_encoding(i.prenume)
            i.prenume=cipher.decrypt(aux)
        return informations

    def update_person_informations(self, informations: PersonalInformation):
        key = ''.join(super()._get_account_informations(informations.id_persoana))
        cipher = AESCipher(key)

        aux=cipher.encrypt(informations.nume)
        informations.nume=ICipher.encoding_to_string(aux)

        aux=cipher.encrypt(informations.prenume)
        informations.prenume = ICipher.encoding_to_string(aux)

        super().update_person_informations(informations)

    def get_person_informations(self, id_persoana: int):
        info=super().get_person_informations(id_persoana)
        key = ''.join(super()._get_account_informations(info.id_persoana))
        cipher = AESCipher(key)

        aux = ICipher.parse_encoding(info.nume)
        info.nume = cipher.decrypt(aux)

        aux = ICipher.parse_encoding(info.prenume)
        info.prenume = cipher.decrypt(aux)

        return info
