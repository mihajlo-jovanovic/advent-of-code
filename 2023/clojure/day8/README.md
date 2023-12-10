# Day 8

Another fun one to do in Clojure! Part 1 was straight-forward; totally fell for part 2 brute force and saw a few stack overflows.
One thing I'm still not clear on is why, as recur should be using tail recursion?

Things that could be improved:
- single parse-input function for both map and instructions
- p1 & p2 are basically the same but for the predicate; can be passed in as argument

Still, ~50 LoC is fairly concise. Clojure/Lisp just rock!

## Usage

```
lein uberjar
java -jar day8-0.1.0-standalone.jar
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
