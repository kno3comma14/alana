(ns clj-datastore.validation
  (:require [malli.core :as malli]
            [malli.error :as me]
            [clj-datastore.schema :refer :all]))

(defn- clean-validation-map
  [validation-map]
  (apply hash-map (flatten (filter (fn [[k, v]] (not (nil? v))) validation-map))))

(defn- validate
  [schema validation-map]
  (let [cleaned-validation-map (clean-validation-map validation-map)]
    (if (malli/validate schema cleaned-validation-map)
      true
    (-> schema
        (malli/explain cleaned-validation-map)
        (me/humanize)))))

(defn validate-entity-input
  "Validates an entity input using EntityInput schema."
  [datastore kind name entity-map]
  (let [validation-map {:datastore datastore
                        :kind kind
                        :name name
                        :entity-map entity-map}]
    (validate EntityInput validation-map)))

(defn validate-property-filter-input
  "Validates a property filter input using PropertyFilterInput schema."
  [key value type datastore kind]
  (let [validation-map {:key key
                        :value value
                        :type type
                        :datastore datastore
                        :kind kind}]
    (validate PropertyFilterInput validation-map)))

(defn validate-create-query-input
  "Validates a create query input using CreateQueryInput schema."
  [kind property-map]
  (let [validation-map {:kind kind
                        :property-map property-map}]
    (validate CreateQueryInput validation-map)))

(defn validate-run-query-input
  "Validates run-query function input using RunQueryInput schema."
  [datastore kind property-map]
  (let [validation-map {:datastore datastore
                        :kind kind
                        :property-map property-map}]
    (validate RunQueryInput validation-map)))

(defn validate-upsert-entity-input
  "Validates upsert-entity function input using UpsertEntityInput schema."
  [datastore entity]
  (let [validation-map {:datastore datastore
                        :entity entity}]
    (validate UpsertEntityInput validation-map)))

(defn validate-map-to-entity-builder-input
  "Validates map-to-entity-builder input using MapToEntityBuilderInput schema."
  [entity-map key]
  (let [validation-map {:entity-map entity-map
                        :key key}]
    (validate MapToEntityBuilderInput validation-map)))


