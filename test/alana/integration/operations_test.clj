(ns alana.integration.operations-test
  (:require [clojure.test :refer :all]
            [alana.operations :refer :all]
            [alana.integration.environment :refer :all])
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
          test-name (:name (nth test-value 0))
          expected-type clojure.lang.PersistentVector
          expected-value "Alana"]
      (is (isa? (type test-value) expected-type))
      (is (=  test-name expected-value))))
  (testing "Integration test to run a composite filter query against a datastore and verify its type and values correctness"
    (let [ds test-datastore
          input-kind integration-test-kind
          property-map [{:key "gender" :value "F" :type "="}
                        {:key "first-chapter-appearance" :value 6 :type "="}]
          test-value (run-query ds input-kind property-map)
          expected-type clojure.lang.PersistentVector
          expected-value "Klara"]
      (is (isa? (type test-value) expected-type))
      (is (=  (:name (nth test-value 0)) expected-value)))))

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
           completed-entity (get @completed-entities 1)
           test-value (delete-entity datastore completed-entity)
           expected-type nil]
       (isa? (type test-value) expected-type)))))

(deftest lookup-entity-test
  (testing "Integration test to lookup an entity from a datastore instance"
    (is
     (let [datastore test-datastore
           completed-entity (get @completed-entities 0)
           test-value (select-keys (lookup-entity datastore completed-entity) [:name :gender])
           expected-value {:name "Alana" :gender "F"}]
       (= test-value expected-value)))))
