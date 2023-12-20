# day19

This one took me a long time...first I tried generating the accepted? predicate in code; that 
ultimately worked for test input but not for the main one (ran into 64kb sizs limit). Fortunately
was able to modify same meta-programming function to a predicate. 

For Part 2, took me forever to write a recursive function that produces constraint ranges. Copilot 
was helpful in generating code that calculates number of combinations.

All in all, fun one but a doozy!

## Usage

```
lein uberjar
java -jar target/default+uberjar/day19-0.1.0-SNAPSHOT-standalone.jar
```

## License

Copyright © 2023 Mihajlo Jovanovic

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
