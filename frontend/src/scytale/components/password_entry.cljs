(ns scytale.components.password-entry
  (:require [reagent.core :as r]
            [clojure.string :as string]))

(def ^:private password-value (r/atom ""))

(defn- hide
  [config]
  (reset! config (merge @config {:show false})))

(defn- password-entry-modal-update
  [_ args]
  (let [config (get args 1)]
    (when-not (:show @config)
      (reset! password-value ""))))

(defn- password-entry-modal-render
  [config submit-handler]
  (let [{:keys [show identifier action loading]} @config]
    [:div.modal {:class (when show "is-active")}
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head
       [:p.modal-card-title "Enter your password"]]
      [:section.modal-card-body
       [:p.is-size-5.mb-4
        "Enter your "
        (if (= :create action) "new" "existing")
        " password to create the note with name: "
        [:strong identifier]]
       [:input.input {:type "password"
                      :placeholder "Enter password..."
                      :on-change #(reset! password-value
                                          (-> % .-target .-value))
                      :value @password-value}]]
      [:footer.modal-card-foot
       [:button.button.is-success
        {:class (when loading "is-loading")
         :on-click #(submit-handler @password-value)}
        (string/capitalize (name action))]
       [:button.button {:on-click #(hide config)} "Cancel"]]]
     [:button.modal-close.is-large {:on-click #(hide config)}]]))

(defn password-entry-modal
  [_ _]
  (r/create-class {:component-did-update password-entry-modal-update
                   :reagent-render password-entry-modal-render}))
