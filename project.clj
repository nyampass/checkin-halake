(defproject checkin-halake "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/nyampass/checkin-halake"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [ring/ring-devel "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [ring-server "0.3.1"]
                 [org.clojure/data.json "0.2.5"]
                 [twilio-api "1.0.0"]
                 [com.novemberain/monger "2.0.1"]
                 [crypto-password "0.1.3"]
                 [environ "1.0.0"]
                 [clj-time "0.9.0"]]
  :plugins [[lein-ring "0.9.0"]
            [lein-environ "1.0.0"]]
  :ring {:handler checkin-halake.core/app}
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.8"]]
                   :source-paths ["dev"]
                   :env {:dev true
                         :mongodb-uri "mongodb://localhost/checkin-halake"
                         :api-request-headers-key "checkin-halake-api"}}})
