(ns checkin-halake.routes.admin
  (:require [compojure.core :refer [defroutes GET POST PUT]]
            [checkin-halake.util :as util]
            [checkin-halake.models
             [users :as users]
             [admin :as admin]
             [ticket :as ticket]
             [events :as events]
             [checkin :as checkin]]))

(defroutes ^:private admin-routes*
  (POST "/users" {{:keys [name password phone email]} :params}
        (let [user (users/register-user email password name phone)]
          (util/response-with-status (boolean user) :user user)))
  (GET "/users" _
       (util/response-with-status true :users (users/query-users)))
  (POST "/users/:id/tickets/:type" {{:keys [id type count]} :params}
        (let [type (keyword type)
              user (users/find-user id)
              count (Long/parseLong count)]
          (when (and user (contains? ticket/ticket-types type))
            (ticket/add-ticket-to-user user type count)
            (let [tickets (ticket/available-tickets user)]
              (util/response-with-status true :tickets tickets))))))

(defn- wrap-check-admin [app]
  (fn [{:keys [user] :as req}]
    (if (:admin? user)
      (app req)
      (util/response-with-status false :reason "Not admin user"))))

(def admin-routes
  (-> #'admin-routes*
      wrap-check-admin))
