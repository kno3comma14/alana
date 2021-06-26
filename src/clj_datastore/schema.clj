(ns clj-datastore.schema)

(def MapToEntityBuilderInput
  [:map {:closed true}
   [:entity-map map?]
   [:key any?]])

(def EntityInput
  [:or 
   [:map {:closed true}
    [:datastore any?]
    [:kind string?]
    [:name {:optional true} string?]
    [:entity-map map?]]
   [:map {:closed true}
    [:datastore any?]
    [:kind string?]
    [:entity-map map?]]])

(def PropertyFilterInput
  [:or
   [:map {:closed true}
    [:key string?]
    [:value any?]
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
  [:or 
   [:map {:closed true}
    [:datastore any?]
    [:kind string?]
    [:property-map any?]]
   [:map {:closed true}
    [:datastore any?]
    [:query-string string?]]])

(def UpsertEntityInput
  [:map {:closed true}
   [:datastore any?]
   [:entity any?]])

(def InsertEntityInput
  [:map {:closed true}
   [:datastore any?]
   [:entity any?]])

(def DeleteEntityInput
  [:map {:closed true}
   [:datastore any?]
   [:completed-key any?]])
