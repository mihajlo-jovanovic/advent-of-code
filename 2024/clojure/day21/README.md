# Day 21: Keypad Conundrum

String lists of lists, paths, func keypads...  

### Part 1

My first approach was to model keypads as maps, then write a function to return a shortest sequence (in terms of directions like "<^v<>>").
As many others, quickly got hung up on input "379A" realizing I needed to try all possible shortest paths. One useful piece of insight (that I got from reddit) was modeling \A position as [0 0] so that the gap is in the same spot in both numeric and directional paths.
Ultimately this worked for part 1 and I was happy with how easy to reason about the solution ended up. Not so happy with the performance aspect tho...

## Part 2

This one took me the longest amount of time this year by far...
I knew right away it would involve some type of `dynamic programming` approach, of breaking down inputs into smaller chucks that could then be 
solved quickly using caching (in Clojure there is a very useful momeoize function to do just that). I also knew I obviously could not continue to 
build the Strings in order to check for the shortest. However it still took several hours to finally get the right level of abstraction and
convert it into code. I did not want to seek help on this one and was stubborn about coming up with a solution myself. In the end, one
general advice from someone on Reddit made it difference: when you;'re stuck on a puzzle, step away from a computer and go to pencil and paper.
After getting a good night sleep I did just that and an hour later - voila!


## Runtime

```bash
day21 git:(main) ✗ java -jar target/uberjar/day21-0.1.0-SNAPSHOT-standalone.jar
Part 1:  174124
Part 2:  216668579770346
"Elapsed time: 26.74275 msecs"
```
