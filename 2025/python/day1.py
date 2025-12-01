def parse_input(filename):
    with open(filename, "r") as f:
        return [int(line) for line in f.read().splitlines()]


def times_cross_zero(n1, n2):
    if n1 > n2:
        dial_clicks = range(n2, n1)
    else:
        dial_clicks = range(n2, n1, -1)
    return sum(1 for x in dial_clicks if x % 100 == 0)


def p1(filename):
    nums = parse_input(filename)
    accumulated = [50]
    for num in nums:
        accumulated.append(accumulated[-1] + num)
    return sum(1 for x in accumulated if x % 100 == 0)


def p2(filename):
    nums = parse_input(filename)
    counter = 0
    n2 = 50
    for n1 in nums:
        times = times_cross_zero(n2, n1 + n2)
        counter += times
        n2 = n1 + n2
    return counter


if __name__ == "__main__":
    input_file = "day1.txt"
    print(f"Part 1: {p1(input_file)}")
    print(f"Part 2: {p2(input_file)}")
