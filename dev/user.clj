(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.repl :refer :all]
            [clojure.pprint :refer [pp pprint]]
            [zipping-checkin [core :refer :all]
                             [users :refer :all]
                             [checkin :refer :all]]))
