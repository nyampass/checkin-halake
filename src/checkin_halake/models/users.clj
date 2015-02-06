(ns checkin-halake.models.users
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]]
            [checkin-halake.models.ticket :as ticket])
  (:use [crypto.password.bcrypt :only [encrypt check]]
        checkin-halake.models.core))

(defn- fix-user [doc]
  (dissoc doc :password))

(def initial-tickets
  (zipmap (keys ticket/ticket-types)
          (repeat 0)))

(defn find-user [id]
  (fix-user (mc/find-one-as-map db "users" {:_id id})))

(defn register-user [email password name phone]
  (let [password (encrypt password)
        user {:_id email, :name name,
              :phone phone, :password password,
              :created-at (java.util.Date.)
              :tickets initial-tickets}]
    (-> (mc/insert-and-return db "users" user)
        fix-user)))

;; (register-user "taro@email.com" "hoge" "Taro" "090")

(defn login [email password]
  (and (seq email) (seq password)
       (let [{crypted-password :password :as user}  (mc/find-one-as-map db "users" {:_id email})]
         (if (check password crypted-password)
           (fix-user user)))))

;; (login "taro@email.com" "hoge")

(defn query-users []
  (map fix-user
       (mc/find-maps db "users")))

;; (query-users)
