# clj-datastore

_Clojure support for Google Datastore_

## tl;dr

clj-datastore is a library written on top of [java-datastore](https://github.com/googleapis/java-datastore)
to be used with clojure datatypes. This library provides support for Google Cloud Platform Datastore.

## Project status

clj-datastore is alpha software at the moment, this doesn't mean that the project cannot be used or it has
poor quality, it does mean that probably will exist changes over the source code that could break the API.

## Installation

The project is not uploaded to [clojars](https://clojars.org/) yet, but you can add the dependencies to
your projects using the git repository as follows:
```clojure
...
 :deps {org.clojure/clojure              {:mvn/version "1.10.1"}
        github-kno3comma14/clj-datastore {:git/url "https://github.com/kno3comma14/clj-datastore.git"
                                          :sha "3684170d5fbe208ea9fd5181b616d9d981a013ca"}}
...                                          
```

## Usage

After the dependencies were added to the project, you can start using the library using ```:requiere```:

```clojure
(ns testing-project.core
  (:require [clj-datastore.operations :refer :all]))
```

At this time the project has only one namespace named ```clj-datastore.operations```. In this namespace
you can find all the basic functionalities mentioned in the following sections.

### Creating entities

The first step to create a datastore entity is create a datastore object:

```clojure
(def ds (create-datastore))
```

After this step, we can create an entity object with one of these two commands:

```clojure
(def test-entity 
  (create-entity ds 
                 "some-kind" 
                 "some-name" 
                 {:hello "Hello, " :name "clj-datastore"}))
```

or

```clojure
(def test-entity 
  (create-entity ds 
                 "some-kind" 
                 {:hello "Hello, " :name "clj-datastore"}))
```

```ds``` is the datastore that we just created, ```"some-kind"``` is a String that represents the kind of the entity,
```"some-name"``` is an optional String(if this argument is not provided the entity will be created with a random id) 
representing that gives unicity to the entity to be created, and the map *{:hello "Hello, " :name "clj-datastore"}* 
is the representation of the data to be created in the datastore.


### Upserting Entities

Upserting an entity is easy as well, we just need to use something like:
```clojure
(upsert-entity ds test-entity)
```

**Note**: Some operations like upserting require to be authenticated with GCP datatore instances. One of the easiest way
to do this is exporting the environment variable *GOOGLE_APPLICATION_CREDENTIALS* with the path to a json credential created
at Google Cloud Platform.

### Running queries

To run a query, clj-datastore provides a function named ```run-query```. This function receives the datastore, the kind of 
the datastore as a string parameter and a vector of maps representing the filters to be attached to the query. The next example 
show you how to run a query with a single property filter:

```clojure
(def input-map [{:key "property1" :value "A" :type "="}])
(def input-kind "some-kind")
(def test-query (run-query ds input-kind input-map))
```

On the other hand, you can run a query using a composite filter like:
```clojure
(def input-map [{:key "property1" :value "A" :type "="}
                {:key "property2" :value 1 :type "<"}])
(def input-kind "some-kind")
(def test-query (run-query ds input-kind input-map))
```

## Collaboration

WIP

## License

Copyright &copy; 2021 Enyert Vinas

Licensed over the term of Mozilla Public License 2.0, see LICENSE.
