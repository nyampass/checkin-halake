(ns checkin-halake.routes.users
  (:require [compojure.core :refer [defroutes GET POST PUT]]
            [checkin-halake.util :as util]
            [checkin-halake.models
             [users :as users]
             [ticket :as ticket]
             [events :as events]
             [checkin :as checkin]]))

(defroutes users-routes
  (POST "/users" {{:keys [name password phone email]} :params}
        (let [user (users/register-user email password name phone)]
          (util/response-with-status (boolean user) :user user)))
  (PUT "/users/me/tickets/:type" {{:keys [type used email password]} :params}
       (let [type (keyword type)]
         (when (contains? ticket/ticket-types type)
           (if-let [user (users/login email password)]
             (if (ticket/use-ticket user type)
               (let [tickets (ticket/available-tickets user)]
                 (util/response-with-status true :tickets tickets))
               (util/response-with-status false :reason "No available tickets"))
             (util/response-with-status false :reason "Email/Password combination is not valid")))))
  (POST "/login" {{:keys [email password]} :params :as req}
        (prn :login email password req)
        (if-let [user (users/login email password)]
          (let [tickets (ticket/available-tickets user)]
            (util/response-with-status true :user (assoc user :tickets tickets)))
          (util/response-with-status false :reason "Email/Password combination is not valid")))
  (POST "/checkin" {{:keys [email password]} :params}
       (let [user (users/login email password)]
         (if (and user (checkin/checkin (:_id user)))
           (util/response-with-status true :user user)
           (util/response-with-status false :reason "Email/Password combination is not valid"))))
  (POST "/checkout" {{:keys [email password]} :params}
       (let [user (users/login email password)]
         (if (and user (checkin/checkout (:_id user)))
           (util/response-with-status true :user user)
           (util/response-with-status false :reason "Email/Password combination is not valid"))))
  (GET "/events" _
       (util/response-with-status true :events (events/query-events))))
