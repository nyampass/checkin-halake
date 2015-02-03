(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [clojure.repl :refer :all]
            [clojure.pprint :refer [pp pprint]]
            [checkin-halake [core :refer :all]]
            [checkin-halake.models
                            [users :as users]
                            [checkin :as checkin]]))

(comment
  (reset! users/users
          {"0" {:id "0"
                :name "sohta"
                :phone "0112223333"
                :password "hoge"
                :email "shogo@nyampass.com"}
           "1" {:id "1"
                :name "tnobo"
                :phone "01234578"
                :password "pass"
                :email "tokusei@nyampass.com"}}))
