(set-env!
 :dependencies  '[[org.clojure/clojure                 "1.8.0"]
                  [boot/core                           "2.7.1"]
                  [adzerk/bootlaces                    "0.1.13" :scope "test"]
                  [cheshire                            "5.7.0"]
                  [degree9/boot-semver                 "1.4.3" :scope "test"]
                  [degree9/boot-exec                   "0.6.0"]]
 :resource-paths   #{"src"})

(require
 '[adzerk.bootlaces :refer :all]
 '[degree9.boot-semver :refer :all])

(task-options!
  target {:dir #{"target"}}
  pom {:project 'degree9/boot-npm
       :version (get-version)
       :description "boot-clj task for wrapping npm"
       :url         "https://github.com/degree9/boot-npm"
       :scm         {:url "https://github.com/degree9/boot-npm"}})

(deftask develop
  "Build boot-npm for development."
  []
  (comp
   (watch)
   (version :develop true
            :minor 'inc
            :patch 'zero
            :pre-release 'snapshot)
   (target)
   (build-jar)))

(deftask deploy
  "Build boot-npm and deploy to clojars."
  []
  (comp
   (version)
   (target)
   (build-jar)
   (push-release)))
