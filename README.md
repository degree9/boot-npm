# boot-npm
[![Clojars Project](https://img.shields.io/clojars/v/degree9/boot-npm.svg)](https://clojars.org/degree9/boot-npm)

Node Package Manager (NPM) wrapper for [boot-clj][1].

* Provides `npm` task for installing node modules.
* Provides `exec` task for executing node modules. (auto-intalls local module)

> The following outlines basic usage of the task, extensive testing has not been done.
> Please submit issues and pull requests!

## Usage

Add `boot-npm` to your `build.boot` dependencies and `require` the namespace:

```clj
(set-env! :dependencies '[[degree9/boot-npm "X.Y.Z" :scope "test"]])
(require '[degree9.boot-npm :refer :all])
```

Use npm to install bower:

```bash
boot {task} {args}
```

Use in a wrapper task:

```clojure
(boot/deftask mytask
  ""
  [...]
  (let [...]
    (comp
      {task-fn})))
```

##Task Options

{options-description}

```clojure
{options}
```

{options-notes}

[1]: https://github.com/boot-clj/boot
