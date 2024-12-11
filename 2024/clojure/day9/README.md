# Day 9 - Disk Fragmenter

### Data Format

I initially settled on a compressed format consisting of vectors, where the first element is the file ID,
and the second one is another vector of exactly two elements: file size and free space length. Here is
what that looks like for the sample input provided:

 ```
[[0 [2 3]] [1 [3 3]] [2 [1 3]] [3 [3 1]] [4 [2 1]] [5 [4 1]] [6 [4 1]] [7 [3 1]] [8 [4 0]] [9 [2 0]]]
```

## Part 1
> _Compact the amphipod's hard drive using the process he requested. What is the resulting filesystem checksum?_

My initial idea was to use a simple recursive function (using standard pop/first/rest constructs) to build
incrementally the compacted disk map. I then expand into full list in order to calculate checksum. Not very
efficient but it works! On to part 2... 

### Part 2
> _How many different positions could you choose for this obstruction?_

Unfortunately my choice of data format, as well as the approach in part 1, made part 2 a lot more difficult. I could
no longer build the compacted disk map sequentially.

I first thought of just doing the brute force method
of simply allocating a vector to represent the entire disk map (which is probably what I should've done),
but instead I tried to reuse my existing data model of a compacted list. This resulted in ~20 or so lines of really
ugly & inefficint Clojure code. Basically I needed to:

1. Check the list of `blocks` in decreasing order of file IDs
2. For each one, see if there's enough free space to the left of it; if yes, we then need to update both the file block we are movingf and the block immediately before (to account for free space as a result of moving the file), as well as account for the edge case there the block with free space is the one immediately to the left of the file to move
3. As we can see, this results in both removal and inserts in the middle of a list,  which is not something vectors are good at 

Best thing I can say about it is it works! (honesly I was surprised I was even able to get it to work without using a debugger). I may at some point re-implement in Rust using something like a deque to avoid removing/inserting elements in a middle of a vector (very inefficient!), but for now it'll do.  

### Runtime

|              | Exec. Time (ms) - Clojure |
|--------------|--------------------------:|
| **Part One** |                 784.385166|
| **Part Two** |               27603.530041|