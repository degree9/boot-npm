(ns degree9.boot-npm
  (:require [boot.core :as boot]
            [boot.util :as util]
            [degree9.boot-exec :as ex]
            [clojure.java.io :as io]
            [cheshire.core :refer :all]))

;; Helper Functions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;https://github.com/metosin/ring-swagger/blob/1c5b8ab7ad7a5735624986bbb6b288aaf168d407/src/ring/swagger/common.clj#L53-L73
(defn- deep-merge
  "Recursively merges maps.
   If the first parameter is a keyword it tells the strategy to
   use when merging non-map collections. Options are
   - :replace, the default, the last value is used
   - :into, if the value in every map is a collection they are concatenated
     using into. Thus the type of (first) value is maintained."
  {:arglists '([strategy & values] [values])}
  [& values]
  (let [[values strategy] (if (keyword? (first values))
                            [(rest values) (first values)]
                            [values :replace])]
    (cond
      (every? map? values)
      (apply merge-with (partial deep-merge strategy) values)

      (and (= strategy :into) (every? coll? values))
      (reduce into values)

      :else
      (apply merge values))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Private Tasks ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(boot/deftask node-modules
  "Include project node_modules folder in fileset."
  []
  (let [tmp (boot/tmp-dir!)
        npmdir (io/file tmp "node_modules")]
    (boot/with-pre-wrap fileset
      (when (.exists (io/file "./node_modules"))
        (util/info "Adding node_modules to fileset. \n")
        (boot/sync! npmdir "./node_modules"))
      (-> fileset (boot/add-resource tmp)))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Public Tasks ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(boot/deftask npm
  "boot-clj wrapper for npm"
  [p package     VAL     str      "An edn file containing a package.json map."
   i install     FOO=BAR {kw str} "Dependency map."
   d develop             bool     "Include development dependencies with packages."
   r dry-run             bool     "Report what changes npm would have made. (usefull with boot -vv)"
   g global              bool     "Opperates in global mode. Packages are installed to npm prefix."
   c cache-key   VAL     kw       "Optional cache key for when npm is used with multiple dependency sets."
   _ include             bool     "Include package.json in fileset output."
   _ pretty              bool     "Pretty print generated package.json file"]
  (let [npmjsonf  (:package   *opts* "./package.edn")
        deps      (:install   *opts*)
        dev       (:develop   *opts*)
        global    (:global    *opts*)
        cache-key (:cache-key *opts* ::cache)
        managed?  (:managed   *opts*)
        include?  (:include   *opts*)
        pretty?   (:pretty    *opts*)
        tmp       (boot/cache-dir! cache-key)
        tmp-path  (.getAbsolutePath tmp)
        npmjsonc  (when (.exists (io/file npmjsonf)) (read-string (slurp npmjsonf)))
        npmjson   (generate-string (deep-merge {:name "boot-npm" :version "0.1.0" :dependencies deps} npmjsonc) {:pretty pretty?})
        args      (cond-> ["install"]
                    (not dev) (conj "--production")
                    dry-run   (conj "--dry-run")
                    global    (conj "--global"))]
    (comp
      (node-modules)
      (ex/properties :contents npmjson :directory tmp-path :file "package.json" :include include?)
      (ex/exec :process "npm" :arguments args :directory tmp-path :local "node_modules/npm/bin" :include true))))

(boot/deftask exec
  "Exec wrapper for npm modules"
  [m module         VAL  str      "NPM node module."
   p process        VAL  str      "CLI executable of the npm module. (if different than module name)"
   d version        VAL  str      "Module version string."
   a arguments      VAL  [str]    "List of arguments to pass to cli process."
   g global              bool     "Opperates in global mode. Packages are installed to global location."
   c cache-key      VAL  kw       "Optional cache key for when npm is used with multiple dependency sets."]
  (let [module    (:module    *opts*)
        process   (:process   *opts* module)
        version   (:version   *opts* "*")
        args      (:arguments *opts*)
        global    (:global    *opts*)
        cache-key (:cache-key *opts* ::cache)
        install   (assoc {} (keyword module) version)
        tmp       (boot/cache-dir! cache-key)
        tmp-path  (.getAbsolutePath tmp)]
    (comp
      (npm :install install :cache-key cache-key :global global)
      (ex/exec :process process :arguments args :cache-key cache-key :local (str "node_modules/" module "/bin") :include true))))
