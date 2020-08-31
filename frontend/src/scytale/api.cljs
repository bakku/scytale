(ns scytale.api
  (:require [ajax.core :as ajax]
            [promesa.core :as p]))

(goog-define BASEURL "http://localhost:8080")

(defn note-exists
  [identifier]
  (p/create
   (fn [resolve reject]
     (ajax/GET (str BASEURL "/api/notes/" identifier "/exists")
               {:handler resolve
                :error-handler reject
                :response-format (ajax/ring-response-format)}))))

(defn create-note
  [identifier password]
  (p/create
   (fn [resolve reject]
     (ajax/POST (str BASEURL "/api/notes")
                {:handler resolve
                 :error-handler reject
                 :response-format (ajax/ring-response-format)
                 :format :json
                 :params {"accessKey" password
                          "identifier" identifier
                          "content" ""}}))))
