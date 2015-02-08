(ns checkin-halake.models.users
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :as mo]
            [environ.core :refer [env]]
            [crypto.password.bcrypt :refer [encrypt check]]
            [checkin-halake.models.core :refer [db]]
            [checkin-halake.models.ticket :as ticket]))

(def user-statuses #{:member :dropin})

(defn- fix-user [doc]
  (-> (dissoc doc :password)
      (some-> identity
              (update-in [:status] (fnil keyword :dropin)))))

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

(defn set-user-status [user status]
  (assert (contains? user-statuses status))
  (boolean (mc/update db "users" {:_id (:_id user)} {mo/$set {:status status}})))
