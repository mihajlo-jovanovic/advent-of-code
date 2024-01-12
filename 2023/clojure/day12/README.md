# Day 12 - : Hot Springs

This one took me a while...big insight that helped with part 2 was to remove entire contiguous groups on \#. That 
really helped along with using memoize (which I had not used before in Clojure).

My first attempt at part one was fairly naive recursive solution which obviously did not work on part 2. At one point
I had an idea to derive answers from running part 1 and folding only once (see copy2)...it appears there is a way
to calculate based on those two, something like count-p2(copy2) ^ 4 / count-p1 ^ 3...but once I got the recursion
right it was no longer necessary.

I know some folks on Reddit find interesting solutions involving DP, Finate State Machines and tables...but honestly
I found those really difficult to follow. What I ended up is really the classic approach IMHO - recursion + memoization
but needs to be carefully crafted to really make use of smaller-size inputs. That took me a few attempts to get right.

All in all, a fun puzzle!

## Installation

Download from http://example.com/FIXME.

## Usage

    $ lein uberjar && java -jar target/default+uberjar/day12-0.1.0-SNAPSHOT-standalone.jar

## Running Tests

    $ lein tests
