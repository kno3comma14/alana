(ns clj-datastore.operations
  (:import (com.google.cloud.datastore Datastore
                                       DatastoreOptions
                                       Entity
                                       Key
                                       Query
                                       StructuredQuery
                                       StructuredQuery$PropertyFilter
                                       StructuredQuery$CompositeFilter)))

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

(defn assign-property-filter
  [{key :key value :value type :type}]
  (case type
    :equal                 (StructuredQuery$PropertyFilter/eq key value)
    :less-than             (StructuredQuery$PropertyFilter/lt key value)
    :less-than-or-equal    (StructuredQuery$PropertyFilter/le key value)
    :greater-than          (StructuredQuery$PropertyFilter/gt key value)
    :greater-than-or-equal (StructuredQuery$PropertyFilter/ge key value))
  [key type]
  (case type
    :is-null      (StructuredQuery$PropertyFilter/isNull key)
    :has-ancestor (StructuredQuery$PropertyFilter/hasAncestor key)))

(defn create-composite-filter
  [first-map-filter & other-map-filters]
  (let [first-property-filter (assign-property-filter first-map-filter)
        other-property-filters (map assign-property-filter other-map-filters)
        result-composite-filter (StructuredQuery$CompositeFilter/and 
                                 first-property-filter, 
                                 other-property-filters)]
    result-composite-filter))

(defn create-filter
  [property-map]
  (if (> (count (property-map) 1))
    (create-composite-filter property-map)
    (assign-property-filter (first property-map))))

(defn create-gql-query
  [query-string]
  (.build (Query/newGqlQueryBuilder query-string)))

(defn create-query
  [kind property-map]
  (let [query (.setKind (Query/newEntityQueryBuilder) kind)
        query-filter (create-filter property-map)]
    (.setFilter query query-filter)
    (.build query))
  [query-string]
  (create-gql-query query-string))

(defn run-query
  "Run a query against a given datastore"
  [datastore kind property-map]
  (let [query (create-query kind property-map)]
    (.run datastore query))
  [datastore query-string]
  (let [query (create-query query-string)]
    (.run datastore query)))

;; (defn verify-entity-existence
;;   "Verify is an entity exists in a database"
;;   [datastore kind property-map]
;;   (let [result (run-query datastore kind property-map)]
;;     (.hasNext result)))

;; (defn upsert-entity
;;   "Upsert an entity to Firestore"
;;   [datastore entity]
;;   (.put datastore entity))

