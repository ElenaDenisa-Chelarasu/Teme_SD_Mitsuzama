#!/usr/bin/env python3
"""reducer.py"""

import sys

current_string = ''
for line in sys.stdin:
    line = line.strip()
    current_string += line
print(current_string)
