(ns clj-datastore.unit.validation-test
  (:require [clojure.test :refer :all]
            [clj-datastore.validation :refer :all]
            [clj-datastore.operations :refer :all])
  (:import (com.google.cloud.datastore Datastore
                                       DatastoreOptions
                                       Entity)))

(def test-datastore (.getService (.build (.setServiceRpcFactory 
                              (.setHost 
                               (.setProjectId 
                                (DatastoreOptions/newBuilder) 
                                "project-id") 
                               "http://localhost:8080") 
                              nil))))

(deftest validate-entity-input-test
  (testing "validate-entity-input true case"
    (is
     (let [datastore test-datastore
           kind "test-kind"
           name "test-name"
           entity-map {:name "clj-datastore"}
           test-value (validate-entity-input datastore kind name entity-map)
           expected-value true]
       (= test-value expected-value))))
  (testing "validate-entity-input error data"
    (is
     (let [datastore test-datastore
           kind "test-kind"
           name nil
           entity-map {:name "clj-datastore"}
           test-value (validate-entity-input datastore kind name entity-map)
           expected-value true]
       (= test-value expected-value)))))

(deftest validate-property-filter-input-test
  (testing "validate-propery-filter-input function for accepted parameter combination"
    (is
     (let [key ""
           value "test-value"
           type "="
           datastore nil
           kind nil
           test-value (validate-property-filter-input key value type datastore kind)
           expected-value true]
       (= test-value expected-value)))
    (is
     (let [key "test-key"
           value nil
           type "is-null"
           datastore nil
           kind nil
           test-value (validate-property-filter-input key value type datastore kind)
           expected-value true]
       (= test-value expected-value)))
    (is
     (let [key "test-key"
           value nil
           type "has-ancestor"
           datastore test-datastore
           kind "test-kind"
           test-value (validate-property-filter-input key value type datastore kind)
           expected-value true]
       (= test-value expected-value)))))

(deftest validate-create-query-input-test
  (testing "validate-create-query-input function"
    (is
     (let [kind "test-kind"
           property-map [{:key "test-key" 
                          :type "has-ancestor" 
                          :datastore test-datastore
                          :kind "test-kind"}]
           test-value (validate-create-query-input kind property-map)
           expected-value true]
       (= test-value expected-value)))))

(deftest validate-run-query-input-test
  (testing "validate-run-query-input function"
    (is
     (let [datastore test-datastore
           kind "test-kind"
           property-map [{:key "test-key" 
                          :type "has-ancestor" 
                          :datastore test-datastore
                          :kind "test-kind"}]
           test-value (validate-run-query-input datastore kind property-map)
           expected-value true]
       (println test-value)
       (= test-value expected-value)))))

(deftest validate-upsert-entity-input-test
  (testing "validate-upsert-entity-input function"
    (is
     (let [datastore test-datastore
           kind "test-kind"
           name "test-name"
           property-map {:a "A"}
           entity (create-entity datastore kind name property-map)
           test-value (validate-upsert-entity-input datastore entity)
           expected-value true]
       (= test-value expected-value)))))
