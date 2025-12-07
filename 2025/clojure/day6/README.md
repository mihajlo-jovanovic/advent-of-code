# Day 6: Trash Compactor 

Solving arithmetic expressions...

## Part 1 

This is one of those problems that reminds me why I love Clojure. I was able to make use of `eval`
and treat math problems like regular Clojure code (I think the big word is homoiconic?)

## Part 2 

Big insight here is to transpose the matrix by 90 degrees counter-clockwise.

### To run:

    $ java -jar day6-0.1.0-standalone.jar
    
## Execution time 

```
Part 2:  10756006415204
"Elapsed time: 164.713625 msecs"
```

I also write a solution in Mojo, which completed on my machine in less than 1 millisecond.
