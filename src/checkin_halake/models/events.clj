(ns checkin-halake.models.events
  (:require [monger.collection :as mc]
            [checkin-halake.models.core :refer [db]]))

(defn- fix-event [event]
  (-> event
      (assoc :id (str (:_id event))) ;; FIXME: :id is a necessary field?
      (dissoc :_id)))

(defn register-event [title image-url event-at content-url]
  (let [event {:title title, :image-url image-url,
               :event-at event-at, :content-url content-url,
               :created-at (java.util.Date.)}]
    (fix-event (mc/insert-and-return db "events" event))))

;; (register-event "Swift開発講座" "/images/evnets/inside_halake.png" (java.util.Date ) "taro@email.com" "hoge" "Taro" "090")

(defn query-events []
  (map fix-event (mc/find-maps db "events")))

;; (query-events)
