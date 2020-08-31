(ns scytale.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [promesa.core :as p]
            [scytale.components.start :refer [start-page]]
            [scytale.components.password-entry :refer [password-entry-modal]]
            [scytale.api :as api]))

(def ^:private active-note (r/atom nil))

(def ^:private start-page-create-note-exists-error
  (r/atom false))

(def ^:private password-modal-create-error
  (r/atom false))

(def ^:private initial-password-modal-config
  {:show false
   :identifier ""
   :slug ""
   :action :create
   :loading false})

(def ^:private password-modal-config
  (r/atom initial-password-modal-config))

(defn- handle-start-page-create
  [identifier slug]
  (when-not (zero? (count slug))
    (-> (api/note-exists slug)
        (p/then #(reset! password-modal-config
                         (merge @password-modal-config
                                {:show true
                                 :identifier identifier
                                 :slug slug
                                 :action :create})))
        (p/catch #(reset! start-page-create-note-exists-error true)))))

(defn- note-create-error
  []
  (swap! password-modal-config #(assoc % :loading false))
  (reset! password-modal-create-error true))

(defn- handle-password-modal-submit
  [password]
  (when-not (zero? (count password))
    (swap! password-modal-config #(assoc % :loading true))
    (-> (api/create-note (:slug @password-modal-config) password)
        (p/then #(reset! password-modal-config initial-password-modal-config))
        (p/catch note-create-error))))

(defn- app
  []
  [:div
   [:nav.navbar.has-shadow
    [:div.container
     [:div.navbar-brand
      [:p.navbar-item.is-size-4 "🔏 Scytale"]]]]
   [start-page handle-start-page-create start-page-create-note-exists-error]
   [password-entry-modal password-modal-config handle-password-modal-submit]])

(defn main
  []
  (rdom/render [app] (.getElementById js/document "app")))
