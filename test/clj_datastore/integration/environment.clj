(ns clj-datastore.integration.environment
  (:require [clj-datastore.operations :refer :all]))

(def generated-data [{:name "Alana"  :gender "F" :created (now) :first-chapter-appearance 1}
                     {:name "Marko"  :gender "M" :created (now) :first-chapter-appearance 1}
                     {:name "Hazel"  :gender "F" :created (now) :first-chapter-appearance 1}
                     {:name "Izabel" :gender "F" :created (now) :first-chapter-appearance 2}
                     {:name "Klara"  :gender "F" :created (now) :first-chapter-appearance 6}
                     {:name "Barr"   :gender "M" :created (now) :first-chapter-appearance 6}
                     {:name "Yuma"   :gender "F" :created (now) :first-chapter-appearance 15}])

(def datastore (create-datastore))

(def kind "some-saga-characters")

(def entities 
  (reduce (fn [acc, x]
            (conj acc (create-entity datastore kind x)))
          []
          generated-data))

(defn- now
  []
  (java.time.LocalDateTime/now))

(defn init-gcp-environment
  "This function setup the initial environment for integration tests"
  []
  (doseq [entity entities]
    (upsert-entity datastore entity)))

(defn teardown-gcp-environment
  "This function teardown the initial environment for integration tests"
  []
  (doseq [entity entities]
    (.delete datastore entity)))

(defn wrap-setup
  "Wrapper to be used for test fixtures"
  [f]
  (init-gcp-environment)
  (f)
  (teardown-gcp-environment))
