# Day 9 - Elf Game (deque)

Did part 1 in Clojure: very inefficient. Ran for over 3 minutes! Stuggled for a while due to lack of proper deque implementaion in Clojure.
Finally did part 2 in Rust: 50ms!

Advice for future similar AoC puzzle: any time you see ring or circle, think circular (de)queue. Constant time inserts. Plus no need
to really keep list alligned (as shown in description) - as long as relative positions are intact it really doesn't matter. That's one
piece of insight that really helped with this one.