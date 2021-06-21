(ns clj-datastore.schema
  (:import (com.google.cloud.datastore Datastore
                                       Entity)))

(defn datastore?
  [datastore]
  (isa? (type x) Datastore))

(defn entity?
  [datastore]
  (isa? (type x) Entity))

(defn is-type?
  [target]
  (let [target-type-values ["equal"
                            "less-than"
                            "less-than-or-equal"
                            "greater-than"
                            "greater-than-or-equal"
                            "is-null"
                            "has-ancestor"]]
    (not (nil? (some #(= target %) target-type-values)))))

(def error-messages {:invalid-is-null "Invalid is-null property type"
                     :invalid-has-ancestry "Invalid has-ancestry property type"
                     :invalid-generic "Invalid property type"})

(def EntityInput
  [:map {:closed true}
   [:datastore datastore?]
   [:kind string?]
   [:name {:optional true} string?]
   [:entity-map map?]])

(def PropertyFilterInput
  [:and
   [:map {:closed true}
    [:key string?]
    [:value {:optional true} string?]
    [:type [:and string? is-type?]]
    [:datastore {:optional true} datastore?]
    [:kind {:optional true} string?]]
   [:or 
    [:fn {:error/message (:invalid-is-null error-messages)
          :error/path [:type]}
     '(fn [{:keys [datastore kind value type]}]
        (and (= type "is-null") 
             (nil? datastore) 
             (nil? kind)
             (nil? value)))]
    [:fn {:error/message (:invalid-has-ancestor error-messages)
          :error/path [:type]}
     '(fn [{:keys [type value datastore kind]}]
        (and (= type "has-ancestor") 
             (nil? value)
             (not (nil? datastore))
             (not (nil? kind))))]
    [:fn {:error/message (:invalid-generic error-messages)
          :error/path [:type]}
     '(fn [{:keys type value datastore kind}]
        (let [generic-filter-types ["equal"
                                    "less-than"
                                    "less-than-or-equal"
                                    "greater-than"
                                    "greater-than-or-equal"]]
          (and (not (nil? (some #(= type %) generic-filter-types)))
               (nil? datastore)
               (nil? kind)
               (not (nil? value)))))]]])

(def CreateQueryInput
  [:map
   [:kind string?]
   [property-map map?]])

(def RunQueryInput
  [:map
   [:datastore datastore?]
   [:kind string?]
   [:property-map vector?]])

(def UpsertEntityInput
  [:map
   [:datastore datastore?]
   [:entity] entity?])
