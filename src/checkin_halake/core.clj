(ns checkin-halake.core
  (:require [compojure.core :refer [defroutes routes context GET POST]]
            [compojure.route :refer [not-found files]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [response]]
            [checkin-halake.models
             [users :as users]
             [events :as events]
             [checkin :as checkin]]
            [clojure.string :as str]
            [environ.core :refer [env]]))

(defn- response-with-status [success? & {:as body}]
  (-> (merge {:status (if success? "success" "failure")}
             body)
      response ))
 
(defroutes api-routes*
  (POST "/users" {{:keys [name password phone email]} :params}
        (let [user (users/register-user email password name phone)]
          (response-with-status (boolean user) :user user)))
  (GET "/users" _
       (response-with-status true :users (users/query-users)))
  (POST "/login" {{:keys [email password]} :params :as req}
        (prn :login email password req)
        (if-let [user (users/login email password)]
          (response-with-status true :user user)
          (response-with-status false :reason "Email/Password combination is not valid")))
  (POST "/checkin" {{:keys [email password]} :params}
       (let [user (users/login email password)]
         (if (and user (checkin/checkin (:_id user)))
           (response-with-status :user user)
           (response-with-status false :reason "Email/Password combination is not valid"))))
  (POST "/checkout" {{:keys [email password]} :params}
       (let [user (users/login email password)]
         (if (and user (checkin/checkout (:_id user)))
           (response-with-status :user user)
           (response-with-status false :reason "Email/Password combination is not valid"))))
  (GET "/events" _
       response-with-status :evnets (events/query-events)))
;;  (not-found "Not found"))

(defonce headers-key (env :api-request-headers-key))

(defn wrap-api-auth [app]
  (fn [{{x-halake-key "x-halake-key"} :headers :as req}]
    (if (= x-halake-key headers-key)
      (app req)
      (response-with-status false :reason "Not authorized"))))

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
              (GET "/")
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
