(ns clj-datastore.schema)

(def EntityInput
  [:map {:closed true}
   [:datastore any?]
   [:kind string?]
   [:name {:optional true} string?]
   [:entity-map map?]])

(def PropertyFilterInput
  [:or
   [:map {:closed true}
    [:key string?]
    [:value string?]
    [:type [:enum "=" "<" "<=" ">" ">="]]]
   [:map {:closed true}
    [:key string?]
    [:type [:enum "is-null"]]]
   [:map {:closed true}
    [:key string?]
    [:type [:enum "has-ancestor"]]
    [:datastore any?]
    [:kind string?]]])

(def CreateQueryInput
  [:map {:closed true}
   [:kind string?]
   [:property-map any?]])

(def RunQueryInput
  [:map {:closed true}
   [:datastore any?]
   [:kind string?]
   [:property-map any?]])

(def UpsertEntityInput
  [:map
   [:datastore any?]
   [:entity any?]])
