(ns clj-datastore.operations-test
  (:require [clojure.test :refer :all]
            [clj-datastore.operations :refer :all])
  (:import (com.google.cloud.datastore Datastore
                                       DatastoreOptions
                                       Entity
                                       Key
                                       Query
                                       StructuredQuery
                                       StructuredQuery$PropertyFilter)))

(def test-datastore (.getService (DatastoreOptions/getDefaultInstance)))
(def test-kind "testkind")
(def test-key (.newKey (.setKind (.newKeyFactory test-datastore) test-kind)))
(def integration-test-kind "User")

(deftest map-to-entity-builder-test
  (testing "Given a map, returns a new Entity object with the right properties and values"
    (is 
     (let [input-map {:property1 "A" :property2 "B"}
           input-key test-key
           test-value (map-to-entity-builder input-map input-key)
           expected-type com.google.cloud.datastore.FullEntity$Builder]
       (isa? (type test-value) expected-type)))))

(deftest create-datastore-test
  (testing "The creation of the correct datastore instance type"
    (is 
     (let [test-value (create-datastore)]
       (isa? (type test-value) Datastore)))))

(deftest create-entity-test
  (testing "The creation of the correct entity instance type and well defined structure"
    (is
     (let [input-map {:property1 "A" :property2 "B"}
           input-kind test-kind
           input-datastore test-datastore
           test-value (create-entity input-datastore input-kind input-map)
           expected-type com.google.cloud.datastore.FullEntity]
       (and (isa? (type test-value) expected-type)
            (= (.get (.getValue test-value (name :property1))) "A")
            (= (.get (.getValue test-value (name :property2))) "B"))))))

(deftest create-query-by-property-equality-test
  (testing "The correct creation of a query object given a map of properties"
    (is
     (let [input-map {:property1 "A"}
           input-kind test-kind
           test-value (create-query-by-property-equality input-kind input-map)
           expected-type com.google.cloud.datastore.StructuredQuery]
       (isa? (type test-value) expected-type)))))

(deftest run-query-test
  (testing "Integration test to run a query against a datastore and verify its type and values correctness"
    (let [ds test-datastore
          input-kind integration-test-kind
          property-map {:email "enyert.vinas@gmail.com"}
          test-value (run-query ds input-kind property-map)
          expected-type com.google.cloud.datastore.QueryResults
          expected-value "enyert.vinas@gmail.com"]
      (is (isa? (type test-value) expected-type))
      (is (=  (.get (.get (.getProperties (.next test-value)) "email")) expected-value)))))

(deftest verify-entity-existence-test
  (testing "The verification of the existence of an Entity"
    (is
     (let [ds test-datastore
          input-kind integration-test-kind
          property-map {:email "enyert.vinas@gmail.com"}
          test-value (verify-entity-existence ds input-kind property-map)          
          expected-value true]
       (= test-value expected-value))))
  (testing "The verification of the absence of an Entity"
    (is
     (let [ds test-datastore
          input-kind integration-test-kind
          property-map {:email "enyert.vinas.no@gmail.com"}
          test-value (verify-entity-existence ds input-kind property-map)          
          expected-value false]
       (= test-value expected-value)))))
