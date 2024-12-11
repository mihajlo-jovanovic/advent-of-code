# Day 11 - Plutonian Pebbles

Counting stones: finally, my first chance this year to use the `memoize` function in Clojure.

### Data Format

Not much to say here—I didn’t even bother to parse the list of numbers; instead, just pasted them verbatim.

## Part 1
> _How many stones will you have after blinking 25 times?_

This part was very easy: just implement the `blink` function and let it rip. However, you could just tell where this was headed in Part 2...

### Part 2
> _How many stones would you have after blinking a total of 75 times?_

Honestly, took me a little while to get the right version of the memoized function, but it was still a fun one! I like problems like this that don’t require a ton of code. It runs in around ~100 milliseconds for Part 2.

### Runtime

|              | Exec. Time (ms) - Clojure | 
|--------------|--------------------------:|
| **Part One** |                  16.832042|
| **Part Two** |                 101.806917|