(ns checkin-halake.models.ticket
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :as mo]
            [monger.result :as mr]
            [environ.core :refer [env]])
  (:use [crypto.password.bcrypt :only [encrypt check]]
        checkin-halake.models.core))

(def ticket-types {:1day {:id :1day :nama "1日利用権"}})

(defn add-ticket-to-user [user ticket-type count]
  (let [ticket-key (str "tickets." (name ticket-type))]
    (mc/update db "users" {:_id (:_id user)} {mo/$inc {ticket-key count}})))

(defn use-ticket [user ticket-type]
  (let [ticket-key (str "tickets." (name ticket-type))
        result (mc/update db "users" {:_id (:_id user) ticket-key {mo/$gt 0}} {mo/$inc {ticket-key -1}})]
    (mr/updated-existing? result)))

(defn available-tickets [user]
  (let [tickets (:tickets (mc/find-one-as-map db "users" {:_id (:_id user)}))]
    (reduce (fn [m type]
              (if (> (tickets type) 0)
                m
                (dissoc m type)))
            tickets
            (keys tickets))))
