(ns checkin-halake.core
  (:require [compojure.core :refer [defroutes routes context GET POST PUT]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response]]
            [checkin-halake.models [users :as users]]
            [checkin-halake.routes
             [users :refer [users-routes]]
             [admin :refer [admin-routes]]]
            [checkin-halake.util :as util]
            [environ.core :refer [env]]))

(defroutes api-routes*
  #'users-routes
  #'admin-routes)

(defonce headers-key (env :api-request-headers-key))

(defn wrap-api-authorized [app]
  (fn [{{x-halake-key "x-halake-key"} :headers :as req}]
    (if (= x-halake-key headers-key)
      (app req)
      (util/response-with-status false :reason "Not authorized"))))

(defn wrap-authenticate [app]
  (fn [{{:keys [email password]} :params :as req}]
    (if-let [user (users/login email password)]
      (app (assoc-in req [:params :user] user))
      (util/response-with-status false :reason "Email/Password combination is not valid"))))

(defn wrap-log [app]
  (fn [req]
    (let [res (app req)]
      (prn (.toString (java.util.Date.)) :req req :res res)
      res)))

(def api-routes
  (-> #'api-routes*
      wrap-authenticate
      wrap-api-authorized
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
