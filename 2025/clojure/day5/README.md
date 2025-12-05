# Day 2: Cafeteria

Overlapping ranges...

## Part 1 

Simple filtering of numbers (`ingredientIDs`) within ranges: 3 lines of Clojure (not including input parsing). Gotta love Clojure!

## Part 2 

We only care about ranges here. I sorted them and wrote a combine-ranges that takes a sorted list of ranges are returns non-overlapping
ones. Then we just add total numbers in each. Fairly simple.

# To run:

    $ java -jar day5-0.1.0-standalone.jar

## Performance

``` 
Part 1:  664
"Elapsed time: 14.647292 msecs"
Part 2:  350780324308385
"Elapsed time: 3.619125 msecs"
```
