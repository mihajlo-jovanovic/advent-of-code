from pathlib import Path
from time import perf_counter_ns


fn parse_input(filename: String, mut numbers: List[List[Int]], mut operations: List[String]) raises:

    var content = Path(filename).read_text()
    var lines = content.split("\n")

    for i in range(len(lines)):
        var line = String(lines[i])
        var parts = line.split()
        if len(parts) == 0:
            continue

        var all_digits = True
        var all_ops = True

        for j in range(len(parts)):
            var p = String(parts[j])
            if not p.isdigit():
                all_digits = False
            if p != "*" and p != "+":
                all_ops = False

        if all_digits:
            var row = List[Int]()
            for j in range(len(parts)):
                row.append(Int(String(parts[j])))
            numbers.append(row^)
        elif all_ops:
            for j in range(len(parts)):
                operations.append(String(parts[j]))


fn transpose(var matrix: List[List[Int]]) -> List[List[Int]]:
    if len(matrix) == 0:
        return List[List[Int]]()

    var rows = len(matrix)
    var cols = len(matrix[0])
    var result = List[List[Int]]()

    for col in range(cols):
        var new_row = List[Int]()
        for row in range(rows):
            new_row.append(matrix[row][col])
        result.append(new_row^)

    return result^


fn reduce_columns(ref columns: List[List[Int]], ref operations: List[String]) -> Int:
    var total = 0

    for i in range(len(columns)):
        var op = operations[i]

        if op == "*":
            var product = 1
            for j in range(len(columns[i])):
                product *= columns[i][j]
            total += product
        elif op == "+":
            var sum_val = 0
            for j in range(len(columns[i])):
                sum_val += columns[i][j]
            total += sum_val

    return total


fn parse_columns_rtl(filename: String) raises -> Int:
    var content = Path(filename).read_text()
    var lines_split = content.split("\n")
    var lines = List[String]()

    for i in range(len(lines_split)):
        var line = String(lines_split[i])
        if len(line) > 0:
            lines.append(line^)

    var max_len = 0
    for i in range(len(lines)):
        if len(lines[i]) > max_len:
            max_len = len(lines[i])

    var padded_lines = List[String]()
    for i in range(len(lines)):
        var line = lines[i]
        var padding = max_len - len(line)
        var padded = String(line)
        for _ in range(padding):
            padded += " "
        padded_lines.append(padded^)

    var num_lines = List[String]()
    for i in range(len(padded_lines) - 1):
        num_lines.append(String(padded_lines[i]))
    var op_line = String(padded_lines[len(padded_lines) - 1])

    var problems = List[List[Int]]()
    var problem_ops = List[String]()
    var current_numbers = List[Int]()
    var current_op = String("")

    for col_idx in range(max_len - 1, -1, -1):
        var all_spaces = True
        var digit_str = String("")

        for i in range(len(num_lines)):
            var c = String(num_lines[i][col_idx])
            if c != " ":
                all_spaces = False
            if c >= "0" and c <= "9":
                digit_str += c

        var op_char = String(op_line[col_idx])
        if op_char != " ":
            all_spaces = False

        if all_spaces:
            if len(current_numbers) > 0:
                var reversed_nums = List[Int]()
                for i in range(len(current_numbers) - 1, -1, -1):
                    reversed_nums.append(current_numbers[i])
                problems.append(reversed_nums^)
                problem_ops.append(current_op^)
                current_numbers = List[Int]()
                current_op = String("")
        else:
            if len(digit_str) > 0:
                current_numbers.append(Int(digit_str))
            if op_char == "*" or op_char == "+":
                current_op = op_char^

    if len(current_numbers) > 0:
        var reversed_nums = List[Int]()
        for i in range(len(current_numbers) - 1, -1, -1):
            reversed_nums.append(current_numbers[i])
        problems.append(reversed_nums^)
        problem_ops.append(current_op^)

    var total = 0
    for i in range(len(problems)):
        var op = problem_ops[i]

        if op == "*":
            var product = 1
            for j in range(len(problems[i])):
                product *= problems[i][j]
            total += product
        elif op == "+":
            var sum_val = 0
            for j in range(len(problems[i])):
                sum_val += problems[i][j]
            total += sum_val

    return total


fn main() raises:
    var start = perf_counter_ns()

    var numbers = List[List[Int]]()
    var operations = List[String]()
    parse_input("day6.txt", numbers, operations)

    var columns = transpose(numbers^)
    var part1 = reduce_columns(columns, operations)
    print("Part 1:", part1)

    var part2 = parse_columns_rtl("day6.txt")
    print("Part 2:", part2)

    var elapsed = perf_counter_ns() - start
    print("Time:", Float64(elapsed) / 1000000.0, "ms")
