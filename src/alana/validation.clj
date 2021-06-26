(ns alana.validation
  (:require [malli.core :as malli]
            [malli.error :as me]
            [alana.schema :refer :all]))

(defn- clean-validation-map
  "Removes nil elements from the map to be evaluated."
  [validation-map]
  (let [filtered-values (filter (fn [[k, v]] (not (nil? v))) validation-map)]
    (reduce (fn [acc, x] (assoc acc (get x 0) (get x 1))) {} filtered-values)))

(defn- validate
  "Provides the validation process given a validation map and a schema."
  [schema validation-map]
  (let [cleaned-validation-map (clean-validation-map validation-map)]
    (malli/validate schema cleaned-validation-map)))

(defn- explain
  "Function to be used when the validation returns a false. This explains the reasons of failures in the evaluation process."
  [schema validation-map]
  (let [cleaned-validation-map (clean-validation-map validation-map)]
    (-> schema
        (malli/explain cleaned-validation-map)
        (me/humanize))))

(defn validate-map-to-entity-builder-input
  "Validates map-to-entity-builder input using MapToEntityBuilderInput schema."
  [entity-map key]
  (let [validation-map {:entity-map entity-map
                        :key key}]
    (validate MapToEntityBuilderInput validation-map)))

(defn explain-map-to-entity-builder-input-failures
  "Explains upsert entity input failures given a MapToEntityBuilderInput schema."
  [entity-map key]
  (let [validation-map {:entity-map entity-map
                        :key key}]
    (explain MapToEntityBuilderInput validation-map)))

(defn validate-entity-input
  "Validates an entity input using EntityInput schema."
  [datastore kind name entity-map]
  (let [validation-map {:datastore datastore
                        :kind kind
                        :name name
                        :entity-map entity-map}]
    (validate EntityInput validation-map)))

(defn explain-entity-input-failures
  "Explain entity input failures given an EntityInput schema."
  [datastore kind name entity-map]
  (let [validation-map {:datastore datastore
                        :kind kind
                        :name name
                        :entity-map entity-map}]
    (explain EntityInput validation-map)))

(defn validate-property-filter-input
  "Validates a property filter input using PropertyFilterInput schema."
  [key value type datastore kind]
  (let [validation-map {:key key
                        :value value
                        :type type
                        :datastore datastore
                        :kind kind}]
    (validate PropertyFilterInput validation-map)))

(defn explain-property-filter-input-failures
  "Explains property filter input failures given a PropertyFilterInput schema."
  [key value type datastore kind]
  (let [validation-map {:key key
                         :value value
                         :type type
                         :datastore datastore
                         :kind kind}]
    (explain PropertyFilterInput validation-map)))

(defn validate-create-query-input
  "Validates a create query input using CreateQueryInput schema."
  [kind property-map]
  (let [validation-map {:kind kind
                        :property-map property-map}]
    (validate CreateQueryInput validation-map)))

(defn explain-create-query-input-failures
  "Explains create query input failures given a CreateQueryInput schema."
  [kind property-map]
  (let [validation-map {:kind kind
                        :property-map property-map}]
    (explain CreateQueryInput validation-map)))

(defn validate-run-query-input
  "Validates run-query function input using RunQueryInput schema."
  ([datastore kind property-map]
   (let [validation-map {:datastore datastore
                         :kind kind
                         :property-map property-map}]
     (validate RunQueryInput validation-map)))
  ([datastore query-string]
   (let [validation-map {:datastore datastore
                         :query-string query-string}]
     (validate RunQueryInput validation-map))))

(defn explain-run-query-input-failures
  "Explains run query input failures given a RunQueryInput schema."
  ([datastore kind property-map]
   (let [validation-map {:datastore datastore
                         :kind kind
                         :property-map property-map}]
     (explain RunQueryInput validation-map)))
  ([datastore query-string]
   (let [validation-map {:datastore datastore
                         :query-string query-string}]
     (explain RunQueryInput validation-map))))

(defn validate-upsert-entity-input
  "Validates upsert-entity function input using UpsertEntityInput schema."
  [datastore entity]
  (let [validation-map {:datastore datastore
                        :entity entity}]
    (validate UpsertEntityInput validation-map)))

(defn explain-upsert-entity-input-failures
  "Explains upsert entity input failures given a UpsertEntityInput schema."
  [datastore entity]
  (let [validation-map {:datastore datastore
                        :entity entity}]
    (explain UpsertEntityInput validation-map)))

(defn validate-insert-entity-input
  "Validates insert-entity function input using InsertEntityInput schema."
  [datastore entity]
  (let [validation-map {:datastore datastore
                        :entity entity}]
    (validate InsertEntityInput validation-map)))

(defn explain-insert-entity-input-failures
  "Explains insert entity input failures given an InsertEntityInput schema."
  [datastore entity]
  (let [validation-map {:datastore datastore
                        :entity entity}]
    (explain InsertEntityInput validation-map)))

(defn validate-delete-entity-input
  "Validates delete-entity function input using DeleteEntityInput schema."
  [datastore completed-key]
  (let [validation-map {:datastore datastore
                        :completed-key completed-key}]
    (validate DeleteEntityInput validation-map)))

(defn explain-delete-entity-input-failures
  "Explains delete entity input failures given a DeleteEntityInput schema."
  [datastore completed-key]
  (let [validation-map {:datastore datastore
                        :completed-key completed-key}]
    (explain DeleteEntityInput validation-map)))
