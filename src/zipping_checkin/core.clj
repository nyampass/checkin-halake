(ns zipping-checkin.core
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [zipping-checkin [users :as users]
                             [checkin :as checkin]]))

(defroutes routes
  (GET "/users/register" {{:keys [name phone email]} :params}
       (let [user-id (users/register-user name phone email)
             user (users/query-user user-id)]
         (str "registered: " user)))
  (GET "/users/unregister" {{:keys [user-id]} :params}
    (let [user (users/query-user user-id)]
      (users/unregister-user user-id)
      (str "unregistered: " (:name user))))
  (GET "/checkin/users" req
    (pr-str (checkin/query-checkin-users)))
  (GET "/api/checkin/checkin" {{:keys [user-id]} :params}
    (let [user (users/query-user user-id)]
      (checkin/checkin user-id)
      (str "{\"type\":\"checkin\", \"status\":\"success\", \"user\":\"" (:name user) "\"}")))
  (GET "/api/checkin/checkout" {{:keys [user-id]} :params}
    (let [user (users/query-user user-id)]
      (checkin/checkout user-id)
      (str "{\"type\":\"checkout\", \"status\":\"success\", \"user\":\"" (:name user) "\"}")))
  (not-found "NOT FOUND"))

(defroutes app
  (-> routes
      (wrap-defaults api-defaults)))

(defn -main []
  (let [port (Long/parseLong (get (System/getenv) "PORT" "8080"))]
    (run-jetty app {:port port})))
