(ns zipping-checkin.core
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defroutes app
  (GET "/" req "Hello, World!")
  (not-found "NOT FOUND"))

(defn -main []
  (let [port (Long/parseLong (get (System/getenv) "PORT" "8080"))]
    (run-jetty app {:port port})))
