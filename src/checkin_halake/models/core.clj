(ns checkin-halake.models.core
  (:require [monger.core :as mg]
            [environ.core :refer [env]]))

(defonce db ((mg/connect-via-uri (env :mongodb-uri)) :db))

