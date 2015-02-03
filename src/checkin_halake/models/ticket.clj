(ns checkin-halake.models.ticket
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :as mo]
            [environ.core :refer [env]])
  (:use [crypto.password.bcrypt :only [encrypt check]]
        checkin-halake.models.core))

(def ticket-types {:1day {:id :1day :nama "1日利用権"}})

(defn add-ticket-to-user [user ticket-type count]
  (let [ticket-key (str "tickets." ticket-type)]
    (mc/update db "users" {:_id (:id user)} {mo/$inc {ticket-key count}})))

(defn use-ticket [user ticket-type]
  (let [ticket-key (str "tickets." ticket-type)]
    (mc/update db "users" {:_id (:id user) mo/$gt {ticket-type 0}} {mo/$inc {ticket-key -1}})))

  
