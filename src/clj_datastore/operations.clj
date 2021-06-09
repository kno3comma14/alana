(ns clj-datastore.operations
  (:import (com.google.cloud.datastore Datastore
                                       DatastoreOptions
                                       Entity
                                       Key
                                       Query
                                       StructuredQuery
                                       StructuredQuery$PropertyFilter)))

(defn map-to-entity-builder
  "Transforms from a native hash-map to a datastore entity"
  [entity-map key]
  (let [entity-map-keys (keys entity-map)
        entity (.setKey (Entity/newBuilder) key)]
    (doseq [k entity-map-keys]
      (let [entity-field (name k)
            entity-value (get entity-map k)]
        (.set entity entity-field entity-value)))
    entity))

(defn create-datastore
  "Creates a Datastore object"
  []
  (.getService (DatastoreOptions/getDefaultInstance)))

(defn create-entity
  "Creates an Entity object"
  ([datastore kind name entity-map]
   (let [key (.newKey (.setKind (.newKeyFactory datastore) kind) name)]
     (.build (map-to-entity-builder entity-map key))))
  ([datastore kind entity-map]
   (let [key (.newKey (.setKind (.newKeyFactory datastore) kind))]
     (.build (map-to-entity-builder entity-map key)))))

(defn create-query-by-property-equality
  [kind property-map]
  (let [query (.setKind (Query/newEntityQueryBuilder) kind)
        keys (map (fn [x] (name x)) (keys property-map))]
    (doseq [k keys]
      (let [value (get property-map (keyword k))]
        (.setFilter query (StructuredQuery$PropertyFilter/eq k value))))
    (.build query)))

(defn run-query
  "Run a query against a given datastore"
  [datastore kind property-map]
  (let [query (create-query-by-property-equality kind property-map)]
    (.run datastore query)))

(defn verify-entity-existence
  "Verify is an entity exists in a database"
  [datastore kind property-map]
  (let [result (run-query datastore kind property-map)]
    (.hasNext result)))

(defn upsert-entity
  "Upsert an entity to Firestore"
  [datastore entity]
  (.put datastore entity))

