# Day 23

Part 2 took ~20 mins on my (Intel core) Macbook Pro circa 2018...could be optimized via edge compaction but not now...

(6494
"Elapsed time: 1083786.214207 msecs")

Update: Since I had some time, ported the same solution over to Rust and ran on my new M3:

```
Day 23 - Part 2: 6494
        generator: 353.125µs,
        runner: 14.764475667s
```

## Usage

Build & run:

    $ lein uberjar && java -jar ...
