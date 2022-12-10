(defproject day9 "0.1.0-SNAPSHOT"
  :description "Advent of Code 2022 Day 9 (head tail tracking)"
  :url "http://adventofcode.com"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :repl-options {:init-ns day9.core}
  :main ^:skip-aot day9.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
