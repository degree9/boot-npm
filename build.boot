(set-env!
 :dependencies  '[[boot/core           "2.8.2"]
                  [cheshire            "5.8.1"]
                  [degree9/boot-semver "1.8.0" :scope "test"]
                  [degree9/boot-io     "1.3.0"]
                  [degree9/boot-exec   "1.1.0"]]
 :resource-paths   #{"src"})

(require
  '[degree9.boot-npm :refer :all]
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
