(ns clj-datastore.validation
  (:require [malli.core :as malli]
            [malli.error :as me]
            [clj-datastore.schema :refer :all]))

(defn validate-entity-input
  [datastore kind name entity-map]
  (let [validation-map {:datastore datastore
                        :kind kind
                        :name name
                        :entity-map entity-map}]
    (if (malli/validate EntityInput validation-map)
      true
      (-> EntityInput
          (malli/explain validation-map)
          (me/humanize)))))
