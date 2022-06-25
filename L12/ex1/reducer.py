#!/usr/bin/env python3
"""reducer.py"""

import sys

current_letter = None
current_words = []
word = None

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    components = line.split()
    first_letter = components[0]
    words = components[1:]

    # this IF-switch only works because Hadoop sorts map output
    # by key (here: first_letter) before it is passed to the reducer
    if first_letter != current_letter:
        if current_letter:
            # write result to STDOUT
            string = f'{current_letter}\t'+'\t'.join(current_words)
            print(string)
        current_words = words
        current_letter = first_letter
    else:
        for word in words:
            current_words.append(word)

# do not forget to output the last words if needed!
if len(current_words) > 0:
    string = f'{current_letter}\t' + '\t'.join(current_words)
    print(string)
