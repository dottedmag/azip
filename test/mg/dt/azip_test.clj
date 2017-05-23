(ns mg.dt.azip-test
  (:require [clojure.test :refer :all]
            [mg.dt.azip :refer :all])
  (:refer-clojure :exclude (replace remove)))

(def leaf-a "a")
(def leaf-b "b")

(def middle-a {:leaf-a leaf-a :leaf-b leaf-b})
(def middle-b {})

(def top {:middle-a middle-a :middle-b middle-b})

(def z (map-azip top))
(def az (down z :middle-a))
(def aaz (down az :leaf-a))

(deftest test-top-node
  (testing "node"
    (is (= (node z) top)))
  (testing "path"
    (is (= (path z) [])))
  (testing "branch?"
    (is (branch? z)))
  (testing "children"
    (is (= (sort (children z)) [:middle-a :middle-b])))
  (testing "down?"
    (is (down? z :middle-a))
    (is (not (down? z :missing))))
  (testing "down"
    (is (= (node (down z :middle-a)) middle-a))
    (is (= (down z :missing) nil)))
  (testing "up"
    (is (= (up z) nil)))
  (testing "root"
    (is (= (root z) top)))
  (testing "insert"
    (is (= (root (insert z :new :newval))
           {:middle-a middle-a :middle-b middle-b :new :newval}))
    (is (= (root (insert z :middle-a :newval))
           {:middle-a :newval :middle-b middle-b})))
  (testing "replace"
    (is (= (root (replace z {})) {})))
  (testing "remove"
    (is (= (remove z) nil))))

(deftest test-middle-node
  (testing "node"
    (is (= (node az) middle-a)))
  (testing "path"
    (is (= (path az) [[top :middle-a]])))
  (testing "branch?"
    (is (branch? az)))
  (testing "children"
    (is (= (sort (children az)) [:leaf-a :leaf-b])))
  (testing "down?"
    (is (down? az :leaf-a))
    (is (not (down? az :missing))))
  (testing "down"
    (is (= (node (down az :leaf-a)) leaf-a))
    (is (= (down az :missing) nil)))
  (testing "up"
    (is (= (node (up az)) top)))
  (testing "root"
    (is (= (root az) top)))
  (testing "insert"
    (is (= (root (insert az :new :newval))
           {:middle-a {:leaf-a leaf-a :leaf-b leaf-b :new :newval}
            :middle-b middle-b}))
    (is (= (root (insert az :leaf-a :newval))
           {:middle-a {:leaf-a :newval :leaf-b leaf-b} :middle-b middle-b})))
  (testing "replace"
    (is (= (root (replace az {})) {:middle-a {} :middle-b {}})))
  (testing "remove"
    (is (= (root (remove az)) {:middle-b {}}))))

(deftest test-leaf-node
  (testing "node"
    (is (= (node aaz) leaf-a)))
  (testing "path"
    (is (= (path aaz) [[top :middle-a] [middle-a :leaf-a]])))
  (testing "branch?"
    (is (not (branch? aaz))))
  (testing "down"
    (is (= (down aaz :foo) nil)))
  (testing "up"
    (is (= (node (up aaz)) middle-a)))
  (testing "root"
    (is (= (root aaz) top)))
  (testing "replace"
    (is (= (root (replace aaz :foo))
           {:middle-a {:leaf-a :foo :leaf-b "b"} :middle-b {}})))
  (testing "remove"
    (is (= (root (remove aaz))
           {:middle-a {:leaf-b "b"} :middle-b {}}))))

(defn- testedit [node arg]
  (is (= arg :testarg))
  (is (= node top))
  :ret)

(deftest test-edit
  (is (= (root (edit az (constantly 42)))
         {:middle-a 42 :middle-b {}}))
  (is (= (root (edit z testedit :testarg)) :ret)))
