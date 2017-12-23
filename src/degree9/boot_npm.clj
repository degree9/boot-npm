(ns degree9.boot-npm
  (:require [boot.core :as boot]
            [boot.util :as util]
            [degree9.boot-exec :as ex]
            [degree9.boot-io :as file]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [cheshire.core :refer :all]))

;; Helper Functions ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- fs-sync [tmp]
  (boot/with-pre-wrap fileset
    (apply boot/sync! tmp (boot/input-dirs fileset))
    fileset))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Public Tasks ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(boot/deftask node-modules
  "Include project node_modules folder in fileset."
  []
  (file/add-directory :source "./node_modules/" :destination "./node_modules/" :optional true))

(boot/deftask npm
  "boot-clj wrapper for npm"
  [p package   VAL str      "A package.json file."
   i install   VAL [str]    "List of dependencies to install."
   d develop       bool     "Include development dependencies with packages."
   r dry-run       bool     "Report what changes npm would have made. (usefull with boot -vv)"
   g global        bool     "Opperates in global mode. Packages are installed to npm prefix."
   c cache-key VAL kw       "Optional cache key for when npm is used with multiple dependency sets."
   _ pretty        bool     "Pretty print generated package.json file"]
  (let [npmjson   (:package   *opts* "./package.json")
        install   (:install   *opts*)
        dev       (:develop   *opts*)
        global    (:global    *opts*)
        cache-key (:cache-key *opts* ::cache)
        pretty?   (:pretty    *opts*)
        tmp       (boot/cache-dir! cache-key)
        tmp-path  (.getAbsolutePath tmp)
        npmf      (io/file npmjson)
        args      (cond-> ["install"]
                    install   (concat install)
                    (not dev) (conj "--production")
                    dry-run   (conj "--dry-run")
                    global    (conj "--global"))]
    (comp
      (file/add-file :source npmjson :destination "./package.json" :optional true)
      (fs-sync tmp)
      (ex/exec :process "npm" :arguments args :directory tmp-path :local "node_modules/npm/bin"))))

(boot/deftask exec
  "Exec wrapper for npm modules"
  [m module         VAL str      "NPM node module."
   p process        VAL str      "CLI executable of the npm module. (if different than module name)"
   d version        VAL str      "Module version string."
   a arguments      VAL [str]    "List of arguments to pass to cli process."
   g global             bool     "Opperates in global mode. Packages are installed to global location."
   c cache-key      VAL kw       "Optional cache key for when npm is used with multiple dependency sets."
   e exclude        VAL #{regex} "Exclude files from being added to the fileset."]
  (let [module     (:module    *opts*)
        process    (:process   *opts* module)
        version    (:version   *opts* "latest")
        args       (:arguments *opts*)
        global     (:global    *opts*)
        cache-key  (:cache-key *opts* ::cache)
        exclude    (:exclude   *opts*)
        install    [(str module "@" version)]
        tmp        (boot/tmp-dir!)
        tmp-path   (.getAbsolutePath tmp)
        cache      (boot/cache-dir! cache-key)
        cache-path (.getAbsolutePath cache)]
    (comp
      ;; Install NPM Module to Cache
      (npm :install install :cache-key cache-key :global global)
      ;; Sync Input with Temp Directory
      (fs-sync tmp)
      ;; Execute Module from Cache with Temp Working Directory
      (ex/exec :process process :arguments args :directory tmp-path :local (str cache-path "/node_modules/" module "/bin") :exclude exclude))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
