# Day 6 - Guard Gallivant

Finally, first grid problem of the season - looking for loops...

### Data Format

At first, I tried reusing one of the earier parsers that stores the `wall` coordinates in individual maps with x, y, and value as keys, like so:

 ```
#{{:x 2, :y 3, :value \#}
  {:x 0, :y 8, :value \#}
  {:x 9, :y 1, :value \#}
  {:x 7, :y 4, :value \#}
  {:x 8, :y 7, :value \#}
  {:x 6, :y 9, :value \#}
  {:x 4, :y 6, :value \^}
  {:x 1, :y 6, :value \#}
  {:x 4, :y 0, :value \#}}
```

This proved a bit akwards and unnecesarily bloated, but it worked and I was too lazy to make it more efficient, instead choosing to spend time re-implementing in Rust.

## Part 1
> _How many distinct positions will the guard visit before leaving the mapped area?_

This was pretty straightforward, as all we needed was a way to simulate movement on a 2D grid. Due to the late hour, a long work week, and the aforementioned issues with the data model representation, it took longer than it should have, but ultimately, it worked fine.

### Part 2
> _How many different positions could you choose for this obstruction?_

After spending almost an hour on Part 1, I decided to tackle this part later in the day. After having some time to think about it, I realized this was yet another use case for a familiar pattern: looking for a repeated state as a way of determining if we're stuck in a loop. All we needed was to try all possible points along the predetermined path and count the ones that resulted in a loop.

When I first implemented this in Clojure, it ran for around 8 minutes and came back with an incorrect result that was off by one (as I forgot to correctly check the last point along the path). I then re-implemented a very similar strategy in Rust and was happy to see it run in a little over a second!
I like this approach of first modeling a solution using something like a Domain Specific Language in Clojure, and only after understanding it better re-implementing in something more effecient (like Rust).

### Runtime

|              | Exec. Time (ms) - Clojure | Exec. Time (ms) - Rust
|--------------|--------------------------:|--------------------------:|
| **Part One** |                 205.274416|                         <1|
| **Part Two** |                    ~8 mins|                1200.883042|


```console
➜  rust git:(main) ✗ cargo-aoc -d 6
    Finished `dev` profile [unoptimized + debuginfo] target(s) in 0.01s
warning: no edition set: defaulting to the 2015 edition while the latest is 2021
   Compiling aoc-autobuild v0.3.0 (/Users/mihajlo/projects/advent-of-code/2024/rust/target/aoc/aoc-autobuild)
    Finished `release` profile [optimized] target(s) in 0.11s
     Running `target/release/aoc-autobuild`
AOC 2024
Day 6 - Part 1 : 5564
	generator: 112.916µs,
	runner: 850.75µs

Day 6 - Part 2 : 1976
	generator: 87.041µs,
	runner: 1.182762292s
```