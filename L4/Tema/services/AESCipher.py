import base64
import hashlib
from Crypto.Cipher import AES
from random import randint
from abstractions.ICipher import ICipher


class AESCipher(ICipher):
    def __init__(self, key):
        self.__bs = AES.block_size
        self.__key = hashlib.sha256(key.encode()).digest()

    def encrypt(self, raw):
        pad = lambda s: s + (self.__bs - len(s) % self.__bs) * chr(self.__bs - len(s) % self.__bs)
        raw = pad(raw)
        iv = b''.join([randint(0, 0xFF).to_bytes(1, 'big') for _ in range(self.__bs)])
        cipher = AES.new(self.__key, AES.MODE_CBC, iv)
        return base64.b64encode(iv + cipher.encrypt(raw.encode()))

    def decrypt(self, enc):
        unpad = lambda s: s[:-ord(s[len(s) - 1:])]
        enc = base64.b64decode(enc)
        iv = enc[:AES.block_size]
        cipher = AES.new(self.__key, AES.MODE_CBC, iv)
        return unpad(cipher.decrypt(enc[AES.block_size:])).decode('utf-8')
