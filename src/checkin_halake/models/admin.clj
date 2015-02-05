(ns checkin-halake.models.admin
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [environ.core :refer [env]]
            [checkin-halake.models.core :refer [db]]
            [crypto.password.bcrypt :refer [encrypt check]]))

(defn- fix-admin [admin]
  (dissoc admin :password))

(defn register-admin [email password name]
  (let [password (encrypt password)
        admin {:_id email, :password password, :name name}]
    (-> (mc/insert-and-return db "admins" admin)
        fix-admin)))

(defn login [email password]
  (assert (and (seq email) (seq password)))
  (let [{crypted-password :password :as admin} (mc/find-one-as-map db "admins" {:_id email})]
    (when (check password crypted-password)
      (fix-admin admin))))

(defn query-admins []
  (map fix-admin (mc/find-maps db "admins")))
