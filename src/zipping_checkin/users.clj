(ns zipping-checkin.users)

(def users (atom {}))

(def latest-id (atom 0))

(defn unique-id []
  (let [id @latest-id]
    (swap! latest-id inc)
    (str id)))

(defn register-user [name phone email]
  (let [user-id (unique-id)
        user {:id user-id, :name name, :phone phone, :email email}]
    (swap! users assoc user-id user)
    user-id))

(defn unregister-user [user-id]
  (swap! users dissoc user-id))

(defn query-user [user-id]
  (get @users user-id))

(defn query-users
  ([] (vals @users))
  ([user-ids]
     (vals (select-keys @users user-ids))))
