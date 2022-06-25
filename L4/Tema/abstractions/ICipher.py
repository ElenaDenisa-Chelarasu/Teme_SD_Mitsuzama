from abc import ABCMeta, abstractmethod
from re import findall


class ICipher(metaclass=ABCMeta):
    @abstractmethod
    def encrypt(self, raw):
        pass

    @abstractmethod
    def decrypt(self, enc):
        pass

    @staticmethod
    def parse_encoding(string) -> bytes:
        ints = findall(r'(?<=\\d)\w+', string)
        rez = b''.join([int(i).to_bytes(1, 'big') for i in ints])
        return rez

    @staticmethod
    def encoding_to_string(b: bytes) -> str:
        return ''.join([f'\d{int(byte)}' for byte in b])
