(ns checkin-halake.sms
  (:require [twilio.core :as twilio]
            [environ.core :refer [env]]
            [clojure.string :as string]))

(defonce account-sid (env :twilio-sid))

(defonce auth-token (env :twilio-key))

(defonce from (env :twilio-from))

(defn- fix-tel [tel]
  (-> tel
      (string/replace #"\D" "")
      (string/replace #"^0" "+81")))

(defn send [to message]
  (twilio/with-auth account-sid auth-token
    (twilio/send-sms
     {:From from
      :To (fix-tel to)
      :Body message})))

;; (send "090-2586-0466" "認証番号:3344をアプリ画面で入力して下さい\n(30分間有効です)\nhttp://halake.com")
