from python import Python
from collections import List, Dict
from time import perf_counter_ns

@fieldwise_init
struct Range(Copyable, Movable):
    var start: Int
    var end: Int

fn count_digits(n: Int) -> Int:
    if n == 0: return 1
    var count = 0
    var temp = n
    while temp > 0:
        temp //= 10
        count += 1
    return count

fn pow_10(exp: Int) -> Int:
    var res = 1
    for _ in range(exp):
        res *= 10
    return res

fn main():
    try:
        var py = Python.import_module("builtins")
        var content = py.open("day2.txt").read().strip()
        var ranges_str = content.split(",")

        var ranges = List[Range]()
        for i in range(len(ranges_str)):
            var parts = ranges_str[i].split("-")
            ranges.append(Range(Int(parts[0]), Int(parts[1])))

        var start_time = perf_counter_ns()

        var p1_sum = 0
        var p2_unique = Dict[Int, Bool]()

        for i in range(len(ranges)):
            var r = ranges[i].copy()
            var min_digits = count_digits(r.start)
            var max_digits = count_digits(r.end)

            for length in range(min_digits, max_digits + 1):
                # Effective range for this length
                var range_start = r.start
                var range_end = r.end

                var min_for_len = pow_10(length - 1)
                var max_for_len = pow_10(length) - 1

                if range_start < min_for_len: range_start = min_for_len
                if range_end > max_for_len: range_end = max_for_len

                if range_start > range_end: continue

                # Part 1 Logic (only if length is even)
                if length % 2 == 0:
                    var half_len = length // 2
                    var multiplier = pow_10(half_len) + 1

                    # Calculate root bounds
                    # We need n >= range_start => root * multiplier >= range_start => root >= range_start / multiplier
                    # We need n <= range_end => root * multiplier <= range_end => root <= range_end / multiplier

                    # Integer division truncates, so for lower bound we might need to increment if remainder
                    var root_min = (range_start + multiplier - 1) // multiplier
                    var root_max = range_end // multiplier

                    # Also clamp to valid root digits (e.g. 10..99 for length 4)
                    var valid_root_min = pow_10(half_len - 1)
                    var valid_root_max = pow_10(half_len) - 1

                    if root_min < valid_root_min: root_min = valid_root_min
                    if root_max > valid_root_max: root_max = valid_root_max

                    for root in range(root_min, root_max + 1):
                        var n = root * multiplier
                        if n >= range_start and n <= range_end:
                            p1_sum += n
                            p2_unique[n] = True

                # Part 2 Logic
                for chunk_size in range(1, (length // 2) + 1):
                    if length % chunk_size == 0:
                        var num_chunks = length // chunk_size

                        # Calculate multiplier
                        var multiplier = 0
                        var current_pow = 1
                        var shift = pow_10(chunk_size)
                        for _ in range(num_chunks):
                            multiplier += current_pow
                            current_pow *= shift

                        # Calculate root bounds
                        var root_min = (range_start + multiplier - 1) // multiplier
                        var root_max = range_end // multiplier

                        var valid_root_min = pow_10(chunk_size - 1)
                        var valid_root_max = pow_10(chunk_size) - 1

                        if root_min < valid_root_min: root_min = valid_root_min
                        if root_max > valid_root_max: root_max = valid_root_max

                        for root in range(root_min, root_max + 1):
                            var n = root * multiplier
                            if n >= range_start and n <= range_end:
                                p2_unique[n] = True

        var p2_sum = 0
        for n in p2_unique.keys():
            p2_sum += n
            
        var end_time = perf_counter_ns()
        var duration_ms = (end_time - start_time) / 1_000_000.0
        
        print("Part 1: " + String(p1_sum))
        print("Part 2: " + String(p2_sum))
        print("Execution time: " + String(duration_ms) + " ms")
        
    except e:
        print("Error: " + String(e))
