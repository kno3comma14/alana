(ns clj-datastore.operations
  (:import [com.google.cloud.datastore Datastore
                                       DatastoreOptions
                                       Entity
                                       Key
                                       Query
                                       StructuredQuery
                                       StructuredQuery$PropertyFilter
                                       StructuredQuery$CompositeFilter
                                       TimestampValue]
           [com.google.cloud Timestamp]))

(defn- java-date->timestamp-value
  "This function transform a java.util.Date to TimestampValue"
  [date]
  (if (not (nil? date ))
    (let [timestamp (com.google.cloud.Timestamp/of date)]
      (.build (com.google.cloud.datastore.TimestampValue/newBuilder timestamp)))
    (throw (AssertionError. "The date parameter has to be a not nil java.util.Date object."))))

(defn map-to-entity-builder
  "Transforms from a native hash-map to a datastore entity"
  [entity-map key]
  (let [entity-map-keys (keys entity-map)
        entity (.setKey (Entity/newBuilder) key)]
    (doseq [k entity-map-keys]
      (let [entity-field (name k)
            entity-value (get entity-map k)]
        (if (isa? (type entity-value) java.util.Date)
          (.set entity entity-field (java-date->timestamp-value entity-value))
          (.set entity entity-field entity-value))))
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
  "Given a map that describes the structure of a filter, this function returns the corresponding property filter"
  [{key :key value :value type :type datastore :datastore kind :kind}]
  (if (and (nil? datastore) (nil? kind))
    (case type
     "="  (StructuredQuery$PropertyFilter/eq key value)
     "<"  (StructuredQuery$PropertyFilter/lt key value)
     "<=" (StructuredQuery$PropertyFilter/le key value)
     ">"  (StructuredQuery$PropertyFilter/gt key value)
     ">=" (StructuredQuery$PropertyFilter/ge key value))
    (when (nil? value)
      (let [internal-key (.newKey (.setKind (.newKeyFactory datastore) kind) key)]
       (case type
       "is-null"      (StructuredQuery$PropertyFilter/isNull key)
       "has-ancestor" (StructuredQuery$PropertyFilter/hasAncestor internal-key))))))

(defn create-composite-filter
  "This function returns a composite filter based on a given vector of maps that represents filter structure"
  [[first-map-filter & other-map-filters]]
  (let [first-property-filter (assign-property-filter first-map-filter)
        other-property-filters (into-array (map assign-property-filter other-map-filters))
        result-composite-filter (StructuredQuery$CompositeFilter/and 
                                 first-property-filter, 
                                 other-property-filters)]
    result-composite-filter))

(defn create-filter
  "This function creates a filter"
  [property-map]
  (if (> (count property-map) 1)
    (create-composite-filter property-map)
    (assign-property-filter (first property-map))))

(defn create-gql-query
  "This function creates a gql query"
  [query-string]
  (.build (Query/newGqlQueryBuilder query-string)))

(defn create-query
  "This function creates a query. Depending of the parameters, this could create a normal query or a GQL query"
  ([kind property-map]
   (let [query (.setKind (Query/newEntityQueryBuilder) kind)
         query-filter (create-filter property-map)]
     (.setFilter query query-filter)
     (.build query)))  
  ([query-string]
   (create-gql-query query-string)))

(defn run-query
  "Run a query against a given datastore"  
  ([datastore kind property-map]
   (let [query (create-query kind property-map)]
     (.run datastore query)))
  ([datastore query-string]
   (let [query (create-query query-string)]
     (.run datastore query))))

(defn upsert-entity
  "Upsert an entity to Datastore"
  [datastore entity]
  (.put datastore entity))

(defn insert-entity
  "Insert an entity to Datastore"
  [datastore entity]
  (.add datastore entity))
