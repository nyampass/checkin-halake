(ns zipping-checkin.core
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

(defroutes routes
  (GET "/" req "Hello, World!")
  (not-found "NOT FOUND"))

(defroutes app
  (-> routes
      (wrap-defaults api-defaults)))

(defn -main []
  (let [port (Long/parseLong (get (System/getenv) "PORT" "8080"))]
    (run-jetty app {:port port})))
