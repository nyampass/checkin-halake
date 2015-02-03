(defproject checkin-halake "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/nyampass/checkin-halake"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [ring/ring-devel "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [org.clojure/data.json "0.2.5"]
                 [twilio-api "1.0.0"]
                 [com.novemberain/monger "2.0.1"]
                 [crypto-password "0.1.3"]
                 [environ "1.0.0"]
                 [clj-time "0.9.0"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.8"]]
                   :source-paths ["dev"]}})
