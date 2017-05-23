(defproject mg.dt/azip "0.1.0-SNAPSHOT"
  :description "Zippers for labeled unordered trees, such as nested maps"
  :url "https://github.com/dottedmag/azip"
  :license {:name "The MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :plugins [[lein-release "1.0.5"]]
  :lein-release {:deploy-via :clojars})
