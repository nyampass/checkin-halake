(defproject zipping-checkin "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/athos/zipping-checkin"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [org.clojure/data.json "0.2.5"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.8"]]
                   :source-paths ["dev"]}})
