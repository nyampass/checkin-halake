(ns zipping-checkin.users)

(def users (atom {}))

(def latest-id (atom 0))

(defn unique-id []
  (let [id @latest-id]
    (swap! latest-id inc)
    id))

(defn register-user [name phone email]
  (let [user {:name name, :phone phone, :email email}
        user-id (unique-id)]
    (swap! users assoc user-id user)
    user-id))

(defn unregister-user [user-id]
  (swap! users dissoc user-id))

(defn query-user [user-id]
  (get @users user-id))

(defn query-users [user-ids]
  (select-keys @users user-ids))
