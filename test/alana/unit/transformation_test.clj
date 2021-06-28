(ns alana.unit.transformation-test
  (:require [alana.transformation :refer :all]
            [clojure.test :refer :all])
  (:import [com.google.cloud.datastore DatastoreOptions
                                       Entity
                                       FullEntity
                                       FullEntity$Builder
                                       TimestampValue]
           [com.google.cloud Timestamp]))

(def test-datastore (.getService (.build (.setServiceRpcFactory 
                              (.setHost 
                               (.setProjectId 
                                (DatastoreOptions/newBuilder) 
                                "project-id") 
                               "http://localhost:8080") 
                              nil))))
(def test-kind "testkind")
(def test-key (.newKey (.setKind (.newKeyFactory test-datastore) test-kind)))

(deftest map->entity-builder-test
  (testing "Given a map, returns a new Entity object with the right properties and values"
    (is 
     (let [input-map {:property1 "A" :property2 "B"}
           input-key test-key
           test-value (map->entity-builder input-map input-key)
           expected-type FullEntity$Builder]
       (isa? (type test-value) expected-type)))))
