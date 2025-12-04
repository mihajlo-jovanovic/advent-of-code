# Day 2: Gift Shop 

Looking for repeated sequences inside number ranges...
[Full description here:](https://adventofcode.com/2025/day/2)

## Part 1
Here I tried to avoid the brute-force method of checking all the numbers in a range. My idea was:

1. Filter out all ranges whose length is odd, as they cannot possibly consist of two equal repeating sequences.
2. For ranges with even length, find the base of the repeated sequence (for example, `123` in `123123`).
3. Check edge cases where the beginning or end of the repeated sequence might fall outside the main range.
4. Finally, combine everything into the full ID and add it to the total.

## Part 2
This part effectively invalidated my approach from Part 1. I ran out of time in the morning and came back to it later in the day. I decided to switch to an approach based on this idea:


```Clojure
(apply = (partition % (str id)))
```

So basically treat the id as a String and use partition to check for repeated sequences of various length.

I observed that the length of the repeated sequence has to be between 1 and half the original product ID length. I then filtered for lengths where the original product ID length is divisible by that length. After that, all I had to do was map those lengths to a function that checks whether an ID is invalid, flatten the results, and sum them.

## Usage

To run:
 
    $ lein uberjar & java -jar day2-0.1.0-standalone.jar

## License

Copyright © 2025 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
https://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
