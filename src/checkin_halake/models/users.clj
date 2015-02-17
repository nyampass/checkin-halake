(ns checkin-halake.models.users
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :as mo]
            [environ.core :refer [env]]
            [crypto.password.bcrypt :refer [encrypt check]]
            [checkin-halake.models.core :refer [db]]
            [checkin-halake.models.ticket :as ticket]))

(def user-statuses #{:member :dropin})

(def user-token-keys #{:apns})

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

(defn login [email password]
  (and (seq email) (seq password)
       (let [{crypted-password :password :as user}  (mc/find-one-as-map db "users" {:_id email})]
         (if (check password crypted-password)
           (fix-user user)))))

(defn query-users []
  (map fix-user
       (mc/find-maps db "users")))

(defn set-user-status [user status]
  (assert (contains? user-statuses status))
  (boolean (mc/update db "users" {:_id (:_id user)} {mo/$set {:status status}})))

(defn update-user-profile [user name phone]
  (if (or (seq name) (seq phone))
    (let [updates (-> {}
                      (cond-> (seq name) (assoc :name name))
                      (cond-> (seq phone) (assoc :phone phone)))]
    (boolean (mc/update db "users" {:_id (:_id user)} {mo/$set updates})))))

(defn update-user-token [user key value]
  (assert (contains? user-token-keys key))
  (if (seq value)
    (let [key' (str "tokens." (name key))]
      (boolean (mc/update db "users" {:_id (:_id user)} {mo/$set {key' value}})))))
