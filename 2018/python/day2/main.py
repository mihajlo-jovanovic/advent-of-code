import collections

data = [x.strip() for x in open('day2.txt').readlines()]


def p1():
    twos = 0
    threes = 0
    for l in data:
        freq = collections.Counter(l).values()
        if [x for x in filter(lambda x: x == 2, freq)]:
            twos += 1
        if [x for x in filter(lambda x: x == 3, freq)]:
            threes += 1
    return twos * threes


if __name__ == '__main__':
    print(p1())
