from collections import List
from pathlib import Path
from time import perf_counter_ns


@fieldwise_init
struct Range(Copyable, Movable, Comparable):
    var start: Int
    var end: Int

    fn __lt__(self, other: Range) -> Bool:
        if self.start != other.start:
            return self.start < other.start
        return self.end < other.end

    fn __le__(self, other: Range) -> Bool:
        return self < other or self == other

    fn __gt__(self, other: Range) -> Bool:
        return other < self

    fn __ge__(self, other: Range) -> Bool:
        return other <= self

    fn __eq__(self, other: Range) -> Bool:
        return self.start == other.start and self.end == other.end

    fn __ne__(self, other: Range) -> Bool:
        return not (self == other)


struct ParsedInput(Movable):
    var ranges: List[Range]
    var numbers: List[Int]

    fn __init__(out self, var ranges: List[Range], var numbers: List[Int]):
        self.ranges = ranges^
        self.numbers = numbers^

    fn __moveinit__(out self, deinit existing: Self):
        self.ranges = existing.ranges^
        self.numbers = existing.numbers^


fn parse_input(filename: String) raises -> ParsedInput:
    var content = Path(filename).read_text()

    var sections = content.strip().split("\n\n")
    var ranges_section = sections[0]
    var numbers_section = sections[1]

    var ranges = List[Range]()
    var range_lines = ranges_section.split("\n")
    for i in range(len(range_lines)):
        var parts = range_lines[i].split("-")
        var start = Int(parts[0].strip())
        var end = Int(parts[1].strip())
        ranges.append(Range(start, end))

    var numbers = List[Int]()
    var number_lines = numbers_section.split("\n")
    for i in range(len(number_lines)):
        var line = number_lines[i].strip()
        if len(line) > 0:
            numbers.append(Int(line))

    return ParsedInput(ranges^, numbers^)


fn is_in_any_range(n: Int, ref ranges: List[Range]) -> Bool:
    for i in range(len(ranges)):
        if ranges[i].start <= n <= ranges[i].end:
            return True
    return False


fn filter_numbers_in_ranges(
    ref ranges: List[Range], ref numbers: List[Int]
) -> List[Int]:
    var result = List[Int]()
    for i in range(len(numbers)):
        if is_in_any_range(numbers[i], ranges):
            result.append(numbers[i])
    return result^


fn count_unique_in_ranges(ref ranges: List[Range]) -> Int:
    if len(ranges) == 0:
        return 0

    var sorted_ranges = List[Range]()
    for i in range(len(ranges)):
        sorted_ranges.append(Range(ranges[i].start, ranges[i].end))
    sort(sorted_ranges)

    var merged = List[Range]()
    merged.append(Range(sorted_ranges[0].start, sorted_ranges[0].end))

    for i in range(1, len(sorted_ranges)):
        var current_start = sorted_ranges[i].start
        var current_end = sorted_ranges[i].end
        var last_idx = len(merged) - 1
        var last_start = merged[last_idx].start
        var last_end = merged[last_idx].end

        if current_start <= last_end + 1:
            merged[last_idx] = Range(last_start, max(last_end, current_end))
        else:
            merged.append(Range(current_start, current_end))

    var total = 0
    for i in range(len(merged)):
        total += merged[i].end - merged[i].start + 1
    return total


fn main() raises:
    var input = parse_input("day5.txt")

    start = perf_counter_ns()
    var filtered = filter_numbers_in_ranges(input.ranges, input.numbers)
    print(len(filtered))

    print(count_unique_in_ranges(input.ranges))
    end = perf_counter_ns()

    duration_ms = (end - start) / 1_000_000.0
    print("Execution time: " + String(duration_ms) + " ms")
