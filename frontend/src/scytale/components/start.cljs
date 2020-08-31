(ns scytale.components.start
  (:require [reagent.core :as r]
            [clojure.string :as string]))

(def ^:private input-value (r/atom ""))

(defn- sluggify
  [s]
  (-> (string/lower-case s)
      (string/replace #"\W+" "-")
      (string/replace #"_" "-")))

(defn- handle-input-change
  [value note-exists-error]
  (reset! note-exists-error false)
  (reset! input-value value))

(defn start-page
  [create-handler note-exists-error]
  [:div
   [:div.hero.is-medium.is-info.is-bold
    [:div.hero-body
     [:div.container
      [:p.title "Private and secure notes made easy"]
      [:p.subtitle "Create and share encrypted notes. Not even we will know what you are writing down."]]]]
   [:div.container.pt-6
    [:div.is-fullwidth.p-6
     [:p.is-size-3.mb-4 "Enter the name of your new or existing note:"]
     [:input.input.is-large
      {:type "text"
       :class (when @note-exists-error "is-danger")
       :placeholder "Get your hands off my notes ..."
       :on-change #(handle-input-change (-> % .-target .-value) note-exists-error)
       :value @input-value}]
     [:div.has-text-centered
      [:button.button.is-info.mt-4.mr-4.is-large "Open"]
      [:button.button.is-success.mt-4.is-large
       {:on-click #(create-handler @input-value (sluggify @input-value))}
       "Create"]]
     (when @note-exists-error
       [:p.mt-4.has-text-danger "The name you entered is already taken by somebody else. Please use a different name."])]]])
