# Day 22: Monkey Market

Definitely an easier one; was able to do this one while watching football with my son...

### Part 1

This was really easy and fast to do in Clojure: just write the pseudo-random generator function as prescribed and we're off to the races...

### Part 2

So for this one I first considered brute force approach (as one does :-) only because I didn't really want to think to hard. It turns out
it is doable as long as we do a couple of small optimizations:

Only consider a pool of possible sequences (reduced the search space to about a quarter)
Split four ways (one per core); on my (fairly new ) MB Pro, this ran for around 22 mins

Not great, but I'll take it...
