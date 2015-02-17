(ns checkin-halake.util
  (:require [ring.util.response :refer [response]]
            [clojure.java.io :refer [resource]]
            [environ.core :refer [env]]
            [herolabs.apns
             [push :as push]
             [ssl :as ssl]
             [message :as message]]))

(defn response-with-status [success? & {:as body}]
  (-> (merge {:status (if success? "success" "failure")}
             body)
      response))

(defn connect-to-push-service []
  (let [store (ssl/keystore :key-path (resource "private/apns.p12")
                            :key-pass (env :halake-ssl-key-pass)
                            :cert-path (resource "private/apns.cer"))
         ctx (->> (ssl/naive-trust-managers :trace true)
                  (ssl/ssl-context :keystore store :trust-managers))]
    (push/create-connection (push/dev-address) ctx)))

(defn notify
  ([device-token message]
   (notify nil device-token message))
  ([conn device-token message]
   (let [conn (or conn (connect-to-push-service))
         message (-> (message/to device-token)
                     (message/with-standard-alert message))]
     (push/send conn message))))
