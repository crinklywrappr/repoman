(ns repoman.core
  (:require [clojure.pprint :refer [print-table]]
            [clj-http.lite.client :as client]
            [cheshire.core :as json]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.edn :as edn])
  (:import [java.io File])
  (:gen-class))

(set! *warn-on-reflection* true)

(def endpoint "https://repology.org/api/v1/")
(def pkg-keys [:repo :subrepo :name :version :status])

(defn windows?
  "Returns a boolean indicating whether or not we are on a Windows platform"
  []
  (try
    (->> (System/getProperty "os.name")
         (re-find #"Windows")
         boolean)
    (catch Exception e
      (binding [*out* *err*]
        (println (format "Unable to determine os.name (%s)" (.getMessage e))))
      false)))

(defn config
  "Returns the config file as a file object"
  []
  (let [home (System/getProperty "user.home")]
    (io/file
     (s/join
      File/separator
      (if (windows?)
        [home ".repoman.edn"]
        [home ".config" "repoman.edn"])))))

(defn get-repos
  "Returns a vector showing the repositories we are interested in"
  [repo-file {:keys [with-repo]}]
  (if (.exists ^File repo-file)
    (try
      (concat
       (edn/read-string
        (slurp repo-file))
       with-repo)
      (catch Exception e
        (binding [*out* *err*]
          (println (format "config must be a valid edn (%s)" (.getMessage e)))
          (println "Using an empty vector \"[]\"")
          with-repo)))
    (binding [*out* *err*]
      (println "config missing.")
      with-repo)))

(defn in-repos?
  "Predicate which determines if this package is in one of our repos"
  [repos pkg]
  (some
   #(= (:repo pkg) %)
   repos))

(defn search
  "Searches repology for projects matching s"
  [repos s]
  (->> s
      (str endpoint "projects/?search=")
      client/get
      :body
      json/parse-string
      (map
       (fn [[k v]]
         (let [v (transform-keys ->kebab-case-keyword v)]
           {:prj k
            :prj-len (count k)
            :repos (count v)
            :matches (count (filter (partial in-repos? repos) v))})))))

(defn project
  "Gets project information from repology
  defaults to only those repos we are interested in"
  ([repos s]
   (project repos s false))
  ([repos s all?]
   (letfn [(showall? [xs]
             (if all?
               xs
               (filter (partial in-repos? repos) xs)))]
     (->> s
          (str endpoint "project/")
          client/get
          :body
          json/parse-string
          (transform-keys ->kebab-case-keyword)
          (map #(select-keys % pkg-keys))
          showall?
          distinct))))

(defn format-search
  "Prints search data to the screen.
  Can optionally output json"
  [repos {:keys [search json]}]
  (let [xs (repoman.core/search repos search)]
    (if json
      (println
       (json/generate-string
        (map #(dissoc % :prj-len) xs)
        {:pretty true}))
      (let [max-len (apply max (map :prj-len xs))]
        (doseq [{:keys [prj prj-len repos matches]} xs]
          (println
           (format
            "%s...%s%3s/%s repos"
            prj
            (apply str (repeat (- max-len prj-len) \.))
            matches
            repos)))))))

(defn format-project
  "Prints project data to the screen.
  Can optionally output json"
  [repos {:keys [project show-all json]}]
  (let [xs (repoman.core/project repos project show-all)]
    (if json
      (println (json/generate-string xs {:pretty true}))
      (print-table pkg-keys xs))))

(defn format-help [summary]
  (println "rp: A tiny tool for querying repology")
  (println)
  (println summary))

(def cli-options
  [["-s" "--search TERM" "Search for a package"]
   ["-p" "--project PRJ" "Show repo info for a project"]
   ["-a" "--show-all" "Show all repos with -p"]
   ["-r" "--with-repo REPO" "Include a specific repo with -s or -p"
    :default []
    :assoc-fn (fn [m k v]
                (update m k conj v))]
   ["-j" "--json" "Convert -s or -p output to json"]
   ["-c" "--config" "Displays the config file location"]
   ["-h" "--help" "Display this help string"]])

(defn -main
  [& args]
  (let [m (parse-opts args cli-options)
        opts (:options m)
        cfg (config)]
    (cond
      (:help opts) (format-help (:summary m))
      (:config opts) (println (.getAbsolutePath ^File cfg))
      :else
      (let [repos (get-repos cfg opts)]
        (cond
          (:search opts) (format-search repos opts)
          (:project opts) (format-project repos opts)
          :else opts)))))
