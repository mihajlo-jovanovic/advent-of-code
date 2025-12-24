# Day 10

Integer Linear Programming, Gaussian Elimination…

## Part 1

I was able to do this in my typical inefficient `functional` way, looping over the Cartesian product and brute-forcing all possibilities using higher-level functions like frequencies.

## Part 2

I really wanted to solve this without bringing in a linear systems solver. I struggled for a while trying a backtracking approach. Then I got tired and enlisted help from Gemini, ultimately ending up with a Gaussian elimination implementation in Clojure. Not very happy with it, but at least I brushed up on backtracking in Clojure (which, btw, seems really only suitable for very small inputs).

On to the next day…

## To run:

    $ java -jar target/uberjar/day10-0.1.0-standalone.jar 

## Performance

``` 
"Elapsed time: 91286.965958 msecs"
Part 2:  15377
```
