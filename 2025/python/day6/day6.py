def transpose(matrix):
    return [list(col) for col in zip(*matrix)]


def reduce_columns(columns, operations):
    from functools import reduce

    totals = []
    for col, op in zip(columns, operations):
        if op == "*":
            totals.append(reduce(lambda a, b: a * b, col))
        elif op == "+":
            totals.append(reduce(lambda a, b: a + b, col))
    return sum(totals)


def parse_input(filename):
    with open(filename, "r") as f:
        lines = f.readlines()

    numbers = []
    operations = []

    for line in lines:
        parts = line.split()
        if parts and all(p.isdigit() for p in parts):
            numbers.append([int(p) for p in parts])
        elif parts and all(p in ["*", "+"] for p in parts):
            operations = parts

    return numbers, operations


if __name__ == "__main__":
    numbers, operations = parse_input("day6.txt")
    columns = transpose(numbers)
    result = reduce_columns(columns, operations)
    print("Part 1:", result)
