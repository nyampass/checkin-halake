(ns zipping-checkin.core
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [zipping-checkin [users :as users]
                             [checkin :as checkin]]
            [clojure.string :as str]))

(defroutes routes
  (GET "/users/register" {{:keys [name phone email]} :params}
       (let [user-id (users/register-user name phone email)
             user (users/query-user user-id)]
         (str "registered: " user)))
  (GET "/users/unregister" {{:keys [user-id]} :params}
    (let [user (users/query-user user-id)]
      (users/unregister-user user-id)
      (str "unregistered: " (:name user))))
  (GET "/users/all" req
    (str/join \newline (users/query-users)))
  (GET "/users/checkin" req
    (str/join \newline (checkin/query-checkin-users)))
  (GET "/api/checkin" {{:keys [user-id]} :params}
    (let [user (users/query-user user-id)]
      (checkin/checkin user-id)
      (str "{\"type\":\"checkin\", \"status\":\"success\", \"user\":\"" (:name user) "\"}")))
  (GET "/api/checkout" {{:keys [user-id]} :params}
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
