# checkin-halake

A Clojure library designed to ... well, that part is up to you.

## Usage

Setup .lein-env file in the project directory

{:twilio-sid "<Twilio Acccount Sid>"
 :twilio-key "<Twilio Auth Key>"
 :twilio-from "<Twilio From Tel>"}

If you want to make push notifications available, put the generated *.p12/*.cer files into the directory `resources/private` and configure the environment variable `$HALAKE_SSL_KEY_PASS`.

## License

Copyright © 2014-2015 Nyampass Co. Ltd.

Distributed under the Eclipse Public License version 1.0.
