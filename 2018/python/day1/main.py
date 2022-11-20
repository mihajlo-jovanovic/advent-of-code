import itertools

data = [x.strip() for x in open('day1.txt').readlines()]


def p1():
    return sum(map(int, data))

def p2():
    acc = {0}
    running_total = 0
    for num in itertools.cycle(map(int, data)):
        running_total += num
        if running_total in acc:
            return running_total
        acc.add(running_total)

if __name__ == '__main__':
    print(p1())
    print(p2())
