(ns alana.unit.transformation-test
  (:require [alana.transformation :refer :all]
            [alana.operations :refer [create-entity]]
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

(deftest java-date->timestamp-value-test
  (testing "Given a date, returns a new TimestampValue object"
    (is
     (let [input-date (java.util.Date. 2021 11 24)
           test-value (java-date->timestamp-value input-date)
           expected-type TimestampValue]
       (isa? (type test-value) expected-type)))
    (is
     (let [now-date (java.util.Date. )
           test-value (java-date->timestamp-value now-date)]
       (= now-date (.toDate (.get test-value)))))))

(deftest map->entity-builder-test
  (testing "Given a map, returns a new Entity object with the right properties and values"
    (is 
     (let [input-map {:property1 "A" :property2 "B"}
           input-key test-key
           test-value (map->entity-builder input-map input-key)
           expected-type FullEntity$Builder]
       (isa? (type test-value) expected-type)))))

(deftest entity->hash-map-test
  (testing "Given en entity, returns a map with the expected properties and values"
    (is
     (let [input-value (create-entity test-datastore test-kind {:a "A" :b 1})
           test-value (entity->hash-map input-value)
           expected-value {:a "A" :b 1}]
       (= test-value expected-value)))))
