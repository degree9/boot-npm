(set-env!
 :dependencies  '[[org.clojure/clojure                 "1.8.0"]
                  [boot/core                           "2.7.2"]
                  [cheshire                            "5.7.1"]
                  [degree9/boot-semver                 "1.7.0" :scope "test"]
                  [degree9/boot-exec                   "1.0.0"]]
 :resource-paths   #{"src"})

(require
 '[degree9.boot-semver :refer :all])

(task-options!
  target {:dir #{"target"}}
  pom {:project 'degree9/boot-npm
       :description "boot-clj task for wrapping npm"
       :url         "https://github.com/degree9/boot-npm"
       :scm         {:url "https://github.com/degree9/boot-npm"}})

(deftask develop
  "Build boot-npm for development."
  []
  (comp
   (version :develop true
            :minor 'inc
            :patch 'zero
            :pre-release 'snapshot)
   (watch)
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
