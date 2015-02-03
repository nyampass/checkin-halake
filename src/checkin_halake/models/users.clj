(ns checkin-halake.models.users
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]])
  (:use [crypto.password.bcrypt :only [encrypt check]]
        checkin-halake.models.core))

(defn- fix-user [doc]
  (dissoc doc :password))

(defn register-user [email password name phone]
  (let [password (encrypt password)
        user {:_id email, :name name,
              :phone phone, :password password,
              :createdAt (java.util.Date.)}]
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
