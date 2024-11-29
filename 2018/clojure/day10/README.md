# Day 10

Used REPL to find the point in sequence of seconds where area is smallest, then fast-forward and display.

Interestingly, this fails with stack overflow error unless stack space is increased:

```
java -Xss10m -jar target/uberjar/day10-0.1.0-SNAPSHOT-standalone.jar
```
