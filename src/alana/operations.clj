(ns alana.operations
  (:import [com.google.cloud.datastore Datastore
                                       DatastoreOptions
                                       Entity
                                       Key
                                       Query
                                       StructuredQuery
                                       StructuredQuery$PropertyFilter
                                       StructuredQuery$CompositeFilter
                                       TimestampValue]
           [com.google.cloud Timestamp])
  (:require [alana.validation :refer :all]
            [alana.transformation :refer :all]))

(defn create-datastore
  "Creates a Datastore object"
  []
  (.getService (DatastoreOptions/getDefaultInstance)))

(defn create-entity
  "Creates an Entity object"
  ([datastore kind name entity-map]
   (if (validate-entity-input datastore kind name entity-map)
     (let [key (.newKey (.setKind (.newKeyFactory datastore) kind) name)]
       (.build (map->entity-builder entity-map key)))
     (throw
      (ex-info "Input assertion failed."
               {:causes (explain-entity-input-failures datastore kind name entity-map)
                :actual-value {:value [datastore kind name entity-map]}}))))
  ([datastore kind entity-map]
   (if (validate-entity-input datastore kind nil entity-map)
     (let [key (.newKey (.setKind (.newKeyFactory datastore) kind))]
       (.build (map->entity-builder entity-map key)))
     (throw
      (ex-info "Input assertion failed."
               {:causes (explain-entity-input-failures datastore kind nil entity-map)
                :actual-value {:value [datastore kind entity-map]}})))))

(defn assign-property-filter
  "Given a map that describes the structure of a filter, this function returns the corresponding property filter"
  [{key :key value :value type :type datastore :datastore kind :kind}]
  (if (validate-property-filter-input key value type datastore kind)
    (if (and (nil? datastore) (nil? kind) (not (nil? value)))
      (case type
        "="  (StructuredQuery$PropertyFilter/eq key value)
        "<"  (StructuredQuery$PropertyFilter/lt key value)
        "<=" (StructuredQuery$PropertyFilter/le key value)
        ">"  (StructuredQuery$PropertyFilter/gt key value)
        ">=" (StructuredQuery$PropertyFilter/ge key value))
      (when (nil? value)
        (let [internal-key (if (= type "has-ancestor")
                             (.newKey (.setKind (.newKeyFactory datastore) kind) key)
                             key)]
          (case type
            "is-null"      (StructuredQuery$PropertyFilter/isNull internal-key)
            "has-ancestor" (StructuredQuery$PropertyFilter/hasAncestor internal-key)))))
    (throw
     (ex-info "Input assertion failed."
              {:causes (explain-property-filter-input-failures key value type datastore kind)
               :actual-value {:value [key value type datastore kind]}}))))

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
   (if (validate-create-query-input kind property-map)
     (let [query (.setKind (Query/newEntityQueryBuilder) kind)
           query-filter (create-filter property-map)]
       (.setFilter query query-filter)
       (.build query))
     (throw
      (ex-info "Input assertion failed."
             {:causes (explain-create-query-input-failures kind property-map)
              :actual-value {:value [kind property-map]}}))))  
  ([query-string]
   (if (not (empty? query-string))
     (create-gql-query query-string))
   (throw
    (ex-info "Input assertion failed."
             {:causes {:query-string "should be a not empty string"}
              :actual-value {:value [query-string]}}))))

(defn run-query
  "Run a query against a given datastore"  
  ([datastore kind property-map]
   (if (validate-run-query-input datastore kind property-map)
     (let [query (create-query kind property-map)]
       (query-result->vector (.run datastore query)))
     (throw
      (ex-info "Input assertion failed."
               {:causes (explain-run-query-input-failures datastore kind property-map)}
               :actual-value {:value [datastore kind property-map]}))))
  ([datastore query-string]
   (if (validate-run-query-input datastore query-string)
     (let [query (create-query query-string)]
           (.run datastore query))
     (throw
      (ex-info "Input assertion failed."
               {:causes (explain-run-query-input-failures datastore query-string)}
               :actual-value {:value [datastore query-string]})))))

(defn upsert-entity
  "Upsert an entity to Datastore"
  [datastore entity]
  (if (validate-upsert-entity-input datastore entity)
    (.put datastore entity)
    (throw
     (ex-info "Input assertion failed."
              {:causes (explain-upsert-entity-input-failures datastore entity)}
              :actual-value {:value [datastore entity]}))))

(defn insert-entity
  "Insert an entity to Datastore"
  [datastore entity]
  (if (validate-insert-entity-input datastore entity)
    (.add datastore entity)
    (throw
     (ex-info "Input assertion failed."
              {:causes (explain-insert-entity-input-failures datastore entity)}
              :actual-value {:value [datastore entity]}))))

(defn delete-entity
  "Delete an entity from a datastore"
  [datastore completed-entity]
  (if (validate-delete-entity-input datastore completed-entity)
    (.delete datastore (into-array (list (.getKey completed-entity))))
    (throw
     (ex-info "Input assertion failed."
              {:causes (explain-delete-entity-input-failures datastore completed-entity)}
              :actual-value {:value [datastore completed-entity]}))))

(defn lookup-entity
  "This function retrieves an entity from a given datastore. A complete entity has to be passed as argument"
  [datastore completed-entity]
  (if (validate-lookup-entity-input datastore completed-entity)
    (entity->hash-map (.get datastore (.getKey completed-entity)))
    (throw
     (ex-info "Input assertion failed."
              {:causes (explain-lookup-entity-input-failures datastore completed-entity)}
              :actual-value {:value [datastore completed-entity]}))))
