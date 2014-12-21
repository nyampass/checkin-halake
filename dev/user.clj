(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.repl :refer :all]
            [clojure.pprint :refer [pp pprint]]
            [zipping-checkin [core :refer :all]
                             [users :as users]
                             [checkin :as checkin]]))

(reset! users/users
       {"0" {:name "sohta"
             :phone "0112223333"
             :email "shogo@nyampass.com"}
        "1" {:name "tnobo"
             :phone "01234578"
             :email "tokusei@nyampass.com"}})
(reset! users/latest-id 2)
