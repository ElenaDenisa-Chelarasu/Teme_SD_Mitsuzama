
import sys
from functional import seq

if __name__== '__main__':
    l = []
    for line in sys.stdin:
        url, count = line.split('\t', 1)
        l.append((url, int(count)))
    seq(l).sorted(lambda it: it[1])\
        .for_each(lambda it: print(f'{it[0]} -> {it[1]}'))
