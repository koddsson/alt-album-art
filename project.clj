(defproject reddit-freshalbumart "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure                     "1.6.0"]
                 [org.clojure/clojurescript            "0.0-2843"]
                 [com.andrewmcveigh/cljs-time             "0.3.2"]
                 [alandipert/storage-atom                 "1.2.4"]
                 [figwheel                                "0.2.5"]
                 [org.clojure/core.async "0.1.256.0-1bf8cf-alpha"]]

  :node-dependencies [[source-map-support "0.2.8"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-figwheel "0.2.5-SNAPSHOT"]]

  :source-paths ["src" "target/classes"]

  :clean-targets ["out" "out-adv"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {
                :main reddit-freshalbumart.core
                :output-to "resources/public/js/compiled/index.js"
                :output-dir "resources/public/js/compiled/out"
                :optimizations :none
                :asset-path "js/compiled/out"
                :cache-analysis true
                :source-map true}}
             {:id "release"
              :source-paths ["src"]
              :compiler {
                :main reddit-freshalbumart.core
                :output-to "out-adv/reddit_freshalbumart.min.js"
                :output-dir "out-adv"
                :optimizations :advanced
                :pretty-print false}}]})
