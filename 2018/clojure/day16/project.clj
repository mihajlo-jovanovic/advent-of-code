(defproject day16 "0.1.0-SNAPSHOT"
  :description "Day 16"
  :url "https://github.com/mihajlo-jovanovic/advent-of-code"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.3"]]
  :main ^:skip-aot day16.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
