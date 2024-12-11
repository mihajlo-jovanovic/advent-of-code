# Day 10: Hoof It 

`Tree traversal`

### Data Format

Re-using the same grid parsing logic as in previous solutions; basically just a map with :grid and :size as keys, where :grid
contains a map of x,y coordinates mapped to an int value representing height. Here is an example:

```
{:grid
 {[2 2] 6,
  [0 0] 0,
  [1 0] 1,
  [2 3] 7,
  [3 3] 6,
  [1 1] 2,
  [3 0] 3,
  [1 3] 8,
  [0 3] 9,
  [0 2] 8,
  [2 0] 2,
  [3 1] 4,
  [2 1] 3,
  [1 2] 7,
  [3 2] 5,
  [0 1] 1},
 :size 4}
```

### Part 1

My first thought was to see if I can use the wonderful `tree-seq` function in Clojure. It's a higher level functioning requiring two additional functions, main one being children. That one should take one argument (in this case a vector of x y coordinates) and return valid paths forward (if any). So after writing this simple function, we get depth-first tree traversal; at this point, all we need to do is find out how many of the `9` positions did we reach. I used a set difference and git the correct answer. Very concise and elegant (this is why i love Clojure!).

### Part 2

Again, same thing here but instead of counting goal positions (ones with a height of 9), we need to count how many distinct paths there are. This ended up being just as simple - just needed to count different occurrences of the same end position in the tree traversal produced by `tree-seq`.

### Runtime

|              |            Exec. Time (ms)| 
|--------------|--------------------------:|
| **Part One** |                  28.411625|
| **Part Two** |                 506.15525 |
