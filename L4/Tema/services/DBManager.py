from typing import List, Tuple
from abstractions.IDBManager import IDBManager
from data_classes.PersonalInformation import PersonalInformation
import sqlite3
from functional import seq


class DBManager(IDBManager):
    def __init__(self):
        self.__db = "./persistence/familii.db"

    def log_in(self, user_id: str, password: str) -> int:
        con = sqlite3.connect(self.__db)
        c = con.cursor()
        row = c.execute(
            f'SELECT id_persoana FROM conturi_utilizatori WHERE nume_utilizator=? AND parola=?',
            (user_id, password)).fetchone()
        c.close()
        con.close()
        if row is None:
            return -1
        else:
            return row[0]

    def get_family_informations(self, id_persoana: int) -> List[PersonalInformation]:
        con = sqlite3.connect(self.__db)
        c = con.cursor()

        rows = c.execute('SELECT f.nume_familie, '
                         'p.id_persoana, p.nume, p.prenume, '
                         'c.intretinere, c.mancare, c.distractie, c.scoala, c.personale '
                         'FROM persoane p '
                         'JOIN familii f USING(id_familie) '
                         'JOIN cheltuieli c USING(id_persoana) '
                         'WHERE id_familie='
                         '(SELECT pp.id_familie FROM persoane pp WHERE pp.id_persoana=?)', (id_persoana,)).fetchall()
        c.close()
        con.close()
        return seq(rows).map(lambda row: PersonalInformation(*row)).to_list()

    def _get_account_informations(self, id_persoana: int) -> Tuple[str]:
        con = sqlite3.connect(self.__db)
        c = con.cursor()

        row = c.execute('SELECT nume_utilizator, parola '
                        'FROM conturi_utilizatori '
                        'WHERE id_persoana=?', (id_persoana,)).fetchone()
        c.close()
        con.close()
        return row

    def update_person_informations(self, informations: PersonalInformation):
        con = sqlite3.connect(self.__db)
        c = con.cursor()
        arg = (informations.nume, informations.prenume, informations.id_persoana)
        c.execute('UPDATE persoane SET nume=?, prenume=? WHERE id_persoana=?', arg)
        arg = (informations.nume_familie, informations.id_persoana)
        c.execute('UPDATE familii SET nume_familie=? '
                  'WHERE id_familie='
                  '(SELECT p.id_familie FROM persoane p WHERE p.id_persoana=?)', arg)
        arg = (informations.intretinere, informations.mancare, informations.distractie, informations.scoala,
               informations.personale, informations.id_persoana)
        c.execute('UPDATE cheltuieli SET intretinere=?, mancare=?, distractie=?, scoala=?, personale=? '
                  'WHERE id_persoana=?', arg)
        c.close()
        con.commit()
        con.close()

    def get_person_informations(self, id_persoana: int)->PersonalInformation:
        con = sqlite3.connect(self.__db)
        c = con.cursor()

        row = c.execute('SELECT f.nume_familie, '
                         'p.id_persoana, p.nume, p.prenume, '
                         'c.intretinere, c.mancare, c.distractie, c.scoala, c.personale '
                         'FROM persoane p '
                         'JOIN familii f USING(id_familie) '
                         'JOIN cheltuieli c USING(id_persoana) '
                         'WHERE id_persoana=?', (id_persoana,)).fetchone()
        c.close()
        con.close()
        return PersonalInformation(*row)