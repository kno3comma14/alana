(ns clj-datastore.unit.validation-test
  (:require [clojure.test :refer :all]
            [clj-datastore.validation :refer :all])
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
  (testing "validate-entity-input positive case"
    (is
     (let [datastore test-datastore
           kind "test-kind"
           name "test-name"
           entity-map {:name "clj-datastore"}
           test-value (validate-entity-input datastore kind name entity-map)]
       (= test-value true))))
  (testing "validate-entity-input error data"
    (is
     (let [datastore test-datastore
           kind "test-kind"
           name nil
           entity-map {:name "clj-datastore"}
           test-value (validate-entity-input datastore kind name entity-map)
           expected-value {:name ["should be a string"]}]
       (= test-value expected-value)))))
