# Day 24  - "Blizzard Maze"

Here I was going for an interesting solution - not necessarily the most performant.
While it won't win any awards, I was actually kind of pleased with the relative 
simplicity - that is to say, it kind of came out the way I thought about the problem, 
sans any clever tricks and such. Just plain brute force listing of all states, with 
ability to reduce to a smaller set if needed (see `keep-best-states` function).

Clojure is just such a cool and elegant language...

## Usage

No frills - just run `lein uberjar` or simply the main method:

```
$ lein uberjar
$ java -jar day24-0.1.0-standalone.jar
```

## License

Copyright © 2023 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
