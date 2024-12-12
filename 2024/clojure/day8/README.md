# Day 8 - Resonant Collinearity 

Antennas & antinodes: Counting distinct points along the same line in a 2D grid...

### Data Format

I've been re-using the same grid representation with small modifications: in this case, it's a map
with :antennas and :size as keys; antennas value is another map with alpha-numeric `frequencies` as keys, and 
list of [x y] coordinates as values. Here is an example:

'''
{:anthenas {\0 #{[7 3] [5 2] [8 1] [4 4]}, \A #{[8 8] [9 9] [6 5]}}, :size 12}
'''

### Part 1

I first wrote a simple `extend-diagonal` to return extra points that are equal distance to the original two points (given as input). Then
I used `combinations` function from **clojure.math.combinatorics** to generate all pairs of points; this is what I loop over in a tail-recursive function that simply adds all the discovered points to a set, finally returning count of distinct `antinodes.`

### Part 2

This is almost exactly the same but instead of two points we needed to generate all points within the same distance as the original two, within valid bounds of the 2D grid. I used GitHub Copilot and ChatGPT o1 to experiment with generating this function in a simple and readable way, editing the output until ultimately arriving at the committed version. Interestingly it initially produced a more complex than necessary version involving `gcd` function (!), which i removed.

All in all, fairly straight-forward solution to an easy to medium difficult puzzle.

### Runtime

|              |           Exec. Time (ms) | 
|--------------|--------------------------:|
| **Part One** |                   11.3085 |
| **Part Two** |                    6.8597 |


