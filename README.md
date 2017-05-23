# azip

A Clojure library providing zippers for labeled unordered trees, such as nested
maps.

This library is modeled after `clojure.zip`, with the set of navigators
slightly adjusted.

## Usage

The coordinates of the library:
```clojure
[mg.dt/azip "0.1.0"]
```

The namespace of the library:
```clojure
(require '[mg.dt.azip :as azip])
```

Given a nested map structure
```clojure
user=> (def data {:child-a {:leaf-a "a" :leaf-b "b"} :child-b {}})
#'user/data
```
create a zipper using `map-azip` (generic `azipper` is available too)
```clojure
user=> (def rootz (azip/map-azip data))
#'user/rootz
```

Navigators for moving the zipper are called `down` and `up`
```clojure
user=> (def childz (azip/down rootz :child-a))
#'user/childz
user=> (def leafz (azip/down childz :leaf-a))
#'user/leafz
user=> (= (azip/up leafz) childz)
true
```

You can use `branch?`, `node`, `children` to interrogate the zipper state
```clojure
user=> (azip/branch? childz)
true
user=> (azip/branch? leafz)
false
user=> (azip/node rootz)
{:child-a {:leaf-a "a", :leaf-b "b"}, :child-b {}}
user=> (azip/node childz)
{:leaf-a "a", :leaf-b "b"}
user=> (azip/children rootz)
(:child-a :child-b)
```

`insert` adds a new child at the current position of the zipper, replacing
existing child if it exists
```clojure
user=> (-> rootz (azip/insert :newkey :newval)
                 (azip/node))
{:child-a {:leaf-a "a", :leaf-b "b"}, :child-b {}, :newkey :newval}
user=> (-> rootz (azip/insert :child-a :newval)
                (azip/node))
{:child-a :newval, :child-b {}}
```

`replace` and `edit` change the current node
```clojure
user=> (-> childz (azip/replace :rep)
                  (azip/up)
                  (azip/node))
{:child-a :rep :child-b {}}
user=> (-> leafz (azip/edit (fn [node arg] arg) :rep)
                 (azip/up)
                 (azip/up)
                 (azip/node))
{:child-a {:leaf-a :rep :leaf-b "b"} :child-b {}}
```

`root` is a shortcut to move the zipper to the top and return the whole data
structure
```clojure
user=> (-> leafz (azip/replace :rep) (azip/root))
{:child-a {:leaf-a :rep :leaf-b "b"} :child-b {}}
```

## License

Copyright © 2017 Mikhail Gusarov <dottedmag@dottedmag.net>

Distributed under the MIT license.
