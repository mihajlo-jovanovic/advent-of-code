# Day 4

This was a fun puzzle to solve in Clojure. Still a bit rusty so took me a while to get the hang of how to parse input. I ended up manually removing 
`Card X:`` prefix, just because...

Also, probably should've used maps instead of vectors for pairs of [id, num-of-winnings]. And nested reduce invocations are not great.

But it:
- works
- functional (no for/if-else crap)
- fairly concise (~50 LoC)

## Usage

Run using Leinengen:

    $ lein run

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
