(ns clj-datastore.integration.operations-test
  (:require [clojure.test :refer :all]
            [clj-datastore.operations :refer :all])
  (:import (com.google.cloud.datastore Datastore
                                       DatastoreOptions
                                       Entity
                                       Key
                                       Query
                                       StructuredQuery
                                       StructuredQuery$PropertyFilter
                                       StructuredQuery$CompositeFilter)))

(def integration-test-kind "test-kind")
(def test-datastore (.getService (DatastoreOptions/getDefaultInstance)))
(def test-key (.newKey (.setKind (.newKeyFactory test-datastore) integration-test-kind)))


(deftest run-query-test
  (testing "Integration test to run a single filter query against a datastore and verify its type and values correctness"
    (let [ds test-datastore
          input-kind integration-test-kind
          property-map [{:key "property1" :value "Hello" :type "="}]
          test-value (run-query ds input-kind property-map)
          expected-type com.google.cloud.datastore.QueryResults
          expected-value "Hello"]
      (is (isa? (type test-value) expected-type))
      (is (=  (.get (.get (.getProperties (.next test-value)) "property1")) expected-value))))
  (testing "Integration test to run a composite filter query against a datastore and verify its type and values correctness"
    (let [ds test-datastore
          input-kind integration-test-kind
          property-map [{:key "property2" :value "World" :type "="}
                        {:key "quantity" :value 32 :type "="}]
          test-value (run-query ds input-kind property-map)
          expected-type com.google.cloud.datastore.QueryResults
          expected-value "Hello"]
      (is (isa? (type test-value) expected-type))
      (is (=  (.get (.get (.getProperties (.next test-value)) "property1")) expected-value)))))

(deftest upsert-entity-test
  (testing "Integration test to upsert an entity into a datastore"
    (is
     (let [datastore test-datastore
           kind integration-test-kind
           property-map {:property1 "Hello1" :property2 "Hello2" :quantity 33}
           entity (create-entity datastore kind property-map)
           test-value (upsert-entity datastore entity)
           expected-type com.google.cloud.datastore.Entity]
       (isa? (type test-value) expected-type)))))
