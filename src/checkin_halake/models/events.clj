(ns checkin-halake.models.events
  (:require [monger.collection :as mc]
            [environ.core :refer [env]])
  (:use checkin-halake.models.core))

(defn- fix-event [doc]
  (dissoc doc :password))

(defn register-event [title image-url event-at content-url]
  (let [event {:title title, :image-url image-url,
               :event-at event-at, :content-url content-url,
               :createdAt (java.util.Date.)}]
    (mc/insert-and-return db "events" event)))

;; (register-event "Swift開発講座" "/images/evnets/inside_halake.png" (java.util.Date ) "taro@email.com" "hoge" "Taro" "090")

(defn query-events []
  (map (mc/find-maps db "events")))

;; (query-events)
