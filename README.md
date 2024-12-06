# Advent of Code

My solutions to the [Advent of Code](https://adventofcode.com/) puzzles, mostly in Clojure these days but occasionally also some in Rust and (hopefully,
eventually) Python, Golang, Kotlin...

## Installation Instructions

### Clojure

You will need [Leinenger](https://leiningen.org/) and Java. I use latest release of [Eclipse Temurin](https://adoptium.net/temurin/releases/) supported by the Adoptium community.

* Note there are now newer tools in the Clojure ecosystem than Leinenger, like the [Clojure CLI](https://clojure.org/guides/deps_and_cli/) and tools.build, which use [deps.edn](https://clojure.org/reference/deps_edn/) file to declare and manage dependencies. They seem geared towards more complex projects and bring with them a learning curve cost which I am not willing to pay at the moment. Leinenger works for what I need to do w.r.t. AoC.

#### Then to build (while inside specific project/day directory):

```bash
lein compile
```

To run tests (if any):

```bash
lein test
```

To produce an uberjar that can be ran directly:

```bash
lein uberjar
java -jar target/uberjar/day[enter-day-here]-0.1.0-SNAPSHOT-standalone.jar
```

### Rust

You will (obviously) need Rust, typically installed via [rustup](https://www.rust-lang.org/tools/install/). I also use an awesome [cargo-aoc](https://github.com/gobanos/cargo-aoc/) CLI from gobanos, mostly to aid in downloading input and structuring my solutions.

Then to run (inside project workspace):

```
cargo aoc
```

To run tests:

```
cargo test
```

To run a solution to a specific day:

```
cargo aoc -d 1
```

* For additional command options please consult cli help.

### Why Clojure?

As I mentioned earlier, most of my solutions are written in Clojure and occasionally Rust. I started learning Rust in 2020 and completed all my solutions in Rust for that year. However, as the years progressed, I increasingly started relying more on Clojure.

There are several reasons for this: first and foremost, I really enjoy programming in Clojure. I find it to be a beautiful and elegant language. It feels especially well-suited for these types of problems where, to paraphrase Rich Hickey, *you really want to think about how many of those incidental complexity "balls" you want in the air* while wrestling with an already complex puzzle.

Rust certainly has its advantages, and I still use it occasionally for tasks like complex low-level parsing (I’m a fan of the [nom](https://docs.rs/nom/latest/nom/) parsing library!) or implementing low-level data structures, such as circular buffers. However, for most other tasks, I prefer modeling the problem domain in Clojure first.

I realize many developers successfully use Python for Advent of Code, and I must admit I'm a little jeleous and wish I knew it better, strickly from a speed programming perspective. I do find it does take more time (at least for me) to formulate solutions in a **Functional** style using constructs like **map**, **filter**, and **reduce**, instead of imperative approaches with **if/else** statements and **for** loops. But I would argue result is more correct, readable code (even if it takes a few mins longer to crank out).

That's all for now. If you have any questions, comments, or feedback, feel free to reach out. Happy coding!