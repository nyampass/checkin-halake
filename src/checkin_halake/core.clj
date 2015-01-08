(ns checkin-halake.core
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [checkin-halake [users :as users]
             [checkin :as checkin]]
            [clojure.string :as str]
            [environ.core :refer [env]]))

(defroutes routes
  (POST "/api/users" {{:keys [name password phone email]} :params}
        (let [user (users/register-user name password phone email)]
          (str "registered: " user)))
  (GET "/api/users" _
       (users/query-users))
  (POST "/api/login" {{:keys [email password]} :params}
        (users/login email password))
  (POST "/api/checkin" _
        (str/join \newline (checkin/query-checkin-users)))
  (POST "/api/checkin" {{:keys [email password]} :params}
       (if-let [user (users/login email password)]
         (do
           (checkin/checkin (:_id user))
           (str "{\"type\":\"checkin\", \"status\":\"success\", \"user\":\"" (:name user) "\"}"))))
  (POST "/api/checkout" {{:keys [email password]} :params}
       (let [user (users/login email password)]
         (checkin/checkout (:_id user))
         (str "{\"type\":\"checkout\", \"status\":\"success\", \"user\":\"" (:name user) "\"}")))
  (not-found "Not found"))

(defonce headers-key (env :api-request-headers-key))

(defn wrap-auth [app]
  (fn [{{x-halake-key "x-halake-key"} :headers :as req}]
    (if (= x-halake-key headers-key)
      (app req))))

;;    {:a 3}))

;;;    {}(app req)))

(defroutes app
  (-> #'routes
      wrap-auth
      (wrap-defaults api-defaults)))

(def app-with-reload
  (ring.middleware.reload/wrap-reload #'app))

(defn -main []
  (let [port (Long/parseLong (get (System/getenv) "PORT" "8080"))
        app (if (= (env :ring-reload) "true")
              #'app
              #'app-with-reload)]
    (run-jetty #'app {:port port :join? false})))
