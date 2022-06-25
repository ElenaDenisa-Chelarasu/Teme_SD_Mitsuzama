#!/usr/bin/env python3
"""mapper.py"""

import sys

from re import findall

link_regex = r'^([^\?#]+)((#.+)|(\?.+))?$'

for url in sys.stdin:
    url = url.strip()
    clean_url = findall(link_regex, url)
    # ultima linie e goala (cred) deoarece inputul e fisier text linux si se termina cu o linie goala
    if len(clean_url) == 1:
        clean_url = clean_url[0][0]
    if len(clean_url) > 0:
        print(f'{clean_url}\t1')
