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
  (PUT "/users/me/tickets/:type" {{:keys [type used user]} :params}
       (let [type (keyword type)]
         (when (contains? ticket/ticket-types type)
           (if (ticket/use-ticket user type)
               (let [tickets (ticket/available-tickets user)]
                 (util/response-with-status true :tickets tickets))
               (util/response-with-status false :reason "利用可能なチケットがありません")))))
  (PUT "/users/me" {{:keys [name phone user]} :params}
       (let [response (users/update-user-profile user name phone)]
         (if (users/update-user-profile user name phone)
           (util/response-with-status true)
           (util/response-with-status false :reason "変更に失敗しました"))))
  (POST "/login" {{:keys [user]} :params :as req}
        (let [tickets (ticket/available-tickets user)]
          (util/response-with-status true :user (assoc user :tickets tickets))))
  (POST "/checkin" {{:keys [user]} :params}
        (checkin/checkin (:_id user))
        (util/response-with-status true :user user))
  (POST "/checkout" {{:keys [user]} :params}
        (checkin/checkout (:_id user))
        (util/response-with-status true :user user))
  (GET "/events" _
       (util/response-with-status true :events (events/query-events))))
