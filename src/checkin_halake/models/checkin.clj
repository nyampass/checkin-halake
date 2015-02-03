(ns checkin-halake.models.checkin
  (:require [checkin-halake.models.users :as users])
  (:use checkin-halake.models.core))

(def checkin-users (atom #{}))

(defn checkin [user-id]
  (swap! checkin-users conj user-id))

(defn checkout [user-id]
  (swap! checkin-users disj user-id))

(defn query-checkin-users []
  (users/query-users @checkin-users))
