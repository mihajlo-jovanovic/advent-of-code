# Day 5 - Print Queue

Playing with number sequences...

### Data Format

I ended up with a map of page numbers and their **predecessors** as rules, and a simple **list of lists** for page numbers. The data looks something like this:
 
 ```
{:rules {
    53 #{75 61 47 97}, 
    13 #{75 61 29 47 97 53}, 
    61 #{75 47 97}, 
    47 #{75 97}, 
    29 #{75 61 47 97 53}, 
    75 #{97}},
 :updates (
    (75 47 61 53 29)
    (97 61 53 29 13)
    (75 29 13)
    (75 97 47 61 53)
    (61 13 29)
    (97 13 75 29 47))
 }
```

## Part 1
> _What do you get if you add up the middle page number from those correctly-ordered updates?_


Once we parse the data into the data structure described above, we just need to write a predicate to filter out page numbers that are not valid according to the rules. This is pretty straightforward.

### Part 2
> _What do you get if you add up the middle page numbers after correctly ordering just those updates?_

Here, we need to do something more clever than trying all permutations. What I came up with is a simple algorithm that walks the sequence from left to right, looking for any numbers that are predecessors (come before) the current page number. If found, pull them all to the front, right ahead of the current element, and repeat.

The key insight to make this more efficient is that we don’t need to order the entire sequence, as we’re just looking for the middle element. So, we can return as soon as we reach the middle point of the list.

### Runtime

|              | Exec. Time (ms) |
|--------------|----------------:|
| **Part One** |         4.323708|
| **Part Two** |         7.806958|