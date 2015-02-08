(ns checkin-halake.routes.admin
  (:refer-clojure :exclude [format])
  (:require [compojure.core :refer [defroutes GET POST PUT DELETE]]
            [checkin-halake.util :as util]
            [checkin-halake.models
             [users :as users]
             [admin :as admin]
             [ticket :as ticket]
             [events :as events]
             [checkin :as checkin]]
            [clj-time
             [format :as format]
             [local :as local]
             [coerce :as coerce]]))

(def format (format/formatter "yyyy-MM-dd HH:mm"))

(defn- str->datetime [s]
  (coerce/to-date (local/to-local-date-time (format/parse format s))))

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
              (util/response-with-status true :tickets tickets)))))
  (PUT "/users/:id/status/:status" {{:keys [id status]} :params}
       (let [status (keyword status)
             user (users/find-user id)]
         (when (and user (contains? users/user-statuses status))
           (users/set-user-status user status)
           (util/response-with-status true))))
  (POST "/events" {{:keys [title image-url event-at content-url]} :params}
        (if-let [event-at (try (str->datetime event-at)
                               (catch IllegalArgumentException _ nil))]
          (let [event (events/register-event title image-url event-at content-url)]
            (util/response-with-status true :event event))
          (util/response-with-status false :reason "日付フォーマットに誤りがあります")))
  (DELETE "/events/:id" {{:keys [id]} :params}
          (try
            (if (events/remove-event id)
              (util/response-with-status true)
              (util/response-with-status false :reason "該当するイベントがありません"))
            (catch IllegalArgumentException _
              (util/response-with-status false :reason "該当するイベントがありません")))))

(defn- wrap-check-admin [app]
  (fn [{{:keys [user]} :params :as req}]
    (if (:admin? user)
      (app req)
      (util/response-with-status false :reason "管理者権限がありません"))))

(def admin-routes
  (-> #'admin-routes*
      wrap-check-admin))
