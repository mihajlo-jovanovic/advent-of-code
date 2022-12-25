# advent-of-code-2022

My solutions to the [Advent of Code](https://adventofcode.com/) 2022 puzzles, mostly in Rust.

`clojure` contains solutions in Clojure (`duh`) for days where I felt the language is more appropriate tool
(plus I probably also got tired of wrestling with the Rust borrow checker ;-)

### Day 16

This one gave me some trouble...solved part 2 somewhat manually, by running part 1 twice (once for `me` and the
second time for the `Elephant`, making sure to use disjoint set of valves - basically excluding the ones that were
opened during the first run). Unfortunately, the way I coded part 1 it was not easy to also list valves opened along 
the winning route. Maybe someday I will go back and re-factor using Dynamic programming / memoization approach, but not likely...