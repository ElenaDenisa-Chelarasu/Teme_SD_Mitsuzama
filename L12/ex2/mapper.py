#!/usr/bin/env python3
"""mapper.py"""

import sys
from subprocess import check_output
from re import fullmatch

REGEX = r'.*(A|a).*(D|d).*'

for command in sys.stdin:
    command = command.strip()
    command_output = check_output(command.split()).decode()
    for line in command_output.split('\n'):
        if fullmatch(REGEX, line):
            print(line)
            