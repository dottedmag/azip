(ns ^{:doc "Functional hierarchical zipper on associative structures"
      :author "Mikhail Gusarov"}
  mg.dt.azip
  (:refer-clojure :exclude (replace remove)))

(defn azipper
  "Creates a new associative zipper structure.

  branch? is a fn that, given a node, returns true if it can
  have children, even if it currently doesn't.

  children is a fn, that, given a branch node, returns a seq
  of keys of its children.

  get-child is a fn, that, given a branch node and a key, returns
  a child of node for the key.

  set-child is a fn, that, given a branch node, a key and a value,
  returns a new node with a child for the key set to the value.

  del-child is a fn, that, given a branch node and a key, returns
  a new node with a child for the key deleted."
  [branch? children get-child set-child del-child root]
  ^{::branch? branch? ::children children ::get-child get-child
    ::set-child set-child ::del-child del-child}
  [root []])

(defn map-azip
  "Returns a zipper for nested maps, given a root map"
  [root]
  (azipper map? keys get assoc dissoc root))

(defn node
  "Returns the node at loc"
  [loc] (loc 0))

(defn- call-meta [fn-name loc & args]
  (apply (fn-name (meta loc)) args))

(defn path
  "Returns a seq of all nodes leading to this loc, with the keys taken"
  [loc] (loc 1))

(defn branch?
  "Returns true if the node at loc is a branch"
  [loc]
  (call-meta ::branch? loc (node loc)))

(defn children
  "Returns a seq of the children of the node at loc, which must be a branch"
  [loc]
  (if (branch? loc)
    (call-meta ::children loc (node loc))
    (throw (Exception. "called children on leaf node"))))

(defn down
  "Returns the loc of the child for the key at this loc, or nil if no
  children or this child does not exist"
  [[node path :as loc] key]
  (when (branch? loc)
    (let [dn (call-meta ::get-child loc node key)]
      (when dn
        (with-meta [dn (conj path [node key])] (meta loc))))))

(defn up
  "Returns the loc of the parent of the node at this loc, or nil if at
  the top"
  [[node path :as loc]]
  (let [[up key] (peek path)]
    (when up
      (let [oldnode (call-meta ::get-child loc up key)]
        (with-meta
          [(if (identical? node oldnode)
             up
             (call-meta ::set-child loc up key node))
           (pop path)]
          (meta loc))))))

(defn root
  "zips all the way up and returns the root node, reflecting any changes"
  [loc]
  (if (empty? (path loc))
    (node loc)
    (recur (up loc))))

(defn replace
  "Replaces the node at this loc without moving"
  [[_ path :as loc] node]
  (with-meta [node path] (meta loc)))

(defn edit
  "Replaces the node at this loc with the value of (f node args)"
  [loc f & args]
  (replace loc (apply f (node loc) args)))

(defn remove
  "Removes the node at loc, returning the loc of the parent"
  [[node path :as loc]]
  (let [[up key] (peek path)]
    (when up
      (with-meta
        [(call-meta ::del-child loc up key) (pop path)]
        (meta loc)))))
