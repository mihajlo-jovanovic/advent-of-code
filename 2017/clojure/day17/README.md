# day17 - Spinlock

This one was nice and easy to do in Clojure. All I had to do was write a function to insert elements
into a circular buffer. Decided on using a list in Clojure (instead of some customized zipper-like
structure) for simplicity.

Ended up adding one helper function that I should probably move to some utility module, as it may come in
handy in the future.

Main piece of insight was that there's no need to actually build the buffer in order to just answer the
question on what element is at position 1.

## Performance

| Test         | Execution Time (ms) |
|--------------|---------------------:|
| **Part One** |                    0 |
| **Part Two** |            1494.7459 |

*Environment Details:*
- *Date:* Nov 16, 2024  
- *Machine:* MacBook Pro M3, 18GB RAM, macOS Sequoia 15.1  
- *Java Version:*  
  - *OpenJDK 23.0.1 (2024-10-15)*  
  - *OpenJDK Runtime Environment: Temurin-23.0.1+11*  
  - *OpenJDK 64-Bit Server VM: Temurin-23.0.1+11 (build 23.0.1+11, mixed mode, sharing)*


## Usage

    $ lein uberjar
    $ java -jar target/uberjar/day17-0.1.0-SNAPSHOT-standalone.jar