# Day 21: Keypad Conundrum

String lists of lists, paths, func keypads...  

### Part 1

My first approach was to model keypads as maps, then write a function to return a shortest sequence (in terms of directions like "<^v<>>").
As many others, quickly got hung up on input "379A" realizing I needed to try all possible shortest paths. One useful piece of insight (that I got from reddit) was modeling \A position as [0 0] so that the gap is in the same spot in both numeric and directional paths.
Ultimately this worked for part 1 and I was happy with how easy to reason about the solution ended up. Not so happy with the performance aspect tho...

## Part 2

This one got me stuck for now. Tried the obvious brute force approach and quickly realized it's going nowhere (there are almost a 1MM unique shortest paths even for the two directional keypads!). Then I also tried to see the pattern in lengths for different inputs - another dead end.
I have a feeling I'll need top invest some time into learning DP for next year...


## Runtime

```bash
day21 git:(main) ✗ java -jar target/uberjar/day21-0.1.0-SNAPSHOT-standalone.jar
Part 1:  174124
"Elapsed time: 25466.394584 msecs"
```
