(ns clj-datastore.integration.operations-test
  (:require [clojure.test :refer :all]
            [clj-datastore.operations :refer :all]
            [clj-datastore.integration.environment :refer :all])
  (:import (com.google.cloud.datastore Datastore
                                       DatastoreOptions
                                       Entity
                                       Key
                                       Query
                                       StructuredQuery
                                       StructuredQuery$PropertyFilter
                                       StructuredQuery$CompositeFilter)))

(use-fixtures :once wrap-setup)

(def integration-test-kind "some-saga-characters")

(def test-datastore (.getService (DatastoreOptions/getDefaultInstance)))

(def test-key (.newKey (.setKind (.newKeyFactory test-datastore) integration-test-kind)))

(deftest run-query-test
  (testing "Integration test to run a single filter query against a datastore and verify its type and values correctness"
    (let [ds test-datastore
          input-kind integration-test-kind
          property-map [{:key "name" :value "Alana" :type "="}]
          test-value (run-query ds input-kind property-map)
          test-name (.get (.get (.getProperties (.next test-value)) "name"))
          expected-type com.google.cloud.datastore.QueryResults
          expected-value "Alana"]
      (is (isa? (type test-value) expected-type))
      (is (=  test-name expected-value))))
  (testing "Integration test to run a composite filter query against a datastore and verify its type and values correctness"
    (let [ds test-datastore
          input-kind integration-test-kind
          property-map [{:key "gender" :value "F" :type "="}
                        {:key "first-chapter-appearance" :value 6 :type "="}]
          test-value (run-query ds input-kind property-map)
          expected-type com.google.cloud.datastore.QueryResults
          expected-value "Klara"]
      (is (isa? (type test-value) expected-type))
      (is (=  (.get (.get (.getProperties (.next test-value)) "name")) expected-value)))))

(deftest upsert-entity-test
  (testing "Integration test to upsert an entity into a datastore instance"
    (is
     (let [datastore test-datastore
           kind integration-test-kind
           property-map {:name "Hazel" :gender "F" :first-chapter-appearance 1}
           entity (create-entity datastore kind property-map)
           test-value (upsert-entity datastore entity)
           expected-type com.google.cloud.datastore.Entity]
       (swap! entity-keys conj (.getKey test-value))
       (isa? (type test-value) expected-type)))))

(deftest insert-entity-test
  (testing "Integration test to insert an entity into a datastore instance"
    (is
     (let [datastore test-datastore
           kind integration-test-kind
           property-map {:name "Even" :gender "F" :first-chapter-appearance 14}
           entity (create-entity datastore kind property-map)
           test-value (insert-entity datastore entity)
           expected-type com.google.cloud.datastore.Entity]
       (swap! entity-keys conj (.getKey test-value))
       (isa? (type test-value) expected-type)))))

(deftest delete-entity-test
  (testing "Integration test to delete an entity from a datastore instance"
    (is
     (let [datastore test-datastore
           completed-key (get @entity-keys 1)
           test-value (delete-entity datastore completed-key)
           expected-type nil]
       (println (type test-value))
       (isa? (type test-value) expected-type)))))
