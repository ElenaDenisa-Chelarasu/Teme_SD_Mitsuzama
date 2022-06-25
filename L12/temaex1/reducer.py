#!/usr/bin/env python3
"""reducer.py"""

import sys

current_url = None
intern_urls = []

for line in sys.stdin:
    line = line.strip()
    components = line.split('\t')
    line_url = components[0]
    intern_line_urls = components[1:]
    if line_url != current_url:
        if current_url:
            string = f'{current_url}\t' + '\t'.join(intern_urls)
            print(string)
        current_url = line_url
        intern_urls = intern_line_urls
    else:
        for url in intern_line_urls:
            intern_urls.append(url)

if len(intern_urls) > 0:
    string = f'{current_url}\t' + '\t'.join(intern_urls)
    print(string)
