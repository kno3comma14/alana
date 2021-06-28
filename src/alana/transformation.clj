(ns alana.transformation
  (:import [com.google.cloud.datastore Entity                                      
                                       TimestampValue]
           [com.google.cloud Timestamp])
  (:require [alana.validation :refer :all]))

(defn- java-date->timestamp-value
  "This function transform a java.util.Date to TimestampValue"
  [date]
  (if (not (nil? date ))
    (let [timestamp (Timestamp/of date)]
      (.build (TimestampValue/newBuilder timestamp)))
    (throw (AssertionError. "The date parameter has to be a not nil java.util.Date object."))))

(defn map->entity-builder
  "Transforms from a native hash-map to a datastore entity"
  [entity-map key]
  (if (validate-map->entity-builder-input entity-map key)
    (let [entity-map-keys (keys entity-map)
          entity (.setKey (Entity/newBuilder) key)]
      (doseq [k entity-map-keys]
        (let [entity-field (name k)
              entity-value (get entity-map k)]
          (if (isa? (type entity-value) java.util.Date)
            (.set entity entity-field (java-date->timestamp-value entity-value))
            (.set entity entity-field entity-value))))
      entity)
    (throw
     (ex-info "Input assertion failed."
              {:causes (explain-map->entity-builder-input-failures entity-map key)
               :actual-value {:value [entity-map key]}}))))

(defn entity->hash-map
  "This function transforms from datastore entity object to hash-map"
  [entity-object]
  (let [properties (keys (.getProperties entity-object))]
    (reduce (fn [acc x] 
              (assoc acc (keyword x) 
                         (.get (.get (.getProperties entity-object) x)))) 
            {} 
            properties)))

(defn query-result->vector
  "This function takes a query result object and transform it to vector"
  [query-result]
  (loop [has-next (.hasNext query-result)
         result []]
    (if (not has-next)
      result
      (recur (.hasNext query-result)
             (if (.hasNext query-result)
               (conj result (entity->hash-map (.next query-result)))
               result)))))
