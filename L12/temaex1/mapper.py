#!/usr/bin/env python3
"""mapper.py"""

import sys
from subprocess import check_output
from re import fullmatch

import requests
from re import findall

link_regex = r'(?<=<a href=")[^"]+(?=")'

for url in sys.stdin:
    url = url.strip()
    response = requests.get(url).content.decode()
    for match in findall(link_regex, response):
        print(f'{url}\t{match}')