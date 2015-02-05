(ns checkin-halake.core
  (:require [compojure.core :refer [defroutes routes context GET POST PUT]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response]]
            [checkin-halake.routes
             [users :as users]
             [admin :as admin]]
            [checkin-halake.util :as util]
            [environ.core :refer [env]]))

(defroutes api-routes*
  #'users/users-routes
  #'admin/admin-routes)

(defonce headers-key (env :api-request-headers-key))

(defn wrap-api-auth [app]
  (fn [{{x-halake-key "x-halake-key"} :headers :as req}]
    (if (= x-halake-key headers-key)
      (app req)
      (util/response-with-status false :reason "Not authorized"))))

(defn wrap-log [app]
  (fn [req]
    (let [res (app req)]
      (prn (.toString (java.util.Date.)) :req req :res res)
      res)))

(def api-routes
  (-> #'api-routes*
      wrap-api-auth
      wrap-json-response
      (wrap-defaults api-defaults)))

(def app (-> (routes
              (GET "/" _)
              (context "/api" _
                       api-routes))
             wrap-log))

(def app-with-reload
  (ring.middleware.reload/wrap-reload #'app))

(defn -main []
  (let [port (Long/parseLong (get (System/getenv) "PORT" "8080"))
        app (if (= (env :ring-reload) "true")
              #'app
              #'app-with-reload)]
    (run-jetty #'app {:port port :join? false})))
