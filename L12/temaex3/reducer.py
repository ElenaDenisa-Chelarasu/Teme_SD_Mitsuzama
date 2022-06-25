#!/usr/bin/env python3
"""reducer.py"""

import sys

current_url = None
current_count = 0

for line in sys.stdin:
    line = line.strip()
    line_url, line_count = line.split('\t', 1)
    line_count = int(line_count)
    if line_url != current_url:
        if current_url:
            print(f'{current_url}\t{current_count}')
        current_url = line_url
        current_count = line_count
    else:
        current_count += line_count

if current_count > 0:
    print(f'{current_url}\t{current_count}')
