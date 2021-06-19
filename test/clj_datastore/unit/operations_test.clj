(ns clj-datastore.unit.operations-test
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

(deftest create-query-test
  (testing "The correct creation of a query object given a map of properties"
    (is
     (let [input-map [{:key "property1" :value "A" :type "equal"}]
           input-kind test-kind
           test-value (create-query input-kind input-map)
           expected-type com.google.cloud.datastore.StructuredQuery]
       (isa? (type test-value) expected-type))))
  (testing "The correct creation of a query object given more than one map of properties"
    (is
     (let [input-map-vector [{:key "property1" :value "A" :type "equal"}
                             {:key "property2" :value "B" :type "less-than"}]
           input-kind test-kind
           test-value (create-query input-kind input-map-vector)
           expected-type com.google.cloud.datastore.StructuredQuery]
       (isa? (type test-value) expected-type)))))
