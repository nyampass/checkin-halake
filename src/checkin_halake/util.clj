(ns checkin-halake.util
  (:require [ring.util.response :refer [response]]))

(defn response-with-status [success? & {:as body}]
  (-> (merge {:status (if success? "success" "failure")}
             body)
      response))
