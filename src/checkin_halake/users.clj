(ns checkin-halake.users
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]])
  (:use [crypto.password.bcrypt :only [encrypt check]]))

(defonce db ((mg/connect-via-uri (env :mongodb-uri)) :db))

(defn- fix-user [doc]
  (dissoc doc :password))

(defn register-user [name password phone email]
  (let [password (encrypt password)
        user {:_id email, :name name,
              :phone phone, :password password,
              :createdAt (java.util.Date.)}]
    (-> (mc/insert-and-return db "users" user)
        fix-user)))

(defn login [email password]
  (let [{crypted-password :password :as user}  (mc/find-one-as-map db "users" {:_id email})]
    (if (check password crypted-password)
      (fix-user user))))

(defn query-users []
  (map fix-user
       (mc/find-maps db "users")))

;; (query-users)
